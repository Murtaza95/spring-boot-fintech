package com.fin.tech.controller;

import java.math.BigDecimal;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * 
 * @author Murtaza Gillani
 *
 */
@RestController
@RequestMapping("/users")
@Api(tags = "Users API")
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

	/**
	 * This endpoint will reister the user in the system.
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/register")
	@ApiOperation(value = "User Registration", notes = "This endpoint shall validates the data provided by the user.If the data is correct the "
			+ "the user is registered in the system. Otherwise error message will be shown in the response. ")
	@ApiResponses(value = {

			@ApiResponse(code = 200, message = "User registered successfully"),
			@ApiResponse(code = 406, message = "User with the provided email already exist. Please use a different email."), 
			@ApiResponse(code = 500, message = "Error registering user"), })
	public ResponseEntity<?> registerUser(@RequestBody UserCommand request) {
		try {
			if (request.getName() == null || request.getEmail() == null || request.getPassword() == null) {
				return ResponseEntity.badRequest().body("Name, email, and password are required");
			}

			Person user = new Person();
			user.setName(request.getName());
			user.setEmail(request.getEmail());
			user.setPassword(passwordEncoder.encode(request.getPassword()));
			user.setBalance(new BigDecimal("100"));
			userRepository.save(user);
			logger.info("User registered successfully..");
			return ResponseEntity.ok("User registered successfully");
		} catch (DataIntegrityViolationException cve) {
			Map<String, Object> errorResponse = ExceptionHandler.buildErrorResponse(cve);

			return ResponseEntity.status((int) errorResponse.get("status")).body(errorResponse);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error registering user");
		}
	}

	/**
	 * This endpoint shall authenticate the user and create the bearer token.
	 * 
	 * @param authenticationRequest
	 * @return
	 */

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	@ApiOperation(value = "Authentication", notes = "This endpoint will authenticate the user. On successfull authentication a Bearer Token shall be generated"
			+ "which valid for the 60 miutes.")
	@ApiResponses(value = {

			@ApiResponse(code = 200, message = "", response = JwtResponse.class),
			@ApiResponse(code = 401, message = "Invalid credentials"),
			@ApiResponse(code = 406, message = "User not found"),
			@ApiResponse(code = 401, message = "Unauthorized"), })
	public ResponseEntity<?> createAuthenticationToken(@RequestBody UserCommand authenticationRequest,HttpSession session) {
		try {

			final Person userDetails = userAuthenticationService.authenticate(authenticationRequest.getEmail(),
					authenticationRequest.getPassword());

			JwtResponse tokenResponse = jwtTokenUtil.generateToken(userDetails);

			return ResponseEntity.ok(tokenResponse);
		} catch (Exception e) {

			Map<String, Object> errorResponse = ExceptionHandler.buildErrorResponse(e);

			return ResponseEntity.status((int) errorResponse.get("status")).body(errorResponse);

		}
	}

}
