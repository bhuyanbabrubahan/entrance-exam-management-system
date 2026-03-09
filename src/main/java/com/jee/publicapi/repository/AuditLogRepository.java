package com.jee.publicapi.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.jee.publicapi.entity.AuditLog;

public interface AuditLogRepository 
extends JpaRepository<AuditLog, Long> {
}
