package org.example.auth.service;


public interface EmailService {
    void sendRegistrationEmail(String email, String username, String verificationToken);
}
