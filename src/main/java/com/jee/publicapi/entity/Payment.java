package com.jee.publicapi.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Payment {

    @Id
    @GeneratedValue
    private Long id;

    private String transactionId;
    private Double amount;
    private String status; // SUCCESS / FAILED / PENDING

    private LocalDateTime paymentDate;

    @ManyToOne
    private User user;
}

