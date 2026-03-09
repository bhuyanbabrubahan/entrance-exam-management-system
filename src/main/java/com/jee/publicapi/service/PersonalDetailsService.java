package com.jee.publicapi.service;

import com.jee.publicapi.dto.PersonalDetailsDTO;
import com.jee.publicapi.entity.PersonalDetails;
import com.jee.publicapi.entity.User;

public interface PersonalDetailsService {

    PersonalDetails getPersonalDetails(User user);

    PersonalDetailsDTO saveDraft(User user, PersonalDetailsDTO dto);

    PersonalDetailsDTO submit(User user, PersonalDetailsDTO dto);
}
