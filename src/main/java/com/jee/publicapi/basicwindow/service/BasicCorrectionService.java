package com.jee.publicapi.basicwindow.service;

import com.jee.publicapi.basicwindow.dto.BasicCorrectionDetailsDTO;
import com.jee.publicapi.basicwindow.dto.BasicCorrectionUpdateDTO;

public interface BasicCorrectionService {

    BasicCorrectionDetailsDTO getCorrectionDetails(Long requestId);

    void updateBasicDetails(Long requestId, BasicCorrectionUpdateDTO dto);

}