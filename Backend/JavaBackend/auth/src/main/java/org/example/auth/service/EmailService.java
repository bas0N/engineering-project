package org.example.auth.service;

import org.example.auth.entity.User;

public interface EmailService {
    public void sendActivation(User user);

    public void sendPasswordRecovery(User user, String uid);

}
