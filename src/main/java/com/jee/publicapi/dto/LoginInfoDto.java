package com.jee.publicapi.dto;

import java.time.LocalDateTime;

public class LoginInfoDto {

    private LocalDateTime loginTime;
    private String ipAddress;

    public LoginInfoDto() {}

    public LoginInfoDto(LocalDateTime loginTime, String ipAddress) {
        this.loginTime = loginTime;
        this.ipAddress = ipAddress;
    }

    public LocalDateTime getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(LocalDateTime loginTime) {
        this.loginTime = loginTime;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
