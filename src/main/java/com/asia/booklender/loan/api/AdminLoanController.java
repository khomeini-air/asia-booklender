package com.asia.booklender.loan.api;

import com.asia.booklender.loan.dto.LoanDto;
import com.asia.booklender.loan.service.LoanService;
import com.asia.booklender.shared.api.ApiPaginationResponse;
import com.asia.booklender.shared.api.PaginationRequest;
import com.asia.booklender.shared.exception.AccessDeniedException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/loans")
@RequiredArgsConstructor
@Validated
public class AdminLoanController {
    private final LoanService loanService;

    /**
     * API to return all book loans within the given page parameter
     *
     * @param request pagination request parameter
     * @return all book loans as per {@link PaginationRequest}
     *
     * @throws AccessDeniedException for Members
     */
    @GetMapping
    public ResponseEntity<ApiPaginationResponse<List<LoanDto>>> getAll(@Valid @ModelAttribute PaginationRequest request) {
        Page<LoanDto> allLoan = loanService.findAll(request.toPageable());

        return ResponseEntity.ok(ApiPaginationResponse.success(allLoan));
    }
}
