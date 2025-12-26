package com.asia.booklender.member.service;

import com.asia.booklender.member.dto.MemberDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberService {
    /**
     * Retrieve all members within the given page.
     * Only applicable for Admin.
     *
     * @param page the page parameter
     * @return the {@code Page<MemberDto>}
     */
    Page<MemberDto> findAll(Pageable page);

    /**
     * Retrieve member detail for the current user
     *
     * @return my member detail
     */
    MemberDto findMe();
}
