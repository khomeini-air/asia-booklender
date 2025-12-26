package com.asia.booklender.shared.api;

import com.asia.booklender.shared.enums.EntitySortField;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.query.SortDirection;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaginationRequest {
    @Min(value = 0, message = "Page number must be non-negative")
    @Builder.Default
    private Integer page = 0;

    @Min(value = 1, message = "Page size must be positive")
    @Max(value = 100, message = "Page size cannot exceed 100")
    @Builder.Default
    private Integer size = 10;

    @Builder.Default
    private SortDirection sortDirection = SortDirection.ASCENDING;

    @Builder.Default
    private EntitySortField sortField = EntitySortField.ID;

    public Pageable toPageable() {
        Sort sort = sortDirection == SortDirection.ASCENDING ?
                Sort.by(sortField.getFieldName()).ascending() :
                Sort.by(sortField.getFieldName()).descending();

        return PageRequest.of(page, size, sort);
    }
}
