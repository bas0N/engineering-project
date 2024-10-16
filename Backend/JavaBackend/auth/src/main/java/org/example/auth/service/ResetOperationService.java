package org.example.auth.service;

import org.example.auth.entity.ResetOperations;
import org.example.auth.entity.User;

public interface ResetOperationService {
    ResetOperations initResetOperation(User user);
    void endOperation(String uid);
}
