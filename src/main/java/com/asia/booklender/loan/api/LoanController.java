package com.asia.booklender.loan.api;

import com.asia.booklender.loan.dto.LoanDto;
import com.asia.booklender.loan.exception.MaxLoansExceededException;
import com.asia.booklender.loan.exception.OverdueLoanException;
import com.asia.booklender.loan.service.LoanService;
import com.asia.booklender.shared.api.ApiPaginationResponse;
import com.asia.booklender.shared.api.ApiResponse;
import com.asia.booklender.shared.api.PaginationRequest;
import com.asia.booklender.shared.exception.AccessDeniedException;
import com.asia.booklender.shared.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing book loans.
 */
@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    /**
     * Borrows a book for the authenticated member.
     * The member ID is automatically extracted from the JWT token.
     *
     * @param bookId the ID of the book to borrow
     * @return the created book loan
     *
     * @throws ResourceNotFoundException if book not found
     * @throws MaxLoansExceededException if loan limit exceeded
     * @throws OverdueLoanException      if member has overdue loans
     * @throws ResourceNotFoundException if no copies available
     */
    @PostMapping("/{bookId}")
    public ResponseEntity<ApiResponse<LoanDto>> loan(@PathVariable Long bookId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(loanService.loan(bookId)));
    }

    /**
     * Returns a borrowed book.
     * Members can only return their own loans.
     * Admins can return any loan.
     *
     * @param loanId the ID of the loan to return
     *
     * @return the updated book loan
     *
     * @throws ResourceNotFoundException if loan not found
     * @throws AccessDeniedException     if member tries to return another member's loan
     * @throws IllegalStateException     if book already returned
     */
    @PostMapping("/return/{loanId}")
    public ResponseEntity<ApiResponse<LoanDto>> returnLoan(@PathVariable Long loanId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(loanService.returnBook(loanId)));
    }

    /**
     * Retrieves loans for the authenticated member.
     *
     * @param activeOnly filter to show only active loans
     *
     * @return List of loans within the given page request.
     */
    @GetMapping("/my")
    public ResponseEntity<ApiPaginationResponse<List<LoanDto>>> getMyLoans(
            @Valid @ModelAttribute PaginationRequest pageRequest,
            @RequestParam(required = false, defaultValue = "false") boolean activeOnly
            ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiPaginationResponse.success(loanService.findMy(activeOnly, pageRequest.toPageable())));
    }

    /**
     * Retrieves a specific loan by ID.
     * Members can only view their own loans.
     * Admins can view any loan.
     *
     * @param loanId the ID of the loan to retrieve
     *
     * @return ResponseEntity with Loan and HTTP 200 (OK)
     *
     * @throws ResourceNotFoundException if loan not found
     * @throws AccessDeniedException     if member tries to view another member's loan
     */
    @GetMapping("/{loanId}")
    public ResponseEntity<ApiResponse<LoanDto>> getLoanById(@PathVariable Long loanId) {
        return ResponseEntity.ok(ApiResponse.success(loanService.findById(loanId)));
    }
}
