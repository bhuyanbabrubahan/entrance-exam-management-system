package com.jee.publicapi.correction.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jee.publicapi.correction.entity.UserCorrectionDocument;
import com.jee.publicapi.correction.entity.UserCorrectionRequest;

@Repository
public interface UserCorrectionDocumentRepository extends 
									JpaRepository<UserCorrectionDocument, Long> {

	 List<UserCorrectionDocument> findByCorrectionRequestId(Long requestId);
	
}
