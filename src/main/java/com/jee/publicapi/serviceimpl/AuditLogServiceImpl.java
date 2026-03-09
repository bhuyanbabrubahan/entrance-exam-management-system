package com.jee.publicapi.serviceimpl;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jee.publicapi.entity.AuditLog;
import com.jee.publicapi.repository.AuditLogRepository;
import com.jee.publicapi.service.AuditLogService;
@Service
public class AuditLogServiceImpl implements AuditLogService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Override
    public void logChange(
            Integer applicationNumber,
            String sectionName,
            String fieldName,
            String oldValue,
            String newValue,
            String modifiedBy) {

        if (oldValue == null && newValue == null) return;

        if (oldValue != null && oldValue.equals(newValue)) return;

        AuditLog log = new AuditLog();
		/*
		 * log.setApplicationNumber(applicationNumber); log.setSectionName(sectionName);
		 * log.setFieldName(fieldName); log.setOldValue(oldValue);
		 * log.setNewValue(newValue); log.setModifiedAt(LocalDateTime.now());
		 * log.setModifiedBy(modifiedBy);
		 */

        auditLogRepository.save(log);
    }
}