package com.asia.booklender.loan.service;

import com.asia.booklender.loan.entity.Loan;
import com.asia.booklender.loan.exception.BookNotAvailableException;
import com.asia.booklender.loan.exception.MaxLoansExceededException;
import com.asia.booklender.loan.exception.OverdueLoanException;
import com.asia.booklender.member.entity.Member;
import com.asia.booklender.shared.exception.ResourceNotFoundException;

/**
 * * Service responsible for borrowing books
 */
public interface BorrowBookService {
    /**
     * Borrows a book for a member using adaptive locking strategy.
     *
     * @param bookId the ID of the book to borrow
     * @param member the authenticated member
     *
     * @return the created Loan entity
     *
     * @throws ResourceNotFoundException if book or member not found
     * @throws MaxLoansExceededException if member has reached loan limit
     * @throws OverdueLoanException if member has overdue loans
     * @throws BookNotAvailableException if no copies available
     */
    Loan borrow(Long bookId, Member member);
}
