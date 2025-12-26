package com.asia.booklender.loan.entity;

import com.asia.booklender.book.entity.Book;
import com.asia.booklender.member.entity.Member;
import com.asia.booklender.shared.entity.BasedEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "loans")
@Data
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class Loan extends BasedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(name = "borrowed_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Instant borrowedAt;

    @Column(name = "returned_at", columnDefinition = "TIMESTAMP")
    private Instant returnedAt;

    @Column(name = "due_at", nullable = false, columnDefinition = "TIMESTAMP")
    private Instant dueAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    /**
     * Check if the book loan is active, meaning it has not been returned yet.
     * @return true if active, false otherwise.
     */
    public boolean isActive() {
        return returnedAt == null;
    }

    /**
     * Check if the book loan is still active and if it is already overdue.
     *
     * @return true if overdue, false otherwise.
     */
    public boolean isOverdue() {
        return isActive() && Instant.now().isAfter(dueAt);
    }

    /**
     * Get the member's email of this book loan.
     * @return member email.
     */
    public String getMemberEmail() {
        return member.getEmail();
    }
}
