package org.example.auth.exceptions;

public class UserDontExistException extends RuntimeException{
    public UserDontExistException(String message) {
        super(message);
    }
}
