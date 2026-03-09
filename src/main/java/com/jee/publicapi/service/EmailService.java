package com.jee.publicapi.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.mail.*;
import jakarta.mail.internet.*;

/**
 * EmailService handles sending OTP and Registration confirmation emails
 */
@Service
public class EmailService {

    @Value("${spring.mail.username}")
    private String FROM;

    @Value("${spring.mail.password}")
    private String PASSWORD;

    /* ===================== SEND OTP EMAIL ===================== */
    public void sendOtpEmail(String to, String otp) {
        try {
            Session session = createSession();

            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(FROM));
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            msg.setSubject("JEE (Main) OTP Verification");
            msg.setContent(buildOtpTemplate(otp), "text/html");

            Transport.send(msg);
        } catch (Exception e) {
            throw new RuntimeException("OTP Email sending failed", e);
        }
    }

    /* ===================== SEND REGISTRATION SUCCESS EMAIL ===================== */
    public void sendRegistrationSuccessEmail(String toCandidate, String firstName, String applicationNo) {
        try {
            Session session = createSession();

            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(FROM));
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(toCandidate));
            msg.setSubject("JEE (Main) Registration Successful");
            msg.setContent(buildRegistrationTemplate(firstName, applicationNo), "text/html");

            Transport.send(msg);
        } catch (Exception e) {
            throw new RuntimeException("Registration Email sending failed", e);
        }
    }

    /* ===================== CREATE MAIL SESSION ===================== */
    private Session createSession() {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.auth", "true");

        return Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM, PASSWORD);
            }
        });
    }


    /* ===================== OTP EMAIL TEMPLATE ===================== */
    private String buildOtpTemplate(String otp) {
        return """
            <div style="font-family: Arial, sans-serif; text-align:center; padding:20px;">
                <h2>JEE (Main) OTP Verification</h2>
                <p>Your One-Time Password (OTP) is:</p>
                <h1 style="color:#007bff;">%s</h1>
                <p>Valid for 5 minutes.</p>
                <p>If you did not request this, please ignore this email.</p>
            </div>
            """.formatted(otp);
    }

    /* ===================== REGISTRATION SUCCESS TEMPLATE ===================== */
    private String buildRegistrationTemplate(String firstName, String applicationNo) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm"));

        return """
            <div style="font-family: Arial, sans-serif; padding:30px; background-color:#f9f9f9;">
                <div style="max-width:600px; margin:auto; background:white; border-radius:10px; padding:20px; text-align:center;">
                    <h2 style="color:#28a745;">Registration Successful ✅</h2>
                    <p>Dear <b>%s</b>,</p>
                    <p>Congratulations! Your registration for <b>JEE (Main)</b> has been successfully completed.</p>
                    <p><b>Application Number:</b> %s</p>
                    <p><b>Registered On:</b> %s</p>
                    <a href="http://localhost:5173/dashboard" style="
                        display:inline-block;
                        margin-top:20px;
                        padding:12px 25px;
                        background-color:#007bff;
                        color:white;
                        text-decoration:none;
                        border-radius:5px;
                        font-weight:bold;
                    ">Go to Dashboard</a>
                    <p style="margin-top:20px; font-size:12px; color:#555;">
                        If you did not register, please contact support immediately.
                    </p>
                </div>
            </div>
            """.formatted(firstName, applicationNo, timestamp);
    }
}
