package com.jee.publicapi.service;

public interface OtpService {

    void storeOtp(String key, String otp);

    boolean validateOtp(String key, String otp);

    void removeOtp(String key);
}
