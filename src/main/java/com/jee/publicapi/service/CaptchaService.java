package com.jee.publicapi.service;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import com.jee.publicapi.entity.Captcha;

@Service
public class CaptchaService {

    private final JwtService jwtService;

    public CaptchaService(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    /* =====================================================
       CAPTCHA IMAGE GENERATION
       ===================================================== */

    public Captcha generateCaptcha() {

        // 🔹 Random captcha text
        String text = RandomStringUtils
                .randomAlphanumeric(6)
                .toUpperCase();

        // 🔹 Create image
        BufferedImage image =
                new BufferedImage(150, 50, BufferedImage.TYPE_INT_RGB);

        Graphics2D g = image.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 150, 50);

        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 28));
        g.drawString(text, 25, 35);
        g.dispose();

        // 🔹 Convert image to byte[]
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", baos);
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate captcha image", e);
        }

        // 🔹 Captcha ID (only for reference, not storage)
        String captchaId = UUID.randomUUID().toString();

        return new Captcha(
                captchaId,
                text,
                baos.toByteArray()
        );
    }

    /* =====================================================
       🔐 JWT-BASED CAPTCHA (STATELESS)
       ===================================================== */

    /** Generate signed captcha token (short-lived) */
    public String generateCaptchaToken(String captchaId, String captchaText) {

        return jwtService.generateCaptchaToken(
                captchaId,
                captchaText
        );
    }

    /** Validate captcha token */
    public boolean validateCaptcha(String token, String userInput) {

        if (token == null || userInput == null) {
            return false;
        }

        return jwtService.validateCaptchaToken(
                token,
                userInput
        );
    }
}
