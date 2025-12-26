package com.asia.booklender.loan.service.impl;

import com.asia.booklender.book.entity.Book;
import com.asia.booklender.book.repository.BookRepository;
import com.asia.booklender.loan.config.LoanRulesConfig;
import com.asia.booklender.loan.entity.Loan;
import com.asia.booklender.loan.exception.BookNotAvailableException;
import com.asia.booklender.loan.exception.MaxLoansExceededException;
import com.asia.booklender.loan.exception.OverdueLoanException;
import com.asia.booklender.loan.repository.LoanRepository;
import com.asia.booklender.loan.service.BorrowBookService;
import com.asia.booklender.member.entity.Member;
import com.asia.booklender.shared.enums.LockStrategy;
import com.asia.booklender.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class BorrowBookServiceImpl implements BorrowBookService {
    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final LoanRulesConfig borrowingRules;

    @Override
    @Transactional
    public Loan borrow(Long bookId, Member member) {
        log.debug("Processing borrow request for book: {} by member: {}", bookId, member);

        // Validate against loan rules
        validateAgainstRules(member.getId());

        // Decrement book inventory
        Book book = decrementBookInventory(bookId);

        // Create and save the loan
        Loan loan = createLoanRecord(book, member);

        log.info("Book borrowed successfully. Loan ID: {}, Due date: {}", loan.getId(), loan.getDueAt());
        log.debug("Loan attempt successful. Loan: {}", loan);

        return loan;
    }

    /**
     * Validates that a member is eligible to borrow books.
     * <p>
     * Checks:
     * <ol>
     * <li>Has not exceeded maximum active loans</li>
     * <li>Has no overdue loans (if enforcement enabled)</li>
     * </ol>
     */
    private void validateAgainstRules(Long memberId) {
        // Check if the member's loan exceeds the max number of loan limit
        validateMaxLoan(memberId);

        // Check if the member has overdue loan
        validateOverdue(memberId);
    }

    private void validateMaxLoan(Long memberId) {
        int activeLoans = loanRepository.countByMemberIdAndReturnedAtIsNull(memberId);

        if (activeLoans >= borrowingRules.getMaxActiveLoans()) {
            log.warn("Member {} has reached maximum active loans limit ({}/{})",
                    memberId,
                    activeLoans,
                    borrowingRules.getMaxActiveLoans());
            throw new MaxLoansExceededException(
                    String.format("Member has reached maximum active loans limit (%d)",
                            borrowingRules.getMaxActiveLoans())
            );
        }
    }

    private void validateOverdue(Long memberId) {
        if (borrowingRules.isEnforceOverdueRestriction()
                && loanRepository.existsByMemberIdAndReturnedAtIsNullAndDueAtBefore(memberId, Instant.now())) {
            log.warn("Member {} has overdue loans and cannot borrow new books", memberId);
            throw new OverdueLoanException("Member has overdue loans and cannot borrow new books");
        }
    }

    /**
     * Decrements book inventory using adaptive hybrid locking strategy.
     * <p>
     * Strategy:
     * <ol>
     * <li>Quick check without lock (fast fail if unavailable)</li>
     * <li>Determine locking strategy based on inventory threshold</li>
     * <li>Apply appropriate lock and decrement inventory</li>
     * </ol>
     */
    private Book decrementBookInventory(Long bookId) {
        Book book = getAndCheckAvailability(bookId);
        LockStrategy lockStrategy = determineLock(book);

        return decrement(book, lockStrategy);
    }

    private Book getAndCheckAvailability(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));

        if (book.getAvailableCopies() <= 0) {
            log.warn("Book {} is not available for borrowing (0 copies available) - fast fail", bookId);
            throw new BookNotAvailableException("Book is not available for borrowing");
        }

        return book;
    }

    private LockStrategy determineLock(Book book) {
        int threshold = borrowingRules.getLowInventoryThreshold();
        boolean usePessimistic = book.getAvailableCopies() <= threshold;

        LockStrategy strategy = usePessimistic ? LockStrategy.PESSIMISTIC : LockStrategy.OPTIMISTIC;

        log.debug("Book {} has {} copies (threshold: {}), using {} lock",
                book.getId(), book.getAvailableCopies(), threshold, strategy);

        return strategy;
    }

    private Book decrement(Book book, LockStrategy strategy) {
        if (strategy == LockStrategy.PESSIMISTIC) {
            return decrementWithPessimisticLock(book);
        } else {
            return decrementWithOptimisticLock(book);
        }
    }

    /**
     * Decrements inventory using pessimistic lock (database-level lock).
     * <p>Used for low inventory books to guarantee consistency.
     * <p>May need retry on {@code OptimisticLockingFailureException}.
     */
    private Book decrementWithPessimisticLock(Book book) {
        Book lockedBook = bookRepository.findWithLockById(book.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + book.getId()));

        if (lockedBook.getAvailableCopies() <= 0) {
            log.warn("Book {} became unavailable after acquiring lock", lockedBook.getId());
            throw new BookNotAvailableException("Book is not available for borrowing");
        }

        return decrementAndSave(lockedBook);
    }

    /**
     * Decrements inventory using optimistic lock.
     * Used for high inventory books for better performance.
     */
    @Retryable(
            retryFor = {OptimisticLockingFailureException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 100)
    )
    private Book decrementWithOptimisticLock(Book book) {
        // Decrement and immediately save it. Retry optimistically upon concurrent conflict.
        return decrementAndSave(book);
    }

    private Book decrementAndSave(Book book) {
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        return bookRepository.save(book);
    }

    /**
     * Creates and saves the loan record with automatic due date calculation.
     */
    private Loan createLoanRecord(Book book, Member member) {
        Loan loan = Loan
                .builder()
                .book(book)
                .member(member)
                .borrowedAt(Instant.now())
                .dueAt(Instant.now().plus(borrowingRules.getLoanDurationDays(), ChronoUnit.DAYS))
                .createdBy(member.getEmail())
                .build();

        return loanRepository.save(loan);
    }
}
