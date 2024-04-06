package com.fin.tech.exception;

/**
 * 
 * @author Murtaza Gillani
 *
 */
public class TransactionException extends RuntimeException {
    public TransactionException(String message) {
        super(message);
    }
}