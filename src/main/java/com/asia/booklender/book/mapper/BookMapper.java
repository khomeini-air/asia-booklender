package com.asia.booklender.book.mapper;

import com.asia.booklender.book.dto.BookDto;
import com.asia.booklender.book.entity.Book;
import org.mapstruct.Mapper;

/**
 * MapStruct mapper for converting between Book domain entity and DTOs.
 */
@Mapper(componentModel = "spring")
public interface BookMapper {
    /**
     * Maps a {@link Book} entity to a {@link BookDto}.
     *
     *  @param book Book domain entity
     * @return BookDto for the public API responses
     */
    BookDto toDto(Book book);
}
