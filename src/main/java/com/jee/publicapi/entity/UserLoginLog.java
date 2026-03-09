package com.jee.publicapi.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class UserLoginLog {

	 @Id
	    @GeneratedValue
	    private Long id;

	    private String ipAddress;
	    private LocalDateTime loginTime;

	    @ManyToOne
	    private User user;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getIpAddress() {
			return ipAddress;
		}

		public void setIpAddress(String ipAddress) {
			this.ipAddress = ipAddress;
		}

		public LocalDateTime getLoginTime() {
			return loginTime;
		}

		public void setLoginTime(LocalDateTime loginTime) {
			this.loginTime = loginTime;
		}

		public User getUser() {
			return user;
		}

		public void setUser(User user) {
			this.user = user;
		}
	    
	    
	    
}

