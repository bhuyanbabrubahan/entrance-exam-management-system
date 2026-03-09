package com.jee.publicapi.serviceimpl;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.jee.publicapi.service.OtpService;

/**
 * OTP Service Implementation
 */
@Service
public class OtpServiceImpl implements OtpService {

    /**
     * Internal OTP store (in-memory)
     * Key   → email / mobile
     * Value → OTP + expiry
     */
    private final Map<String, OtpEntry> otpStore = new ConcurrentHashMap<>();

    private static final int OTP_VALID_MINUTES = 5;

    @Override
    public void storeOtp(String key, String otp) {
        LocalDateTime expiryTime =
                LocalDateTime.now().plusMinutes(OTP_VALID_MINUTES);

        otpStore.put(key, new OtpEntry(otp, expiryTime));
    }

    @Override
    public boolean validateOtp(String key, String otp) {

        OtpEntry entry = otpStore.get(key);
        if (entry == null) {
            return false;
        }

        // ⏰ Expired
        if (LocalDateTime.now().isAfter(entry.expiryTime)) {
            otpStore.remove(key);
            return false;
        }

        // ✅ Match
        boolean valid = entry.otp.equals(otp);
        if (valid) {
            otpStore.remove(key); // one-time use
        }

        return valid;
    }

    @Override
    public void removeOtp(String key) {
        otpStore.remove(key);
    }

    /* ================= INTERNAL HOLDER ================= */

    private static class OtpEntry {
        private final String otp;
        private final LocalDateTime expiryTime;

        private OtpEntry(String otp, LocalDateTime expiryTime) {
            this.otp = otp;
            this.expiryTime = expiryTime;
        }
    }
}
