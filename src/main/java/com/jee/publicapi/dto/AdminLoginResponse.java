package com.jee.publicapi.dto;

import java.util.Set;

public class AdminLoginResponse {

    private String token;
    private Set<String> roles;
    private String email;

    public AdminLoginResponse(String token, Set<String> roles, String email) {
        this.token = token;
        this.roles = roles;
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public String getEmail() {
        return email;
    }
}
