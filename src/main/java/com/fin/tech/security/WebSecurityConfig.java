package com.fin.tech.security;

import javax.persistence.EntityManagerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.transaction.PlatformTransactionManager;

import com.fin.tech.repository.UserRepository;
import com.fin.tech.service.TransactionService;
import com.fin.tech.service.UserAuthenticationService;

@Configuration
@EnableWebSecurity
/**
 * 
 * @author Murtaza Gillani
 *
 */
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {


	@Autowired
	private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

	@Autowired
	private UserDetailsService jwtUserDetailsService;
	
	@Autowired
	private JwtRequestFilter jwtRequestFilter;
	
	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
	    httpSecurity.csrf().disable()
	            // Permit access to these particular requests without authentication
	            .authorizeRequests()
	            .antMatchers("/users/login", "/users/register", "/swagger-resources/**", "/swagger-ui/**", "/v2/api-docs/**", "/v3/api-docs.yaml", "/v3/api-docs.json").permitAll() // Permit access to Swagger UI resources
	                .anyRequest().authenticated()
	                .and()
	            //exception handling for unauthorized requests
	            .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint)
	            .and()
	            // Configure session management to be stateless
	            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

	    // Filter to validate the tokens with every request
	    httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
	}


	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(jwtUserDetailsService).passwordEncoder(passwordEncoder());
	}
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
	
	@Bean
	public UserAuthenticationService userAuthenticationService() {
		return new UserAuthenticationService();
	}
	
	
	@Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }
	

}

