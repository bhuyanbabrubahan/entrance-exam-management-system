package com.jee.publicapi.helper;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpSession;

@Component
public class SessionHelper {

    // Method to retrieve the message from the session
    public Message getMessageFromSession() {
        try {
            HttpSession session = ((ServletRequestAttributes) 
                RequestContextHolder.getRequestAttributes()).getRequest().getSession();
            Message message = (Message) session.getAttribute("message");
            // Optionally remove the message after retrieving it
            if (message != null) {
                session.removeAttribute("message");
            }
            return message;
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Return null in case of any exception
        }
    }

    // Method to remove a message from the session
    public void removeMessageFromSession() {
        try {
            HttpSession session = ((ServletRequestAttributes) 
                RequestContextHolder.getRequestAttributes()).getRequest().getSession();
            session.removeAttribute("message");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
