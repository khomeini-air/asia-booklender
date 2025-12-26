package com.asia.booklender.book.mapper;

import com.asia.booklender.book.dto.BookAdminDto;
import com.asia.booklender.book.dto.CreateOrUpdateBookRequest;
import com.asia.booklender.book.entity.Book;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper for converting between Book domain entity and DTO for admin operation.
 */
@Mapper(componentModel = "spring")
public interface BookAdminMapper {
    /**
     * Maps a {@link Book} entity to a {@link BookAdminDto}.
     *<p>
     * The {@code createdAt} and {@code updatedAt} are converted from Instant to epoch milliseconds
     * to keep the API payload simple and timezone-agnostic.
     *
     * @param book Book domain entity
     * @return BookAdminDto for API responses
     */
    @Mapping(target = "createdAt",
            expression = "java(book.getCreatedAt() != null ? book.getCreatedAt().toEpochMilli() : null)")
    @Mapping(target = "updatedAt",
            expression = "java(book.getUpdatedAt() != null ? book.getUpdatedAt().toEpochMilli() : null)")
    BookAdminDto toBookAdminDto(Book book);

    /**
     * Maps create/update request payload to Book entity.
     *
     * @param request Create or update book request
     * @return Book entity ready for persistence
     */
    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    Book toBook(CreateOrUpdateBookRequest request);
}
