package com.fin.tech.service;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.fin.tech.exception.InvalidCredentialsException;
import com.fin.tech.exception.UserNotFoundException;
import com.fin.tech.model.Person;
import com.fin.tech.repository.UserRepository;

public class UserAuthenticationService {

	@Autowired
	private UserDetailsService jwtInMemoryUserDetailsService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userRepository;

	public Person authenticate(String username, String password) throws Exception {
		Objects.requireNonNull(username);
		Objects.requireNonNull(password);

		UserDetails userDetails = jwtInMemoryUserDetailsService.loadUserByUsername(username);

		// Check if user exists
		if (userDetails == null) {
		    throw new UserNotFoundException("User not found");
		}

		
		  // Extract relevant information from UserDetails to create or retrieve a
		  Person user = userRepository.findByEmail(userDetails.getUsername()) .orElseThrow(() -> new
		  UserNotFoundException("User not found"));
		 

		// Check if password matches
		if (!passwordEncoder.matches(password, userDetails.getPassword())) {
		    throw new InvalidCredentialsException("Invalid credentials");
		}

		return user;

	}

	public Person authenticateByEmail(String username) throws Exception {
		Objects.requireNonNull(username);

		UserDetails userDetails = jwtInMemoryUserDetailsService.loadUserByUsername(username);

		// Check if user exists
		if (userDetails == null) {
			throw new UserNotFoundException("User not found");
		}
		
		 Person user = userRepository.findByEmail(userDetails.getUsername()) .orElseThrow(() -> new UserNotFoundException("User not found"));
		
		return user;
	}

}
