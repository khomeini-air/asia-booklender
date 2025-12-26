package com.asia.booklender.book.service;

import com.asia.booklender.book.dto.BookAdminDto;
import com.asia.booklender.book.dto.BookDto;
import com.asia.booklender.book.dto.CreateOrUpdateBookRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {
    /**
     * Saves the book given by the request.
     *
     * @param request create book request.
     * @return {@link BookDto} book's detail information
     */
    BookAdminDto create(CreateOrUpdateBookRequest request);

    /**
     * Updates existing book as per the update request.
     *
     * @param id update request
     * @param request update request
     * @return {@link BookDto} with updated attributes
     */
    BookAdminDto update(Long id, CreateOrUpdateBookRequest request);

    /**
     * Retrieves all available books within the given page.
     *
     * @param page {@link Page}
     * @return all available books
     */
    Page<BookDto> findAll(Pageable page);

    /**
     * Retrieve the book detail by id
     *
     * @param id the book id
     * @return the book detail
     */
    BookDto findById(Long id);

    /**
     * Deletes a book by id
     * @param id the book id
     */
    void deleteById(Long id);
}
