package com.fin.tech.exception;
/**
 * 
 * @author Murtaza Gillani
 *
 */
public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(String message) {
        super(message);
    }
}