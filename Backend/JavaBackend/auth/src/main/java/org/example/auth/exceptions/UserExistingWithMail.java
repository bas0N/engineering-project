package org.example.auth.exceptions;

public class UserExistingWithMail extends RuntimeException{
    public UserExistingWithMail(String message) {
        super(message);
    }
}
