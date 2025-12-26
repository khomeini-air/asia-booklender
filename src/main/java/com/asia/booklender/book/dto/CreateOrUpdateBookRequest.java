package com.asia.booklender.book.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrUpdateBookRequest {
    @NotBlank
    private String title;

    @NotBlank
    private String author;

    @NotBlank
    private String isbn;

    @PositiveOrZero(message = "totalCopies must be positive or zero")
    private int totalCopies;

    @PositiveOrZero(message = "availableCopies must be positive or zero")
    private int availableCopies;
}
