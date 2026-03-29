package com.jee.publicapi.payment.serviceimpl;


import org.springframework.stereotype.Service;

import com.jee.publicapi.entity.PersonalDetails;
import com.jee.publicapi.payment.service.FeeCalculationService;


@Service
public class FeeCalculationServiceImpl implements FeeCalculationService {

    @Override
    public double calculateFee(PersonalDetails details) {

        double fee = 0;

        if (details == null) {
            return fee;
        }

        String category = details.getCategory();

        if (category == null) {
            return fee;
        }

        switch (category.toUpperCase()) {

            case "GEN":
                fee = 1000;
                break;

            case "OBC":
                fee = 800;
                break;

            case "SC":
            case "ST":
                fee = 200;
                break;

            case "EWS":
                fee = 0;
                break;

            default:
                fee = 1000;
        }

        // Disability override
        if ("YES".equalsIgnoreCase(details.getDisability())) {
            fee = 0;
        }

        return fee;
    }
}