package com.asia.booklender.loan.dto;

import com.asia.booklender.book.dto.BookDto;
import com.asia.booklender.member.dto.MemberDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(NON_NULL)
public class LoanDto {
    private Long id;
    private Long borrowedAt;
    private Long returnedAt;
    private Long dueAt;
    private BookDto book;
    private MemberDto member;
}
