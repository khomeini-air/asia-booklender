package com.asia.booklender.shared.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Result {
    SUCCESS("S", "SUCCESS", "Success"),
    BAD_CREDENTIALS("F", "BAD_CREDENTIALS", "User is not aunthenticated/authorized"),
    PARAM_ILLEGAL("F", "PARAM_ILLEGAL", "Bad Parameter"),
    RESOURCE_NOT_FOUND("F", "RESOURCE_NOT_FOUND", "Resource Not Found"),
    BOOK_NOT_AVAILABLE("F", "BOOK_NOT_AVAILABLE", "Book not available for loan"),
    MAX_LOAN_EXCEEDED("F", "MAX_LOAN_EXCEEDED", "Maximum loan exceeded"),
    LOAN_OVERDUE("F", "LOAN_OVERDUE", "User has loan overdue"),
    ACCESS_DENIED("F", "ACCESS_DENIED", "User is not authorized to access the service"),
    LOAN_ALREADY_RETURNED("F", "LOAN_ALREADY_RETURNED", "The loan has been returned previously"),
    INTERNAL_ERROR("F", "UNKNOWN_ERROR", "Unknown Error");

    private final String result;
    private final String code;
    private final String description;
}
