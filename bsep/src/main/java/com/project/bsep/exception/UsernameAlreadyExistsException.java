package com.project.bsep.exception;

public class UsernameAlreadyExistsException extends RuntimeException {

    public UsernameAlreadyExistsException(){}
    public UsernameAlreadyExistsException(String message) {
        super(message);
    }
}
