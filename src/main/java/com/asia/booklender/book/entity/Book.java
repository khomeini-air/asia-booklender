package com.asia.booklender.book.entity;

import com.asia.booklender.shared.entity.BasedEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "books")
@Data
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class Book extends BasedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false, unique = true)
    private String isbn;

    @Column(nullable = false, name = "total_copies")
    private Integer totalCopies;

    @Column(nullable = false, name = "available_copies")
    private Integer availableCopies;

    @Version
    @Column(nullable = false, columnDefinition = "DEFAULT 0")
    private Integer version;
}
