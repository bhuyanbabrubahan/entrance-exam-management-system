package com.jee.publicapi.dto;

public class AdminDashboardStatsDTO {
	
	private long totalCandidates;
    private long submittedApplications;
    private long pendingReviews;
    private long upcomingExams;
    
    
	public long getTotalCandidates() {
		return totalCandidates;
	}
	public void setTotalCandidates(long totalCandidates) {
		this.totalCandidates = totalCandidates;
	}
	public long getSubmittedApplications() {
		return submittedApplications;
	}
	public void setSubmittedApplications(long submittedApplications) {
		this.submittedApplications = submittedApplications;
	}
	public long getPendingReviews() {
		return pendingReviews;
	}
	public void setPendingReviews(long pendingReviews) {
		this.pendingReviews = pendingReviews;
	}
	public long getUpcomingExams() {
		return upcomingExams;
	}
	public void setUpcomingExams(long upcomingExams) {
		this.upcomingExams = upcomingExams;
	}
    
    

}
