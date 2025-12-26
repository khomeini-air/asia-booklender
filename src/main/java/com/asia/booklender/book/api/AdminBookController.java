package com.asia.booklender.book.api;

import com.asia.booklender.book.dto.BookDto;
import com.asia.booklender.book.dto.CreateOrUpdateBookRequest;
import com.asia.booklender.book.service.BookService;
import com.asia.booklender.shared.api.ApiResponse;
import com.asia.booklender.shared.api.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/books")
@RequiredArgsConstructor
@Validated
public class AdminBookController {
    private final BookService bookService;

    /**
     * Creates or update the book
     *
     * @param request create or update book request
     * @return {@code BookDto} the book detail after saved
     */
    @PostMapping
    public ResponseEntity<ApiResponse<BookDto>> create(@RequestBody @Valid CreateOrUpdateBookRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(bookService.create(request)));
    }

    /**
     * Retrieves a book detail by id
     *
     * @param id the book's id
     * @param request
     * @return
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BookDto>> updateById(@PathVariable Long id, @RequestBody @Valid CreateOrUpdateBookRequest request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(bookService.update(id, request)));
    }

    /**
     * Delete a book by id
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteById(@PathVariable Long id) {
        bookService.deleteById(id);

        ApiResponse<Void> response = ApiResponse.<Void>builder().result(Result.SUCCESS).build();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}
