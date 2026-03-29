package com.jee.publicapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jee.publicapi.entity.UserPageSession;

public interface UserPageSessionRepository
        extends JpaRepository<UserPageSession,Long>{

	Optional<UserPageSession>
    findTopByEmailAndPageNameOrderByStartTimeDesc(String email,String pageName);

    Optional<UserPageSession>
    findTopByEmailOrderByStartTimeDesc(String email);

}