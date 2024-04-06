package com.fin.tech.exception;
/**
 * 
 * @author Murtaza Gillani
 *
 */
public class InvalidAmountException extends RuntimeException {
    public InvalidAmountException(String message) {
        super(message);
    }
}