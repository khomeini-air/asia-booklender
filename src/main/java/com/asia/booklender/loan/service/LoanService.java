package com.asia.booklender.loan.service;

import com.asia.booklender.loan.dto.LoanDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service layer for managing book loans.
 * Handles borrowing and returning books with configurable business rules.
 */
public interface LoanService {
    /**
     * Borrows a book loan detail for the authenticated member.
     *
     * @param bookId book id
     * @return {@link LoanDto} the loan detail information
     */
    LoanDto loan(Long bookId);

    /**
     * Returns a book loan by the loan id.
     *
     * @param loanId the loan id
     * @return {@code LoanDto} the loan details
     */
    LoanDto returnBook(Long loanId);

    /**
     * Retrieve my active loans
     *
     * @param isActive if true, returns only active loans; if false, returns all loans
     * @param pageable pagination
     * @return {@code Page<LoanDto>} paged Loan based on activity.
     */
    Page<LoanDto> findMy(boolean isActive, Pageable pageable);

    /**
     * Retrieve my book loan based on the loan id.
     * <p>
     *     Member can only access his own loan.
     *     Admin can access any loans.
     * </p>
     *
     * @param loanId the loan id.
     * @return {@code LoanDto}
     */
    LoanDto findById(Long loanId);

    /**
     * Retrieve all book loans within the given page.
     *
     * @param page the page parameter
     * @return the {@code Page<LoanDto>}
     */
    Page<LoanDto> findAll(Pageable page);
}
