package com.asia.booklender.book.api;

import com.asia.booklender.book.dto.BookDto;
import com.asia.booklender.shared.api.PaginationRequest;
import com.asia.booklender.book.service.BookService;
import com.asia.booklender.shared.api.ApiPaginationResponse;
import com.asia.booklender.shared.api.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Validated
public class BookController {
    private final BookService bookService;

    /**
     * a public API to return all available book with paging
     *
     * @param request pagination request parameter
     * @return all books as per {@link PaginationRequest}
     */
    @GetMapping
    public ResponseEntity<ApiPaginationResponse<List<BookDto>>> getAll(@Valid @ModelAttribute PaginationRequest request) {
        Page<BookDto> allBooks = bookService.findAll(request.toPageable());

        return ResponseEntity.ok(ApiPaginationResponse.success(allBooks));
    }

    /**
     * Get book detail by id
     *
     * @param id the book's id
     * @return the book detail
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookDto>> getById(@PathVariable Long id) {
        BookDto bookDTO = bookService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(bookDTO));
    }
}
