package com.jee.publicapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.jee.publicapi.entity.CorrectionWindow;

import jakarta.transaction.Transactional;

public interface CorrectionWindowRepository extends JpaRepository<CorrectionWindow, Long> {
	
	CorrectionWindow findByActiveTrue();

    @Modifying
    @Transactional
    @Query("UPDATE CorrectionWindow c SET c.active=false")
    void deactivateAllWindows();
}
