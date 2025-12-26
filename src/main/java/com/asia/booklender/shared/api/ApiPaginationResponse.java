package com.asia.booklender.shared.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Page;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(NON_NULL)
public class ApiPaginationResponse<T> extends ApiResponse<T> {
    private PaginationMeta pagination;

    public static <T> ApiPaginationResponse<List<T>> success(Page<T> pageData) {
        return ApiPaginationResponse
                .<List<T>>builder()
                .result(Result.SUCCESS)
                .pagination(PaginationMeta.builder()
                        .currentPage(pageData.getNumber())
                        .totalPages(pageData.getTotalPages())
                        .pageSize(pageData.getSize())
                        .totalElements(pageData.getTotalElements())
                        .build())
                .data(pageData.getContent())
                .build();
    }
}
