package com.servicea.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class SessionDetails implements Serializable {
	private String username;
	private String sessionkey;
	private String jsessionid;
}
