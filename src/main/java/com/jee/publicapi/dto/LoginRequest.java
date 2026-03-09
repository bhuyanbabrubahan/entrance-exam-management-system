package com.jee.publicapi.dto;

public class LoginRequest {

    private String role;     // USER or ADMIN
    private String username; // applicationNumber OR email
    private String password;
    
 // getters & setters
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

    
}
