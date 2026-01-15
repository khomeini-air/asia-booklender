package com.asia.booklender.member.entity;

import com.asia.booklender.shared.entity.BasedEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "members",
        indexes = {
                @Index(name = "idx_email", columnList = "email", unique = true)
        })
@Data
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class Member extends BasedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;
}
