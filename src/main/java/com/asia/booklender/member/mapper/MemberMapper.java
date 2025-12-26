package com.asia.booklender.member.mapper;

import com.asia.booklender.member.dto.MemberDto;
import com.asia.booklender.member.entity.Member;
import org.mapstruct.Mapper;

/**
 * MapStruct mapper for converting between {@link Member} domain entity and its DTO.
 */
@Mapper(componentModel = "spring")
public interface MemberMapper {
    MemberDto toDto(Member member);
}
