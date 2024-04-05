package com.fin.tech.test;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fin.tech.command.UserCommand;
import com.fin.tech.controller.UserController;
import com.fin.tech.exception.InvalidCredentialsException;
import com.fin.tech.jwt.JwtTokenUtil;
import com.fin.tech.model.Person;
import com.fin.tech.service.UserAuthenticationService;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {
	private static final Logger logger = LoggerFactory.getLogger(AuthenticationControllerTest.class);

    private MockMvc mockMvc;

    @Mock
    private UserAuthenticationService userAuthenticationService;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @InjectMocks
    private UserController userController;

    @Test
    void testCreateAuthenticationTokenSuccess() throws Exception {
    	logger.info("Started the testCreateAuthenticationToken_Success test case.");
        UserCommand authenticationRequest = new UserCommand();
        authenticationRequest.setEmail("test@example.com");
        authenticationRequest.setPassword("password");

        // Mock user details
        Person userDetails = new Person();
        userDetails.setEmail("test@example.com");
        userDetails.setPassword("password");

        // Mock authentication
        when(userAuthenticationService.authenticate("test@example.com", "password"))
                .thenReturn(userDetails);

        // Set up mockMvc
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        // Perform the POST request
        mockMvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(authenticationRequest)))
                .andExpect(status().isOk());
        logger.info("Finished the testCreateAuthenticationToken_Success  test case.");
    }

    @Test
    void testCreateAuthenticationTokenFailure() throws Exception {
    	logger.info("Started the testCreateAuthenticationToken_Failure test case.");
        UserCommand authenticationRequest = new UserCommand();
        authenticationRequest.setEmail("test@example.com");
        authenticationRequest.setPassword("password");

        // Mock authentication failure
        when(userAuthenticationService.authenticate("test@example.com", "password"))
                .thenThrow(new InvalidCredentialsException("Invalid credentials"));

        // Set up mockMvc
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        // Perform the POST request
        mockMvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(authenticationRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().json("{'error': 'Invalid credentials'}"));
        logger.info("Finished the testCreateAuthenticationToken_Failure test case.");
    }

    // Utility method to convert object to JSON string
    private String asJsonString(Object object) throws Exception {
        return new ObjectMapper().writeValueAsString(object);
    }
}

