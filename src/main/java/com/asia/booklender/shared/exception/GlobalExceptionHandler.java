package com.asia.booklender.shared.exception;

import com.asia.booklender.loan.exception.BookNotAvailableException;
import com.asia.booklender.loan.exception.LoanAlreadyReturnedException;
import com.asia.booklender.loan.exception.MaxLoansExceededException;
import com.asia.booklender.loan.exception.OverdueLoanException;
import com.asia.booklender.shared.api.ApiResponse;
import com.asia.booklender.shared.api.Result;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handle failed authentication.
     *
     * @return HTTP 401 Unauthorized.
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse> handleBadCredentials() {
        log.warn("Authentication failed.");
        return buildResponse(HttpStatus.UNAUTHORIZED, Result.BAD_CREDENTIALS, "Bad username/password");
    }

    /**
     * Handle bad request.
     *
     * @param ex {@link  MethodArgumentNotValidException}
     * @return HTTP 400 Bad Request.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleBadRequest(MethodArgumentNotValidException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, Result.PARAM_ILLEGAL, ex.getMessage());
    }

    /**
     * Handle resource not found.
     *
     * @param ex {@link ResourceNotFoundException}
     * @return HTTP 404
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> handleNotFound(ResourceNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, Result.RESOURCE_NOT_FOUND, ex.getMessage());
    }

    /**
     * Handle invalid-format request, e.g. invalid enum value.
     *
     * @param ex {@link HttpMessageNotReadableException}
     * @return HTTP 400 Bad Request
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse> handleInvalidEnumValue(HttpMessageNotReadableException ex) {
        String message = ex.getMessage();

        if (ex.getCause() instanceof InvalidFormatException ife) {
            if (ife.getTargetType().isEnum()) {
                message = String.format("Invalid value '%s' for %s. Allowed values: %s",
                        ife.getValue(),
                        ife.getTargetType().getSimpleName(),
                        Arrays.toString(ife.getTargetType().getEnumConstants()));
            }
        }

        return buildResponse(HttpStatus.BAD_REQUEST, Result.PARAM_ILLEGAL, message);
    }

    /**
     * Handle book not available error upon loan request.
     *
     * @param ex {@link BookNotAvailableException}
     * @return HTTP 422 Unprocessable Entity
     */
    @ExceptionHandler(BookNotAvailableException.class)
    public ResponseEntity<ApiResponse> handleLoanConflict(BookNotAvailableException ex) {
        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, Result.BOOK_NOT_AVAILABLE, ex.getMessage());
    }

    /**
     * Handle loan request error when user attempt to make loan while exceeding max loans permitted
     * @param ex {@link MaxLoansExceededException}
     * @return HTTP 422 Unprocessable Entity
     */
    @ExceptionHandler(MaxLoansExceededException.class)
    public ResponseEntity<ApiResponse> handleMaxLoansExceeded(MaxLoansExceededException ex) {
        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, Result.MAX_LOAN_EXCEEDED, ex.getMessage());
    }

    /**
     * Handle loan request where user has overdue loan
     * @param ex {@link OverdueLoanException}
     * @return HTTP 422 Unprocessable Entity
     */
    @ExceptionHandler(OverdueLoanException.class)
    public ResponseEntity<ApiResponse> handleOverdueLoan(OverdueLoanException ex) {
        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, Result.LOAN_OVERDUE, ex.getMessage());
    }

    /**
     * Handle access denied event.
     * @param ex {@link AccessDeniedException}
     * @return HTTP 403 Unauthorized
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse> handleOverdueLoan(AccessDeniedException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, Result.ACCESS_DENIED, ex.getMessage());
    }

    /**
     * Handle access denied event.
     * @param ex {@link LoanAlreadyReturnedException}
     * @return HTTP 403 Unauthorized
     */
    @ExceptionHandler(LoanAlreadyReturnedException.class)
    public ResponseEntity<ApiResponse> handleLoanStatusException(LoanAlreadyReturnedException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, Result.LOAN_ALREADY_RETURNED, ex.getMessage());
    }

    /**
     * Handle generic/undefined exception.
     *
     * @param ex {@link Exception}
     * @return
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGenericException(Exception ex) {
        log.error("Internal Error: ", ex.fillInStackTrace());
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, Result.INTERNAL_ERROR, ex.getMessage());
    }

    private ResponseEntity<ApiResponse> buildResponse(HttpStatus status, Result result, String message) {
        return ResponseEntity
                .status(status)
                .body(ApiResponse
                        .builder()
                        .result(result)
                        .message(message)
                        .build());
    }
}
