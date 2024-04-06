package com.fin.tech.exception;

/**
 * 
 * @author Murtaza Gillani
 *
 */
public class UserNotFoundException  extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}