package com.asia.booklender.member.api;

import com.asia.booklender.member.dto.MemberDto;
import com.asia.booklender.member.service.MemberService;
import com.asia.booklender.shared.api.ApiPaginationResponse;
import com.asia.booklender.shared.api.PaginationRequest;
import com.asia.booklender.shared.exception.AccessDeniedException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/members")
@RequiredArgsConstructor
@Validated
public class AdminMemberController {
    private final MemberService memberService;

    /**
     * API to return all available members within the given page parameter
     *
     * @param request pagination request parameter
     * @return all members as per {@link PaginationRequest}
     *
     * @throws AccessDeniedException for Members
     */
    @GetMapping
    public ResponseEntity<ApiPaginationResponse<List<MemberDto>>> getAll(@Valid @ModelAttribute PaginationRequest request) {
        Page<MemberDto> allMember = memberService.findAll(request.toPageable());

        return ResponseEntity.ok(ApiPaginationResponse.success(allMember));
    }
}
