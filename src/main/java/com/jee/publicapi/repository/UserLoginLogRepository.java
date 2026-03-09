package com.jee.publicapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jee.publicapi.entity.User;
import com.jee.publicapi.entity.UserLoginLog;

@Repository
public interface UserLoginLogRepository
        extends JpaRepository<UserLoginLog, Long> {

    UserLoginLog findTopByUserOrderByLoginTimeDesc(User user);
    UserLoginLog findTopByUserEmailOrderByLoginTimeDesc(String email);
  
}