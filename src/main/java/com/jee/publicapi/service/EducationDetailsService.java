package com.jee.publicapi.service;

import com.jee.publicapi.dto.EducationDetailsDTO;
import com.jee.publicapi.entity.EducationDetails;
import com.jee.publicapi.entity.User;

public interface EducationDetailsService {

    EducationDetails getOrCreate(User user);

    EducationDetails saveDraft(User user, EducationDetailsDTO dto);

    EducationDetails submit(User user, EducationDetailsDTO dto);
    
    
}