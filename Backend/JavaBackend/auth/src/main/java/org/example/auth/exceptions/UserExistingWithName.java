package org.example.auth.exceptions;

public class UserExistingWithName extends RuntimeException{
    public UserExistingWithName(String message) {
        super(message);
    }
}
