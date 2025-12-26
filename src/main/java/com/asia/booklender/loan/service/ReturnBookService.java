package com.asia.booklender.loan.service;

import com.asia.booklender.loan.entity.Loan;
import com.asia.booklender.member.entity.Member;
import com.asia.booklender.shared.exception.AccessDeniedException;
import com.asia.booklender.shared.exception.ResourceNotFoundException;

/**
 *  Service responsible for returning borrowed books.
 */
public interface ReturnBookService {
    /**
     * Returns a borrowed book.
     *
     * @param loanId the ID of the loan to return
     * @param member the authenticated member
     *
     * @return the updated Loan entity with returnedAt timestamp
     *
     * @throws ResourceNotFoundException if loan or book not found
     * @throws AccessDeniedException if member tries to return another member's loan
     * @throws IllegalStateException if book has already been returned
     */
    Loan returnBook(Long loanId, Member member);
}
