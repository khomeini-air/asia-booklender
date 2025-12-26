package com.asia.booklender.member.service.impl;

import com.asia.booklender.member.dto.MemberDto;
import com.asia.booklender.member.entity.Member;
import com.asia.booklender.member.mapper.MemberMapper;
import com.asia.booklender.member.repository.MemberRepository;
import com.asia.booklender.member.service.MemberService;
import com.asia.booklender.shared.exception.AccessDeniedException;
import com.asia.booklender.shared.exception.ResourceNotFoundException;
import com.asia.booklender.shared.security.CurrentUser;
import com.asia.booklender.shared.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default implementation of {@link MemberService}.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<MemberDto> findAll(Pageable page) {
        CurrentUser currentUser = SecurityUtil.currentUser();

        // Reject non-admin users
        if(currentUser == null || !currentUser.isAdmin()) {
            log.warn("Unauthenticated user or Non-admin attempt to get all members. User: {}", currentUser);
            throw new AccessDeniedException("Denied request for non-admin");
        }

        return memberRepository
                .findAll(page)
                .map(memberMapper::toDto);
    }

    @Override
    public MemberDto findMe() {
        CurrentUser currentUser = SecurityUtil.currentUser();

        if (currentUser == null) {
            log.warn("Unauthenticated user attempt to get me-details");
            throw new AccessDeniedException("Denied request for non-authenticated user");
        }

        Member me = memberRepository
                .findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Member me not found with email: " + currentUser.getUsername()));

        return memberMapper.toDto(me);
    }
}
