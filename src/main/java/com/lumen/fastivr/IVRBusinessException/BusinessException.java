package com.lumen.fastivr.IVRBusinessException;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class BusinessException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String sessionId ;
	private String state;

	public BusinessException() {
		// TODO Auto-generated constructor stub
	}

	public BusinessException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public BusinessException(String sessionId, String message) {
		this(message);
		this.sessionId = sessionId;
	}
	
	public BusinessException(String sessionId, String message, String state) {
		this(sessionId,message);
		this.state = state;
	}

	public BusinessException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public BusinessException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public BusinessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}
	
	
}
