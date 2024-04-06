package com.fin.tech.exception;

import org.slf4j.Logger;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashMap;
import java.util.Map;
/**
 * 
 * @author Murtaza Gillani
 *
 */
public class ExceptionHandler {

    public static Map<String, Object> buildErrorResponse(Exception e) {
        String errorMessage;
        HttpStatus status;

        // Determine error message and HTTP status based on exception type
        if (e instanceof UserNotFoundException) {
            errorMessage = e.getMessage();
            status = HttpStatus.NOT_FOUND;
        } 
        else if (e instanceof InvalidCredentialsException) {
            errorMessage = e.getMessage();
            status = HttpStatus.UNAUTHORIZED;
        }
        else if (e instanceof InsufficientBalanceException) {
            errorMessage = e.getMessage();
            status = HttpStatus.NOT_ACCEPTABLE;
        }
        else if (e instanceof DataIntegrityViolationException) {
            errorMessage = "User with the provided email already exist. Please use a different email.";
            status = HttpStatus.NOT_ACCEPTABLE;
        }
        else if (e instanceof UsernameNotFoundException) {
            errorMessage =e.getMessage() ;
            status = HttpStatus.NOT_ACCEPTABLE;
        }
        else if (e instanceof InvalidAmountException) {
            errorMessage =e.getMessage() ;
            status = HttpStatus.NOT_ACCEPTABLE;
        }
        else if (e instanceof UnauthorizedUserException) {
            errorMessage =e.getMessage() ;
            status = HttpStatus.UNAUTHORIZED;
        }
        
        
        else if (e instanceof TransactionException) {
            errorMessage =e.getMessage() ;
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        
        else {
            errorMessage = "Internal server error";
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        // Construct JSON error response
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", errorMessage);
        errorResponse.put("status", status.value());
        return errorResponse;
    }
}
