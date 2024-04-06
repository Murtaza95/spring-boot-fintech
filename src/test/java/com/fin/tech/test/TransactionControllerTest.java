package com.fin.tech.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fin.tech.command.TransactionCommand;
import com.fin.tech.controller.TransactionController;
import com.fin.tech.exception.InsufficientBalanceException;
import com.fin.tech.exception.InvalidAmountException;
import com.fin.tech.exception.TransactionException;
import com.fin.tech.model.Person;
import com.fin.tech.repository.UserRepository;
import com.fin.tech.service.TransactionService;
import com.fin.tech.service.UserAuthenticationService;

/**
 * 
 * @author Murtaza Gillani
 *
 */
@ExtendWith(MockitoExtension.class)
public class TransactionControllerTest {
	private static final Logger logger = LoggerFactory.getLogger(UserControllerTest.class);
	
	@Mock
	private UserRepository userRepository;

	@Mock
	private UserAuthenticationService userAuthenticationService;

	@InjectMocks
	private TransactionService transactionService;

	@InjectMocks
	private TransactionController transactionController;
	
	@BeforeEach
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	

	@Test
	public void testTransferAmountInsufficientBalance() throws Exception {
		logger.info("Started the testTransferAmountInsufficientBalance test case");
		setupMockAuthentiation();
		TransactionCommand cmd = new TransactionCommand();
		cmd.setFromEmail("debit@example.com");
		cmd.setToEmail("credit@example.com");
		cmd.setAmount(new BigDecimal("1000"));

		Person debitUser = new Person();
		debitUser.setEmail("debit@example.com");
		debitUser.setBalance(new BigDecimal("500"));

		// Mock credit user
		Person creditUser = new Person();
		creditUser.setEmail("credit@example.com");
		creditUser.setBalance(new BigDecimal("1000"));

		when(userAuthenticationService.authenticateByEmail("debit@example.com")).thenReturn(debitUser);
		when(userAuthenticationService.authenticateByEmail("credit@example.com")).thenReturn(creditUser);

		InsufficientBalanceException exception = assertThrows(InsufficientBalanceException.class, () -> {
			transactionService.initiateTransaction(cmd);
		});

		// Assert the exception message
		assertEquals("Insufficient balance", exception.getMessage());
		logger.info("Completed the testTransferAmountInsufficientBalance test case");
	}

	@Test
	public void testTransferAmountExceptionThrown() {
		logger.info("Started the testTransferAmountExceptionThrown test case");
		TransactionCommand cmd = new TransactionCommand();
		cmd.setFromEmail("debit@example.com");
		cmd.setToEmail("credit@example.com");
		cmd.setAmount(new BigDecimal("100"));

		TransactionException exception = assertThrows(TransactionException.class, () -> {
			transactionService.initiateTransaction(cmd);
		});

		assertEquals("There is an error while doing the transaction. Your transaction is reverted.",
				exception.getMessage());
		logger.info("Finished the testTransferAmountExceptionThrown test case");
	}
	
	@Test
    public void testInitiateTransaction_InvalidAmountException() throws Exception {
		logger.info("Started the testInitiateTransaction_InvalidAmountException test case");
		setupMockAuthentiation();
        TransactionCommand cmd = new TransactionCommand();
        cmd.setFromEmail("debit@example.com");
        cmd.setToEmail("credit@example.com");
        cmd.setAmount(new BigDecimal("-100")); 
        
        Person debitUser = new Person();
		debitUser.setEmail(cmd.getFromEmail());
		debitUser.setBalance(new BigDecimal("100"));

		// Mock credit user
		Person creditUser = new Person();
		creditUser.setEmail(cmd.getToEmail());
		creditUser.setBalance(new BigDecimal("100"));

		when(userAuthenticationService.authenticateByEmail(debitUser.getEmail())).thenReturn(debitUser);
		when(userAuthenticationService.authenticateByEmail(creditUser.getEmail())).thenReturn(creditUser);

        InvalidAmountException exception = assertThrows(InvalidAmountException.class, () -> {
			transactionService.initiateTransaction(cmd);
		});

		assertEquals("Invalid amount.",exception.getMessage());
		logger.info("Finished the testInitiateTransaction_InvalidAmountException test case");
    }
	
	private void setupMockAuthentiation() {
		 // Mock authentication
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        
        // Mock UserDetails
        UserDetails userDetails = mock(UserDetails.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        // Mock UserDetails.getEmail() to return the expected email
        when(userDetails.getUsername()).thenReturn("debit@example.com");

        // Set up SecurityContextHolder to return the mock authentication
        SecurityContextHolder.getContext().setAuthentication(authentication);
	}

}
