package com.lumen.fastivr.IVRBusinessException;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class SecurityUserCannotBeAddedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public SecurityUserCannotBeAddedException() {
		// TODO Auto-generated constructor stub
	}

	public SecurityUserCannotBeAddedException(String message) {
		super(message);
	}

	public SecurityUserCannotBeAddedException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public SecurityUserCannotBeAddedException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public SecurityUserCannotBeAddedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}
	
	
}
