package com.jee.publicapi.entity;

public class Captcha {

    private String captchaId;
    private String captchaText;
    private byte[] imageBytes;

    public Captcha(String captchaId, String captchaText, byte[] imageBytes) {
        this.captchaId = captchaId;
        this.captchaText = captchaText;
        this.imageBytes = imageBytes;
    }

    public String getCaptchaId() {
        return captchaId;
    }

    public String getCaptchaText() {
        return captchaText;
    }

    public byte[] getImageBytes() {
        return imageBytes;
    }
}
