package com.fin.tech.test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fin.tech.command.TransactionCommand;
import com.fin.tech.controller.TransactionController;
import com.fin.tech.exception.InsufficientBalanceException;
import com.fin.tech.exception.TransactionException;
import com.fin.tech.model.Person;
import com.fin.tech.repository.UserRepository;
import com.fin.tech.service.TransactionService;
import com.fin.tech.service.UserAuthenticationService;

@ExtendWith(MockitoExtension.class)
public class TransactionControllerTest {

    private MockMvc mockMvc;
    
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
    public void testTransferAmount_InsufficientBalance() throws Exception {
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

        when(userRepository.findAll()).thenReturn(new ArrayList<>());
        when(userAuthenticationService.authenticateByEmail("debit@example.com")).thenReturn(debitUser);
        when(userAuthenticationService.authenticateByEmail("credit@example.com")).thenReturn(creditUser);

        InsufficientBalanceException exception = assertThrows(InsufficientBalanceException.class, () -> {
            transactionService.initiateTransaction(cmd);
        });

        // Assert the exception message
        assertEquals("Insufficient balance", exception.getMessage());
    }

    @Test
    public void testTransferAmount_ExceptionThrown() {
        TransactionCommand cmd = new TransactionCommand();
        cmd.setFromEmail("debit@example.com");
        cmd.setToEmail("credit@example.com");
        cmd.setAmount(new BigDecimal("100"));


        TransactionException exception = assertThrows(TransactionException.class, () -> {
            transactionService.initiateTransaction(cmd);
        });

        assertEquals("There is an error while doing the transaction. Your transaction is reverted.", exception.getMessage());
    }
    private String asJsonString(Object object) throws Exception {
        return new ObjectMapper().writeValueAsString(object);
    }
}
