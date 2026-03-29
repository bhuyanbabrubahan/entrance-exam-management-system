package com.jee.publicapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jee.publicapi.entity.UserActivity;
// for admin 
public interface UserActivityRepository
        extends JpaRepository<UserActivity, Long> {

    List<UserActivity> findTop50ByOrderByActivityTimeDesc();

    List<UserActivity> findByEmailOrderByActivityTimeDesc(String email);
}