package com.lumen.fastivr.IVRBusinessException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.lumen.fastivr.IVRDto.IVRWebHookErrorResponseDto;

import static com.lumen.fastivr.IVRUtils.IVRConstants.*;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
	
	final static Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	
//	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
//	@ExceptionHandler(value = ConstraintViolationException.class)
//	public ResponseEntity<Map<String, String>> handleValidationExceptions(ConstraintViolationException e) {
//		Map<String, String> errorMap = new HashMap<>();
//
//		e.getConstraintViolations().forEach(error -> {
//			String value = ((ConstraintViolationImpl<FastIvrUser>)error).getMessage();
//			String key = (String)((ConstraintViolationImpl<FastIvrUser>)error).getPropertyPath().toString();
//			errorMap.put(key, value);
//		});
//		return new ResponseEntity<Map<String,String>>(errorMap, HttpStatus.BAD_REQUEST);
//	}
	
	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<IVRWebHookErrorResponseDto> handleBusinessException(BusinessException ex) {
		LOGGER.error("Exception stack trace: ", ex);
		IVRWebHookErrorResponseDto errResponse = new IVRWebHookErrorResponseDto();
		errResponse.setSessionId(ex.getSessionId());
		errResponse.setMessage(GPDOWN_ERR_MSG + ":" + ex.getMessage());
		errResponse.setState(ex.getState());
		errResponse.setResponseCode(GPDOWN_ERR_MSG_CODE);
		
		return new ResponseEntity<IVRWebHookErrorResponseDto>(errResponse, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(BadUserInputException.class)
	public ResponseEntity<IVRWebHookErrorResponseDto> handleBadUserInputException(BadUserInputException ex) {
		LOGGER.error("Exception stack trace: ", ex);
		IVRWebHookErrorResponseDto errResponse = new IVRWebHookErrorResponseDto();
		errResponse.setSessionId(ex.getSessionId());
		errResponse.setMessage(GPDOWN_ERR_MSG + ":" + ex.getMessage());
		errResponse.setState(ex.getState());
		errResponse.setResponseCode(GPDOWN_ERR_MSG_CODE);
		
		return new ResponseEntity<IVRWebHookErrorResponseDto>(errResponse, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(SecurityUserCannotBeAddedException.class)
	public ResponseEntity<IVRWebHookErrorResponseDto> handleSecurityUserCannotBeAddedException(SecurityUserCannotBeAddedException ex) {
		LOGGER.error("Exception stack trace: ", ex);
		IVRWebHookErrorResponseDto errResponse = new IVRWebHookErrorResponseDto();
		errResponse.setMessage(GPDOWN_ERR_MSG + ":" + ex.getMessage());
		errResponse.setResponseCode(HttpStatus.NOT_ACCEPTABLE.toString());
		errResponse.setResponseCode(GPDOWN_ERR_MSG_CODE);
		
		return new ResponseEntity<IVRWebHookErrorResponseDto>(errResponse, HttpStatus.NOT_ACCEPTABLE);
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<IVRWebHookErrorResponseDto> handleException(Exception ex) {
		LOGGER.error("Exception stack trace: ", ex);
		IVRWebHookErrorResponseDto errResponse = new IVRWebHookErrorResponseDto();
		errResponse.setMessage(ex.getMessage());
		errResponse.setResponseCode(GPDOWN_ERR_MSG_CODE);
		errResponse.setMessage(GPDOWN_ERR_MSG);
		return new ResponseEntity<IVRWebHookErrorResponseDto>(errResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@Override
	public ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		return new ResponseEntity<Object>((String)ex.getMessage(), HttpStatus.BAD_REQUEST);
	}

}
