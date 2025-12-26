package com.asia.booklender.loan.repository;

import com.asia.booklender.loan.entity.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    Page<Loan> findByMemberId(Long memberId, Pageable pageable);

    Page<Loan> findByMemberIdAndReturnedAtIsNull(Long memberId, Pageable pageable);

    Page<Loan> findByBookIdAndReturnedAtIsNull(Long bookId, Pageable pageable);

    int countByMemberIdAndReturnedAtIsNull(Long memberId);

    boolean existsByMemberIdAndReturnedAtIsNullAndDueAtBefore(Long memberId, Instant before);
}
