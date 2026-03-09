package com.jee.publicapi.basicwindow.entity;

import com.jee.publicapi.correction.entity.UserCorrectionRequest;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "basic_correction_window")
public class BasicWindow {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "request_id")
    private UserCorrectionRequest request;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private boolean active;
    
    @Column(name = "deActivated_at")
    private LocalDateTime deActivatedAt;

    @Column(name = "deActivated_by_admin")
    private Boolean deActivatedByAdmin = false;

    // ===== Getters & Setters =====

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

    public LocalDateTime getDeActivatedAt() {
		return deActivatedAt;
	}

	public void setDeActivatedAt(LocalDateTime deActivatedAt) {
		this.deActivatedAt = deActivatedAt;
	}

	public Boolean getDeActivatedByAdmin() {
		return deActivatedByAdmin;
	}

	public void setDeActivatedByAdmin(Boolean deActivatedByAdmin) {
		this.deActivatedByAdmin = deActivatedByAdmin;
	}

	public UserCorrectionRequest getRequest() { return request; }


	public void setRequest(UserCorrectionRequest request) {
        this.request = request;
    }

    public LocalDateTime getStartDate() { return startDate; }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() { return endDate; }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public boolean isActive() { return active; }

    public void setActive(boolean active) {
        this.active = active;
    }
}