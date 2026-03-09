package com.jee.publicapi.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class OtpEntity {

    @Id
    private String email;

    private String otp;

    private LocalDateTime expiryTime;
}