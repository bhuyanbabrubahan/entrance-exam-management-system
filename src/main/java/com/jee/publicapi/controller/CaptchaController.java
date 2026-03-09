package com.jee.publicapi.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jee.publicapi.entity.Captcha;
import com.jee.publicapi.service.CaptchaService;

@RestController
@RequestMapping("/captcha")
@CrossOrigin(origins = "http://localhost:5173")
public class CaptchaController {

    private final CaptchaService captchaService;

    public CaptchaController(CaptchaService captchaService) {
        this.captchaService = captchaService;
    }

    @GetMapping(value = "/generate", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> generateCaptcha() {

        Captcha captcha = captchaService.generateCaptcha();
        String captchaToken =
                captchaService.generateCaptchaToken(
                        captcha.getCaptchaId(),
                        captcha.getCaptchaText()
                );

        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl("no-store");
        headers.add("captcha-token", captchaToken);
        headers.add("Access-Control-Expose-Headers", "captcha-token");

        return ResponseEntity.ok()
                .headers(headers)
                .body(captcha.getImageBytes());
    }
}
