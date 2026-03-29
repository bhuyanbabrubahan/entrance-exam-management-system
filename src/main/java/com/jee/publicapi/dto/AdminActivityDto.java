package com.jee.publicapi.dto;

import java.time.LocalDateTime;

public class AdminActivityDto {

        private Long id;

        private String email;

        private String endpoint;

        private String method;

        private String ipAddress;

        private String browser;

        private String device;

        private LocalDateTime activityTime;

        private Long durationSeconds;

        private String pageName;
        
		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getEndpoint() {
			return endpoint;
		}

		public void setEndpoint(String endpoint) {
			this.endpoint = endpoint;
		}

		public String getMethod() {
			return method;
		}

		public void setMethod(String method) {
			this.method = method;
		}

		public String getIpAddress() {
			return ipAddress;
		}

		public void setIpAddress(String ipAddress) {
			this.ipAddress = ipAddress;
		}

		public String getBrowser() {
			return browser;
		}

		public void setBrowser(String browser) {
			this.browser = browser;
		}

		public String getDevice() {
			return device;
		}

		public void setDevice(String device) {
			this.device = device;
		}

		public LocalDateTime getActivityTime() {
			return activityTime;
		}

		public void setActivityTime(LocalDateTime activityTime) {
			this.activityTime = activityTime;
		}

		public Long getDurationSeconds() {
			return durationSeconds;
		}

		public void setDurationSeconds(Long durationSeconds) {
			this.durationSeconds = durationSeconds;
		}

		public String getPageName() {
			return pageName;
		}

		public void setPageName(String pageName) {
			this.pageName = pageName;
		}
        
        

}
