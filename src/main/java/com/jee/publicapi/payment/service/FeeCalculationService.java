package com.jee.publicapi.payment.service;

import com.jee.publicapi.entity.PersonalDetails;

public interface FeeCalculationService {

    double calculateFee(PersonalDetails details);

}