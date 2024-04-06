package com.fin.tech.exception;

/**
 * 
 * @author Murtaza Gillani
 *
 */
public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}