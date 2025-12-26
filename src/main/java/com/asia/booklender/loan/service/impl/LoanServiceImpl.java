package com.asia.booklender.loan.service.impl;

import com.asia.booklender.loan.dto.LoanDto;
import com.asia.booklender.loan.entity.Loan;
import com.asia.booklender.loan.mapper.LoanMapper;
import com.asia.booklender.loan.repository.LoanRepository;
import com.asia.booklender.loan.service.BorrowBookService;
import com.asia.booklender.loan.service.LoanService;
import com.asia.booklender.loan.service.ReturnBookService;
import com.asia.booklender.member.entity.Member;
import com.asia.booklender.member.repository.MemberRepository;
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
 * Default implementation of {@link LoanService}.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LoanServiceImpl implements LoanService {
    private final LoanRepository loanRepository;
    private final LoanMapper loanMapper;
    private final MemberRepository memberRepository;
    private final BorrowBookService borrowBookService;
    private final ReturnBookService returnBookService;

    @Override
    @Transactional
    public LoanDto loan(Long bookId) {
        log.info("Processing borrow request for bookId: {}", bookId);

        // Get the authenticated member (automatically from JWT token)
        Member member = getAuthenticatedMember();

        // Execute borrow process
        Loan loan = borrowBookService.borrow(bookId, member);

        // Map to the DTO and return
        return loanMapper.toDto(loan);
    }

    @Override
    @Transactional
    public LoanDto returnBook(Long loanId) {
        log.info("Processing loan return request for loanId: {}", loanId);

        // Get the authenticated member (automatically from JWT token)
        Member member = getAuthenticatedMember();

        // Execute return process
        Loan loan = returnBookService.returnBook(loanId, member);

        // Map to the DTO and return
        return loanMapper.toDto(loan);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LoanDto> findMy(boolean isActive, Pageable pageable) {
        Member member = getAuthenticatedMember();

        // activeOnly = true, Retrieving active book loans only
        if (isActive) {
            return loanRepository
                    .findByMemberIdAndReturnedAtIsNull(member.getId(), pageable)
                    .map(loanMapper::toDto);
        }

        // activeRetrieving all book loans
        return loanRepository
                .findByMemberId(member.getId(), pageable)
                .map(loanMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public LoanDto findById(Long loanId) {
        CurrentUser currentUser = SecurityUtil.currentUser();

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found with id: " + loanId));

        // Authorization check: Members can only view their own loans
        validateAuthority(currentUser, loan);

        return loanMapper.toDto(loan);
    }

    private void validateAuthority(CurrentUser currentUser, Loan loan) {
        if (!currentUser.isAdmin() && !loan.getMemberEmail().equals(currentUser.getUsername())) {
            log.warn("Member (Non-admin) {} attempted to access loan {} belonging to member {}",
                    currentUser, loan.getId(), loan.getMember().getId());
            throw new AccessDeniedException("You can only view your own loans");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LoanDto> findAll(Pageable page) {
        CurrentUser currentUser = SecurityUtil.currentUser();

        // Reject non-admin users
        if(!currentUser.isAdmin()) {
            log.warn("Non-admin attempt to get all book loan. User: {}", currentUser);
            throw new AccessDeniedException("Denied request for non-admin");
        }

        return loanRepository
                .findAll(page)
                .map(loanMapper::toDto);
    }

    /**
     * Gets the authenticated member from the security context.
     * Extracts the member based on the email stored in the JWT token.
     *
     * @return the authenticated member entity
     * @throws AccessDeniedException if user is not authenticated or not a member
     */
    private Member getAuthenticatedMember() {
        CurrentUser currentUser = SecurityUtil.currentUser();

        // Ensure the current user is authenticated.
        if (currentUser == null) {
            log.warn("Attempted to access book loan feature by unauthenticated user. ");
            throw new AccessDeniedException("User is not authenticated");
        }

        // For members, username is their email address
        Member member = memberRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new AccessDeniedException("Authenticated user is not associated with a member account"));

        log.info("Found authenticated member: {}/{}", member.getName(), member.getEmail());
        return member;
    }
}
