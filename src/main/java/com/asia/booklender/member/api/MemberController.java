package com.asia.booklender.member.api;

import com.asia.booklender.member.dto.MemberDto;
import com.asia.booklender.member.service.MemberService;
import com.asia.booklender.shared.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Validated
public class MemberController {
    private final MemberService memberService;

    /**
     * Retrieves my member information
     *
     * @return member detail for the current user
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MemberDto>> getMe() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(memberService.findMe()));
    }
}
