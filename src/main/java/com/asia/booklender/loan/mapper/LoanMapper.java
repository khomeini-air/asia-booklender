package com.asia.booklender.loan.mapper;

import com.asia.booklender.book.mapper.BookMapper;
import com.asia.booklender.loan.dto.LoanDto;
import com.asia.booklender.loan.entity.Loan;
import com.asia.booklender.member.mapper.MemberMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for converting between {@link com.asia.booklender.loan.entity.Loan} domain entity and its DTO
 */
@Mapper(componentModel = "spring", uses = {BookMapper.class, MemberMapper.class})
public interface LoanMapper {

    /**
     * Map a {@link Loan} entity to a {@link LoanDto}
     *
     * @param loan {@code Loan} domain entity.
     * @return {@code LoanDto} for API responses
     */
    @Mapping(target = "borrowedAt", expression = "java(loan.getBorrowedAt().toEpochMilli())")
    @Mapping(target = "dueAt", expression = "java(loan.getDueAt().toEpochMilli())")
    @Mapping(target = "returnedAt", expression = "java(loan.getReturnedAt() != null ? loan.getReturnedAt().toEpochMilli() : null)")
    LoanDto toDto(Loan loan);
}
