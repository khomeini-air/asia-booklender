package com.asia.booklender.loan.exception;

/**
 * Exception thrown when a member with overdue loans attempts to borrow a new book.
 */
public class OverdueLoanException extends RuntimeException {
    public OverdueLoanException(String message) {
        super(message);
    }
}
