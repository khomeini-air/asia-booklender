package com.asia.booklender.book.service.impl;

import com.asia.booklender.book.dto.BookAdminDto;
import com.asia.booklender.book.dto.BookDto;
import com.asia.booklender.book.dto.CreateOrUpdateBookRequest;
import com.asia.booklender.book.entity.Book;
import com.asia.booklender.book.mapper.BookAdminMapper;
import com.asia.booklender.book.mapper.BookMapper;
import com.asia.booklender.book.repository.BookRepository;
import com.asia.booklender.book.service.BookService;
import com.asia.booklender.shared.exception.AccessDeniedException;
import com.asia.booklender.shared.exception.ResourceNotFoundException;
import com.asia.booklender.shared.security.CurrentUser;
import com.asia.booklender.shared.security.SecurityUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookAdminMapper bookAdminMapper;

    @Override
    @Transactional
    public BookAdminDto create(CreateOrUpdateBookRequest request) {
        CurrentUser currentUser = SecurityUtil.currentUser();

        // Defensively validate the user - reject if non-admin.
        validateUser(currentUser);

        Book book = bookAdminMapper.toBook(request);
        book.setCreatedBy(currentUser.getUsername());
        book = bookRepository.save(book);

        log.info("Book successfully created id={} title={} isbn={} by user={}",
                book.getId(),
                book.getTitle(),
                book.getIsbn(),
                currentUser.getUsername());

        return bookAdminMapper.toBookAdminDto(book);
    }

    @Override
    @Transactional
    public BookAdminDto update(Long id, CreateOrUpdateBookRequest request) {
        CurrentUser currentUser = SecurityUtil.currentUser();

        // Defensively validate the user - reject if non-admin.
        validateUser(currentUser);

        // Find the book from the database
        Book book = bookRepository
                .findById(id)
                .orElseThrow(() -> {
                    log.warn("Book not found id={}", id);
                    return new ResourceNotFoundException(String.format("Book id %s not found", id));
                });

        // Debug: before state
        log.debug(
                "Book before update id={} title={} author={} totalCopies={} availableCopies={}",
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getTotalCopies(),
                book.getAvailableCopies()
        );

        // Update the book based on the request
        updateBook(book, request);

        // Debug: after state
        log.debug(
                "Book after update id={} title={} author={} totalCopies={} availableCopies={}",
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getTotalCopies(),
                book.getAvailableCopies()
        );

        // Save the updated book
        book = bookRepository.save(book);

        log.info("Book successfully updated id={} by user={}", id, currentUser.getUsername());
        return bookAdminMapper.toBookAdminDto(book);
    }

    private void updateBook(Book book, CreateOrUpdateBookRequest request) {
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setAvailableCopies(request.getAvailableCopies());
        book.setTotalCopies(request.getTotalCopies());
        book.setUpdatedAt(Instant.now());
    }

    @Override
    public Page<BookDto> findAll(Pageable pageable) {
        return bookRepository
                .findAll(pageable)
                .map(bookMapper::toDto);
    }

    @Override
    public BookDto findById(Long id) {
        return bookRepository
                .findById(id)
                .map(bookMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Book id %s not found", id)));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        CurrentUser currentUser = SecurityUtil.currentUser();

        // Defensively validate the user - reject if non-admin.
        validateUser(currentUser);

        Book book = bookRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Book id %s not found", id)));
        bookRepository.delete(book);

        log.warn("Book deleted id={} by user={}", id, currentUser.getUsername());
    }

    private void validateUser(CurrentUser currentUser) {
        if(currentUser == null || !currentUser.isAdmin()) {
            log.warn("Non-admin attempt to access books management. User: {}", currentUser);
            throw new AccessDeniedException("Denied request for non-admin");
        }
    }
}
