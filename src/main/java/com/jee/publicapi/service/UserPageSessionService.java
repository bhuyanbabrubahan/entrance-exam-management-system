package com.jee.publicapi.service;


public interface UserPageSessionService {

    void startSession(String email,String pageName);

    void endSession(String email,String pageName);

}