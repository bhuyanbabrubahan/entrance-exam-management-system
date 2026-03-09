package com.jee.publicapi.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer applicationNumber;

    private String sectionName;  // PERSONAL / EDUCATION / DOCUMENT
    private String fieldName;

    @Column(length = 2000)
    private String oldValue;

    @Column(length = 2000)
    private String newValue;

    private LocalDateTime modifiedAt;

    private String modifiedBy; // USER or ADMIN
}