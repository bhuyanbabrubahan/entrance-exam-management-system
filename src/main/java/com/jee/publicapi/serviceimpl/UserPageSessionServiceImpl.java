package com.jee.publicapi.serviceimpl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.jee.publicapi.entity.UserPageSession;
import com.jee.publicapi.repository.UserPageSessionRepository;
import com.jee.publicapi.service.UserPageSessionService;

@Service
public class UserPageSessionServiceImpl implements UserPageSessionService {

	private final UserPageSessionRepository repository;

	public UserPageSessionServiceImpl(UserPageSessionRepository repository) {
		this.repository = repository;
	}

	/* ================= START SESSION ================= */

	@Override
	public void startSession(String email, String pageName) {
	    if (email == null || pageName == null) return;

	    email = email.trim();
	    pageName = pageName.trim();

	    Optional<UserPageSession> existing = repository.findTopByEmailAndPageNameOrderByStartTimeDesc(email, pageName);

	    if (existing.isPresent()) {
	        UserPageSession session = existing.get();
	        if (session.getEndTime() == null) return; // already active
	    }

	    UserPageSession newSession = new UserPageSession();
	    newSession.setEmail(email);
	    newSession.setPageName(pageName);
	    newSession.setStartTime(LocalDateTime.now());
	    newSession.setEndTime(null);
	    newSession.setDurationSeconds(null);

	    repository.save(newSession);
	}

	/* ================= END SESSION ================= */

	@Override
	public void endSession(String email, String pageName) {

		if (email == null || pageName == null) {
			return;
		}

		email = email.trim();
		pageName = pageName.trim();

		Optional<UserPageSession> optional = repository.findTopByEmailAndPageNameOrderByStartTimeDesc(email, pageName);

		if (optional.isPresent()) {

			UserPageSession session = optional.get();

			if (session.getEndTime() == null) {

				LocalDateTime endTime = LocalDateTime.now();

				session.setEndTime(endTime);

				long seconds = Duration.between(session.getStartTime(), endTime).getSeconds();

				session.setDurationSeconds(seconds);

				repository.save(session);

				System.out.println("🔴 Page session ended: " + email + " → " + pageName + " (" + seconds + " sec)");
			}
		}
	}
}