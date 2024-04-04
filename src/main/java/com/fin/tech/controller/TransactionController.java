package com.fin.tech.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fin.tech.command.TransactionCommand;
import com.fin.tech.exception.ExceptionHandler;
import com.fin.tech.model.Person;
import com.fin.tech.repository.UserRepository;
import com.fin.tech.service.TransactionService;
import com.fin.tech.service.UserAuthenticationService;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
	@Autowired
	private TransactionService transactionService;
	
	@Autowired
	private UserRepository userRepository;

	@PostMapping("/transfer")
	public ResponseEntity<?> transferAmount(@RequestBody TransactionCommand cmd) {
		try {
			if (cmd.getFromEmail() == null || cmd.getFromEmail().isEmpty() || cmd.getToEmail() == null || cmd.getToEmail().isEmpty()
					|| cmd.getAmount() == null) {
				return ResponseEntity.badRequest().body("From Email, To Email and Transfer amount is mandatory.");
			}
			
			transactionService.initiateTransaction(cmd);
			List<Person> persons = userRepository.findAll();
			System.out.println(persons);
		} 
		catch (Exception e) {
			Map<String, Object> errorResponse = ExceptionHandler.buildErrorResponse(e);
			return ResponseEntity.status((int) errorResponse.get("status")).body(errorResponse);

		}

		return ResponseEntity.ok("Fund Successfully Transfered.");

	}

}
