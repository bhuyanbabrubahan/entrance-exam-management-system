package com.jee.publicapi.payment.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.jee.publicapi.entity.PersonalDetails;
import com.jee.publicapi.entity.User;
import com.jee.publicapi.payment.dto.PaymentDetailsDTO;
import com.jee.publicapi.payment.service.FeeCalculationService;
import com.jee.publicapi.service.PersonalDetailsService;
import com.jee.publicapi.service.UserService;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "http://localhost:5173")
public class PaymentController {

    @Autowired
    private UserService userService;

    @Autowired
    private PersonalDetailsService personalDetailsService;

    @Autowired
    private FeeCalculationService feeCalculationService;

    /* =========================================================
       GET PAYMENT DETAILS
       ========================================================= */

    @GetMapping("/details")
    public ResponseEntity<?> getPaymentDetails(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        /* ===== GET USER FROM JWT ===== */

        User user = userService.getUserFromToken(authHeader);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid or missing token");
        }

        PaymentDetailsDTO res = new PaymentDetailsDTO();

        /* ===== USER INFO ===== */

        res.setFirstName(user.getFirstName());
        res.setMiddleName(user.getMiddleName());
        res.setLastName(user.getLastName());

        res.setApplicationNumber(
                user.getApplicationNumber() != null
                        ? user.getApplicationNumber().toString()
                        : null
        );

        /* ===== PERSONAL DETAILS ===== */

        PersonalDetails details = personalDetailsService.getPersonalDetails(user);

        if (details != null) {

            res.setCategory(details.getCategory());
            res.setDisability(details.getDisability());

            double fee = feeCalculationService.calculateFee(details);
            res.setFeeAmount(fee);
        }

        /* ===== LOGIN INFO (DIRECT FROM USER ENTITY) ===== */

        String loginTime =
                user.getLastLoginTime() != null
                        ? user.getLastLoginTime().toString()
                        : null;

        String ipAddress =
                user.getLastLoginIp() != null
                        ? user.getLastLoginIp()
                        : "—";

        res.setLoginTime(loginTime);
        res.setIpAddress(ipAddress);

        return ResponseEntity.ok(res);
    }
}