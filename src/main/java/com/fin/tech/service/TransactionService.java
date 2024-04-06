package com.fin.tech.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fin.tech.command.TransactionCommand;
import com.fin.tech.exception.InsufficientBalanceException;
import com.fin.tech.exception.InvalidAmountException;
import com.fin.tech.exception.TransactionException;
import com.fin.tech.model.Person;
import com.fin.tech.repository.UserRepository;

/**
 * 
 * @author Murtaza Gillani
 *
 */
@Service
public class TransactionService {

	@Autowired
	private UserAuthenticationService userAuthenticationService;

	@Autowired
	private UserRepository userRepository;

	@Transactional(rollbackFor = {TransactionException.class, Exception.class})
	public void initiateTransaction(TransactionCommand cmd) {
		try {
			 Person debitUser = userAuthenticationService.authenticateByEmail(cmd.getFromEmail());
			 Person creditUser = userAuthenticationService.authenticateByEmail(cmd.getToEmail());
			// Check if debit user has sufficient balance
			if (debitUser.getBalance().compareTo(cmd.getAmount()) < 0) {
				throw new InsufficientBalanceException("Insufficient balance");
			}
			if (cmd.getAmount().compareTo(BigDecimal.ZERO) < 0) {
				throw new InvalidAmountException("Invalid amount.");
			}

			// Update debit user balance
			debitUser.setBalance(debitUser.getBalance().subtract(cmd.getAmount()));
			userRepository.save(debitUser);

			// Update credit user balance
			creditUser.setBalance(creditUser.getBalance().add(cmd.getAmount()));
			userRepository.save(creditUser);
		} 
		catch (InsufficientBalanceException e) {
			throw e;
		} 
		catch (InvalidAmountException e) {
			throw e;
		} 
		catch (Exception e) {
			throw new TransactionException("There is an error while doing the transaction. Your transaction is reverted.");
		}

	}
	
	public ResponseEntity<?> test() {
		System.out.println("Hello");;
		return null;
	}

}
