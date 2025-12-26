package com.asia.booklender.loan.exception;

/**
 * Exception thrown when a member attempts to borrow more books than allowed.
 */
public class MaxLoansExceededException extends RuntimeException {
    public MaxLoansExceededException(String message) {
        super(message);
    }
}
