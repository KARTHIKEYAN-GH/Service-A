package com.servicea.dto;

import lombok.Data;

@Data
public class SessionInfo {
	private String username;
	private String sessionKey;
	
	public SessionInfo(String username, String sessionKey) {
		this.username = username;
		this.sessionKey = sessionKey;
	}
}
