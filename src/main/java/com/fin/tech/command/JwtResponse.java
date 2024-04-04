package com.fin.tech.command;

import java.text.SimpleDateFormat;
import java.util.Date;

public class JwtResponse {
	 private String token;
	 private String issuedAt;
	 private String expiresAt;

	 public JwtResponse(String token, Date issuedAt, Date expiresAt) {
	        this.token = token;
	        this.issuedAt = formatDate(issuedAt);
	        this.expiresAt = formatDate(expiresAt);
	    }
	 
	 private String formatDate(Date date) {
	        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        return dateFormat.format(date);
	    }

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String  getIssuedAt() {
		return issuedAt;
	}

	public void setIssuedAt(String  issuedAt) {
		this.issuedAt = issuedAt;
	}

	public String  getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(String  expiresAt) {
		this.expiresAt = expiresAt;
	}

}
