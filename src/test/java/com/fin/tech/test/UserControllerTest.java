package com.fin.tech.test;

import static org.mockito.Mockito.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fin.tech.command.UserCommand;
import com.fin.tech.controller.UserController;
import com.fin.tech.repository.UserRepository;

/**
 * 
 * @author Murtaza Gillani
 *
 */
@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
	private static final Logger logger = LoggerFactory.getLogger(UserControllerTest.class);
	
	private MockMvc mockMvc;

	@Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserController userController;

    @Test
    public void testRegisterUserSuccess() throws Exception {
    	logger.info("Started the success test case.");
        UserCommand request = new UserCommand();
        request.setName("John");
        request.setEmail("john@example.com");
        request.setPassword("password");
        
        // Set up mockMvc
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        mockMvc.perform(MockMvcRequestBuilders.post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("User registered successfully"));
        logger.info("Finished the success test case.");
    }

    @Test
    public void testRegisterUserMissingData() throws Exception {
    	logger.info("Started the missing data test case.");
        UserCommand request = new UserCommand();
        request.setName(null);
        request.setEmail(null);
        request.setPassword(null);
        
        // Set up mockMvc
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        mockMvc.perform(MockMvcRequestBuilders.post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        logger.info("Finished the missing data test case.");
    }

    @Test
    public void testRegisterUserConstraint() throws Exception {
    	logger.info("Started the constratint test case.");
        UserCommand request = new UserCommand();
        request.setName("John");
        request.setEmail("john@example.com");
        request.setPassword("password");

        when(userRepository.save(any())).thenThrow(new DataIntegrityViolationException("The user with the same email address already exist."));

        // Set up mockMvc
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        mockMvc.perform(MockMvcRequestBuilders.post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
		        .andExpect(MockMvcResultMatchers.status().isNotAcceptable())
		        .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("User with the provided email already exist. Please use a different email."))  // Check error message
		        .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(406));
        logger.info("Finished the constratint test case.");
    }

    private String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
