package com.jee.publicapi.service;

public interface AuditLogService {

    void logChange(
            Integer applicationNumber,
            String sectionName,
            String fieldName,
            String oldValue,
            String newValue,
            String modifiedBy
    );
}
