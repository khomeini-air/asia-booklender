package com.asia.booklender.loan.service.impl;

import com.asia.booklender.book.entity.Book;
import com.asia.booklender.book.repository.BookRepository;
import com.asia.booklender.loan.entity.Loan;
import com.asia.booklender.loan.exception.LoanAlreadyReturnedException;
import com.asia.booklender.loan.repository.LoanRepository;
import com.asia.booklender.loan.service.ReturnBookService;
import com.asia.booklender.member.entity.Member;
import com.asia.booklender.shared.exception.AccessDeniedException;
import com.asia.booklender.shared.exception.ResourceNotFoundException;
import com.asia.booklender.shared.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReturnBookServiceImpl implements ReturnBookService {
    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;

    @Override
    @Transactional
    public Loan returnBook(Long loanId, Member member) {
        log.debug("Processing return for loanId: {} by member: {}", loanId, member);

        // Validate if the return request can be granted.
        Loan loan = validate(loanId, member.getId());

        // Increment the book inventory pessimistically
        incrementBookInventory(loan.getBook().getId());

        // Set the 'returnedAt' and save
        Loan returnedLoan = markLoanAsReturned(loan);

        // Log the return result
        logReturnStatus(returnedLoan);

        return returnedLoan;
    }

    /**
     * Validates if the return request can be granted.
     *
     * Checks:
     * 1. Loan exists
     * 2. Member is authorized to return this loan
     * 3. Book hasn't already been returned
     */
    private Loan validate(Long loanId, Long memberId) {
        // Retrieve the loan
        Loan loan = findLoanById(loanId);

        // validate member authorization
        validateAuthorization(loan, memberId);

        // Ensure the loan is not yet returned, else throw {@code IllegalStateException}
        validateReturnSatus(loan);

        return loan;
    }

    private Loan findLoanById(Long loanId) {
        return loanRepository
                .findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found with id: " + loanId));
    }

    /**
     * Validate if current user is authorized to return the book.
     * Admin can return any loan.
     * Members can only return their own loan.
     *
     * @param loan loan to return.
     * @param memberId the member id of the current user
     */
    private void validateAuthorization(Loan loan, Long memberId) {
        Long loanMemberId = loan.getMember().getId();

        if (!SecurityUtil.currentUser().isAdmin()
                && !loanMemberId.equals(memberId)) {
            log.warn("Member {} attempted to return loan {} belonging to member {}", memberId, loan.getId(), loanMemberId);
            throw new AccessDeniedException("You can only return your own loans");
        }
    }

    /**
     * Validate that the loan is not yet returned
     *
     * @param loan loan to return.
     */
    private void validateReturnSatus(Loan loan) {
        if (loan.getReturnedAt() != null) {
            log.warn("Attempted to return already a book that has been returned: {}", loan.getId());
            throw new LoanAlreadyReturnedException(String.format("Loan id %s, book id %s has already been returned",
                    loan.getId(),
                    loan.getBook().getId()));
        }
    }

    /**
     * Increments book inventory using pessimistic lock to guarantee consistency.
     *
     * @param bookId the book id.
     */
    private void incrementBookInventory(Long bookId) {
        Book book = bookRepository.findWithLockById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));

        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);
    }

    /**
     * Marks a loan as returned with current timestamp.
     *
     * @param loan the book loan
     */
    private Loan markLoanAsReturned(Loan loan) {
        loan.setReturnedAt(Instant.now());
        return loanRepository.save(loan);
    }

    /**
     * Logs whether the book was returned on time or late.
     *
     * @param loan the book loan that has been returned successfully
     */
    private void logReturnStatus(Loan loan) {
        if (loan.getReturnedAt().isAfter(loan.getDueAt())) {
            log.info("Book returned late. Loan ID: {}, Due: {}, Returned: {}",
                    loan.getId(), loan.getDueAt(), loan.getReturnedAt());
        } else {
            log.info("Book returned on time. Loan ID: {}", loan.getId());
        }

        log.debug("Loan returned successfully: Loan: {}", loan);
    }
}
