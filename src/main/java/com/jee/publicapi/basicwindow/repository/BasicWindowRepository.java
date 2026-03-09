package com.jee.publicapi.basicwindow.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jee.publicapi.basicwindow.entity.BasicWindow;

public interface BasicWindowRepository extends JpaRepository<BasicWindow, Long>{

	 boolean existsByRequestIdAndActiveTrue(Long requestId);

	 boolean existsByRequest_IdAndActiveTrueAndEndDateAfter(
		        Long requestId,
		        LocalDateTime time
		);
	    
	    boolean existsByRequestId(Long requestId);
	    
	    Optional<BasicWindow> findByRequest_Id(Long requestId);
	    Optional<BasicWindow> findTopByRequest_IdOrderByEndDateDesc(Long requestId);
	
}
