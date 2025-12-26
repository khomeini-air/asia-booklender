package com.asia.booklender.loan.exception;

/**
 * Exception thrown when attempting to borrow a book that has no available copies.
 */
public class BookNotAvailableException extends RuntimeException {
    public BookNotAvailableException(String message) {
        super(message);
    }
}
