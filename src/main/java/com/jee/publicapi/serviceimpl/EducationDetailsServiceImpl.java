package com.jee.publicapi.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jee.publicapi.dto.EducationDetailsDTO;
import com.jee.publicapi.entity.EducationDetails;
import com.jee.publicapi.entity.User;
import com.jee.publicapi.enums.FormStatus;
import com.jee.publicapi.repository.EducationDetailsRepository;
import com.jee.publicapi.service.EducationDetailsService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class EducationDetailsServiceImpl
        implements EducationDetailsService {

    @Autowired
    private EducationDetailsRepository repository;

    @Override
    public EducationDetails getOrCreate(User user) {

        EducationDetails edu =
                repository.findByUser(user).orElse(null);

        System.out.println("[SERVICE] getOrCreate -> exists = " + (edu != null));

        return edu;
    }

    @Override
    public EducationDetails saveDraft(User user, EducationDetailsDTO dto) {

        System.out.println("[SERVICE] Saving Draft...");

        EducationDetails edu =
                repository.findByUser(user)
                        .orElse(new EducationDetails());

        edu.setUser(user);

        // ==== MAP FIELDS USING GETTERS ====
        edu.setBoard10(dto.getBoard10());
        edu.setSchoolName10(dto.getSchoolName10());
        edu.setRollNumber10(dto.getRollNumber10());
        edu.setPassingYear10(dto.getPassingYear10());
        edu.setMarksType10(dto.getMarksType10());
        edu.setPercentage10(dto.getPercentage10());

        edu.setBoard12(dto.getBoard12());
        edu.setSchoolName12(dto.getSchoolName12());
        edu.setRollNumber12(dto.getRollNumber12());
        edu.setPassingYear12(dto.getPassingYear12());
        edu.setStream12(dto.getStream12());
        edu.setMarksType12(dto.getMarksType12());
        edu.setPercentage12(dto.getPercentage12());
        edu.setPcmPercentage(dto.getPcmPercentage());

        edu.setExamStatus(dto.getExamStatus());
        edu.setAppearingYear(dto.getAppearingYear());

        edu.setEducationStatus(FormStatus.IN_PROGRESS);
        user.setEducationStatus(FormStatus.IN_PROGRESS);

        repository.save(edu);

        System.out.println("[SERVICE] Draft saved");
        return edu;
    }

    @Override
    public EducationDetails submit(User user, EducationDetailsDTO dto) {

        System.out.println("[SERVICE] Submitting...");

        EducationDetails edu = saveDraft(user, dto);

        edu.setEducationStatus(FormStatus.COMPLETED);
        user.setEducationStatus(FormStatus.COMPLETED);

        repository.save(edu);

        System.out.println("[SERVICE] Submission completed");

        return edu;
    }
}