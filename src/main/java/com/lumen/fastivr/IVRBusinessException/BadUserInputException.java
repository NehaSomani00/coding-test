package com.lumen.fastivr.IVRBusinessException;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class BadUserInputException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String sessionId ;
	private String state;

	public BadUserInputException() {
		// TODO Auto-generated constructor stub
	}

	public BadUserInputException(String message) {
		super(message);
	}

	public BadUserInputException(String sessionId, String message) {
		this(message);
		this.sessionId = sessionId;
	}
	
	public BadUserInputException(String sessionId, String message, String state) {
		this(sessionId,message);
		this.state = state;
	}

	public BadUserInputException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public BadUserInputException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public BadUserInputException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}
	
	
}
