package com.fin.tech.exception;

/**
 * 
 * @author Murtaza Gillani
 *
 */
public class UnauthorizedUserException  extends RuntimeException {
    public UnauthorizedUserException(String message) {
        super(message);
    }
}