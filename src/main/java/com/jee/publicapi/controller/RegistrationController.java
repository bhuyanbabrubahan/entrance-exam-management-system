package com.jee.publicapi.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.jee.publicapi.dto.RegistrationRequest;
import com.jee.publicapi.entity.User;
import com.jee.publicapi.enums.FormStatus;
import com.jee.publicapi.helper.BarcodeGenerator;
import com.jee.publicapi.service.CaptchaService;
import com.jee.publicapi.service.EmailService;
import com.jee.publicapi.service.JwtService;
import com.jee.publicapi.service.OtpService;
import com.jee.publicapi.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

/**
 * ===================================================== REGISTRATION CONTROLLER
 * (JWT CAPTCHA + OTP + SESSION LOGIN)
 * =====================================================
 */
@RestController
@RequestMapping("/api/user/register")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class RegistrationController {

	private final UserService userService;
	private final EmailService emailService;
	private final CaptchaService captchaService;
	private final OtpService otpService;
	private final JwtService jwtService;
	private final UserDetailsService userDetailsService;
	private final PasswordEncoder passwordEncoder;

	public RegistrationController(UserService userService, EmailService emailService, CaptchaService captchaService,
			OtpService otpService, JwtService jwtService, UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {

		this.userService = userService;
		this.emailService = emailService;
		this.captchaService = captchaService;
		this.otpService = otpService;
		this.jwtService = jwtService;
		this.userDetailsService = userDetailsService;
		this.passwordEncoder = passwordEncoder;
	}

	/*
	 * ===================================================== STEP 0: VERIFY CAPTCHA
	 * (JWT BASED) URL: POST /api/user/register/verify-captcha
	 * =====================================================
	 */
	@PostMapping("/verify-captcha")
	public ResponseEntity<?> verifyCaptcha(@RequestBody Map<String, String> body) {

		String captchaToken = body.get("captchaToken");
		String userInput = body.get("userInput");

		if (captchaToken == null || userInput == null || userInput.isBlank()) {
			return ResponseEntity.badRequest().body(Map.of("message", "Captcha data missing"));
		}

		boolean valid = captchaService.validateCaptcha(captchaToken, userInput);

		if (!valid) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid captcha"));
		}

		return ResponseEntity.ok(Map.of("status", "CAPTCHA_VERIFIED", "message", "Captcha verified successfully"));
	}

	/*
	 * ===================================================== STEP 1: SEND OTP URL:
	 * POST /api/user/register =====================================================
	 */
	@PostMapping
	public ResponseEntity<?> startRegistration(@RequestBody RegistrationRequest request) {

	    request.setEmail(request.getEmail().trim().toLowerCase());

	    User existingUser = userService.findByEmail(request.getEmail());

	    if (existingUser != null && existingUser.isVerified()) {
	        return ResponseEntity.badRequest()
	                .body(Map.of("status", "EMAIL_EXISTS"));
	    }

	    if (existingUser == null) {

	        User user = new User();
	        user.setEmail(request.getEmail());
	        user.setMobileNumber(request.getMobileNumber());
	        user.setPassword(passwordEncoder.encode(request.getPassword()));

	        // 🔥 IMPORTANT FIX
	        user.setAadharCard("TEMP"); // placeholder, replaced after OTP

	        user.setEnabled(false);
	        user.setVerified(false);
	        user.setRole("ROLE_USER");
	        user.setCreatedAt(LocalDateTime.now());
	        user.setPersonalStatus(FormStatus.IN_PROGRESS);
	        user.setEducationStatus(FormStatus.NOT_STARTED);
	        user.setDocumentStatus(FormStatus.NOT_STARTED);
	        user.setPaymentStatus(FormStatus.NOT_STARTED);

	        userService.save(user);
	    }

	    String otp = generateOtp();
	    otpService.storeOtp(request.getEmail(), otp);
	    emailService.sendOtpEmail(request.getEmail(), otp);

	    return ResponseEntity.ok(
	            Map.of("status", "OTP_SENT", "email", request.getEmail()));
	}

	/*
	 * ===================================================== STEP 2: VERIFY OTP +
	 * REGISTER + AUTO LOGIN USER
	 * =====================================================
	 */
	@PostMapping("/verify-otp")
	public ResponseEntity<?> verifyOtp(
	        @RequestParam String email,
	        @RequestParam String otp,
	        @RequestBody RegistrationRequest data,
	        HttpServletRequest request) {

	    /* ---------- OTP VALIDATION ---------- */
	    if (!otpService.validateOtp(email, otp)) {
	        return ResponseEntity.badRequest().body(Map.of(
	                "status", "INVALID_OTP",
	                "message", "Invalid or expired OTP"
	        ));
	    }

	    if (data == null) {
	        return ResponseEntity.badRequest().body(Map.of(
	                "status", "INVALID_DATA",
	                "message", "Registration data missing"
	        ));
	    }

	    System.out.println("🔐 Verifying OTP for email: " + email);
	    System.out.println("OTP: " + otp);
	    System.out.println("Registration Data: " + data);

	    /* ---------- LOAD / CREATE USER ---------- */
	    User user = userService.findByEmail(email);

	    /* 🔥 SAFETY NET (VERY IMPORTANT) */
	    if (user == null) {

	        System.out.println("⚠ User missing at OTP verify. Creating shell user.");

	        user = new User();
	        user.setEmail(email);
	        user.setMobileNumber(data.getMobileNumber());
	        user.setPassword(passwordEncoder.encode(data.getPassword()));

	        // satisfy DB constraint
	        user.setAadharCard(
	            data.getAadharCard() != null ? data.getAadharCard() : "TEMP"
	        );

	        user.setEnabled(false);
	        user.setVerified(false);
	        user.setRole("ROLE_USER");
	        user.setCreatedAt(LocalDateTime.now());

	        userService.save(user);
	    }

	    Integer applicationNumber =
	            user.getApplicationNumber() != null
	                    ? user.getApplicationNumber()
	                    : generateApplicationNumber();

	    String barcode =
	            BarcodeGenerator.generateBarcodeBase64(applicationNumber.toString());

	    updateUser(user, data, applicationNumber, barcode);

	    /* ================= LOGIN TRACKING ================= */
	    user.setLastLoginTime(LocalDateTime.now());

	    String ipAddress = request.getHeader("X-FORWARDED-FOR");
	    if (ipAddress == null || ipAddress.isBlank()) {
	        ipAddress = request.getRemoteAddr();
	    }
	    user.setLastLoginIp(ipAddress);

	    /* ================= SAVE USER ================= */
	    userService.save(user);

	    otpService.removeOtp(email);

	    /* =====================================================
	       🔐 JWT AUTO LOGIN (CORRECT WAY)
	       ===================================================== */
	    UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
	    String jwtToken = jwtService.generateToken(userDetails);

	    emailService.sendRegistrationSuccessEmail(
	            user.getEmail(),
	            user.getFirstName(),
	            applicationNumber.toString()
	    );

	    System.out.println("✅ OTP verified & JWT issued for: " + email);
	    System.out.println("JWT Token Generated: " + jwtToken);

	    /* ---------- RESPONSE ---------- */
	    Map<String, Object> response = new HashMap<>();
	    response.put("status", "REGISTRATION_SUCCESS");
	    response.put("applicationNumber", applicationNumber);
	    response.put("token", jwtToken);
	    response.put("email", user.getEmail());
	    response.put("role", user.getRole());

	    return ResponseEntity.ok(response);
	}

/*===================================================== HELPERS========================= */
	
	private String generateOtp() {
		return String.valueOf(100000 + new Random().nextInt(900000));
	}

	private Integer generateApplicationNumber() {
		return 100000000 + new Random().nextInt(900000000);
	}

	private void updateUser(User user, RegistrationRequest data, Integer applicationNumber, String barcode) {

		user.setApplicationNumber(applicationNumber);
		user.setBarcodeImage(barcode);

		user.setFirstName(data.getFirstName());
		user.setMiddleName(data.getMiddleName());
		user.setLastName(data.getLastName());
		user.setGender(data.getGender());

		user.setDay(data.getDay());
		user.setMonth(data.getMonth());
		user.setYear(data.getYear());

		//user.setEmail(data.getEmail()); //Email already exists
		if (data.getAadharCard() != null && !data.getAadharCard().isBlank()) {
		    user.setAadharCard(data.getAadharCard());
		}
		user.setMobileNumber(data.getMobileNumber());
		if (data.getPassword() != null && !data.getPassword().isBlank()) {
		    user.setPassword(passwordEncoder.encode(data.getPassword()));
		}

		user.setRole("ROLE_USER");
		user.setEnabled(true);
		user.setVerified(true);

		if (user.getId() == null) {
		    user.setPersonalStatus(FormStatus.IN_PROGRESS);
		    user.setEducationStatus(FormStatus.NOT_STARTED);
		    user.setDocumentStatus(FormStatus.NOT_STARTED);
		    user.setPaymentStatus(FormStatus.NOT_STARTED);
		}

		if (user.getCreatedAt() == null) {
			user.setCreatedAt(LocalDateTime.now());
		}
	}

	/*
	 * ===================== STEP 3: RESEND OTP===========================================
	 */
	@PostMapping("/resend-otp")
	public ResponseEntity<?> resendOtp(@RequestParam String email) {
		try {
			/* ================= VALIDATION ================= */
			if (email == null || email.isBlank()) {
				return ResponseEntity.badRequest()
						.body(Map.of("status", "EMAIL_REQUIRED", "message", "Email is required"));
			}

			email = email.trim().toLowerCase();

			/* ================= OTP GENERATION ================= */
			String otp = generateOtp();

			// Store OTP for later validation
			otpService.storeOtp(email, otp);

			/* ================= SEND OTP EMAIL ================= */
			emailService.sendOtpEmail(email, otp);

			// Debug log for OTP generation and sending
			System.out.println("🔁 OTP Resent for email: " + email);
			System.out.println("OTP sent: " + otp); // Avoid printing OTP in production

			/* ================= RESPONSE ================= */
			return ResponseEntity.ok(Map.of("status", "OTP_RESENT", "message", "OTP resent successfully"));

		} catch (Exception e) {
			/* ================= ERROR LOG ================= */
			System.err.println("❌ OTP resend failed for email: " + email);
			e.printStackTrace();

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
					Map.of("status", "OTP_RESEND_FAILED", "message", "Unable to resend OTP. Please try again later."));
		}
	}

	
	
	@PostMapping("/final-submit")
	public ResponseEntity<?> finalSubmit(Authentication authentication,
	                                     HttpServletRequest request) {

	    String email = authentication.getName();
	    User user = userService.findByEmail(email);

	    if (user == null) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                .body(Map.of("message", "User not found"));
	    }

	    if (user.getPersonalStatus() != FormStatus.COMPLETED ||
	        user.getEducationStatus() != FormStatus.COMPLETED ||
	        user.getDocumentStatus() != FormStatus.COMPLETED ||
	        user.getPaymentStatus() != FormStatus.COMPLETED) {

	        return ResponseEntity.badRequest()
	                .body(Map.of("message", "Complete all sections before final submit"));
	    }

	    if (Boolean.TRUE.equals(user.getFinalSubmitted())) {
	        return ResponseEntity.badRequest()
	                .body(Map.of("message", "Already final submitted"));
	    }

	    user.setFinalSubmitted(true);
	    user.setFinalSubmittedAt(LocalDateTime.now());

	    // Optional: Track final submit activity
	    user.setLastLoginTime(LocalDateTime.now());

	    userService.save(user);

	    return ResponseEntity.ok(Map.of(
	            "message", "Application submitted successfully",
	            "submittedAt", user.getFinalSubmittedAt()
	    ));
	}
}
