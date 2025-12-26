package com.asia.booklender.shared.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(NON_NULL)
public class PaginationMeta {
    private Integer currentPage;
    private Integer pageSize;
    private Long totalElements;
    private Integer totalPages;
}
