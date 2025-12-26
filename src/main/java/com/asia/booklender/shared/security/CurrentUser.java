package com.asia.booklender.shared.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrentUser {
    private String username;
    private Set<String> roles;

    public boolean isAdmin() {
        return !roles.isEmpty() && roles.stream().anyMatch(s -> s.equals(Role.ADMIN.getAuthority()));
    }
}

