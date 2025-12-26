package com.asia.booklender.shared.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Role {
    MEMBER("MEMBER", "ROLE_MEMBER"),
    ADMIN("ADMIN", "ROLE_ADMIN");

    private final String role;
    private final String authority;
}