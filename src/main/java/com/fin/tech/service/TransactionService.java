package com.fin.tech.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fin.tech.command.TransactionCommand;
import com.fin.tech.exception.InsufficientBalanceException;
import com.fin.tech.exception.TransactionException;
import com.fin.tech.model.Person;
import com.fin.tech.repository.UserRepository;

@Service
public class TransactionService {

	@Autowired
	private UserAuthenticationService userAuthenticationService;

	@Autowired
	private UserRepository userRepository;

	@Transactional(rollbackFor = TransactionException.class) // Enable transaction management and rollback for any
																// Exceptio
	public void initiateTransaction(TransactionCommand cmd) {
		List<Person> persons = userRepository.findAll();
		try {
			final Person debitUser = userAuthenticationService.authenticateByEmail(cmd.getFromEmail());
			final Person creditUser = userAuthenticationService.authenticateByEmail(cmd.getToEmail());
			// Check if debit user has sufficient balance
			if (debitUser.getBalance().compareTo(cmd.getAmount()) < 0) {
				throw new InsufficientBalanceException("Insufficient balance");
			}

			// Update debit user balance
			debitUser.setBalance(debitUser.getBalance().subtract(cmd.getAmount()));
			userRepository.save(debitUser);

			// Update credit user balance
			creditUser.setBalance(creditUser.getBalance().add(cmd.getAmount()));
			userRepository.save(creditUser);
			List<Person> persons2 = userRepository.findAll();
		} 
		catch (InsufficientBalanceException e) {
			throw e;
		} 
		catch (Exception e) {
			throw new TransactionException("There is an error while doing the transaction. Your transaction is reverted.");
		}

	}

}
