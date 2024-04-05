package com.fin.tech.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fin.tech.command.JwtResponse;
import com.fin.tech.command.UserCommand;
import com.fin.tech.exception.ExceptionHandler;
import com.fin.tech.jwt.JwtTokenUtil;
import com.fin.tech.model.Person;
import com.fin.tech.repository.UserRepository;
import com.fin.tech.service.UserAuthenticationService;

@RestController
@RequestMapping("/users")
public class UserController {
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserAuthenticationService userAuthenticationService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@GetMapping("/hello")
	public String registerUser() {
		return "Hello World";
	}

	@PostMapping("/register")
	public ResponseEntity<?> registerUser(@RequestBody UserCommand request) {
		try {
			if (request.getName() == null || request.getEmail() == null || request.getPassword() == null) {
				return ResponseEntity.badRequest().body("Name, email, and password are required");
			}

			Person user = new Person();
			user.setName(request.getName());
			user.setEmail(request.getEmail());
			user.setPassword(passwordEncoder.encode(request.getPassword()));
			user.setBalance(new BigDecimal("1000.50"));
			userRepository.save(user);
			logger.info("User registered successfully..");
			return ResponseEntity.ok("User registered successfully");
		} 
		catch(DataIntegrityViolationException  cve) {
			Map<String, Object> errorResponse = ExceptionHandler.buildErrorResponse(cve);

			return ResponseEntity.status((int) errorResponse.get("status")).body(errorResponse);
		}
		catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error registering user");
		}
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ResponseEntity<?> createAuthenticationToken(@RequestBody UserCommand authenticationRequest) {
		try {

			final Person userDetails = userAuthenticationService.authenticate(authenticationRequest.getEmail(),
					authenticationRequest.getPassword());

			JwtResponse tokenResponse = jwtTokenUtil.generateToken(userDetails);

			return ResponseEntity.ok(tokenResponse);
		} 
		catch (Exception e) {

			Map<String, Object> errorResponse = ExceptionHandler.buildErrorResponse(e);

			return ResponseEntity.status((int) errorResponse.get("status")).body(errorResponse);

		}
	}

}
