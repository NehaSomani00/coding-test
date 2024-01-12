package com.lumen.fastivr.IVRBusinessException;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import com.lumen.fastivr.IVRDto.IVRWebHookErrorResponseDto;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {
	
	@InjectMocks
	private GlobalExceptionHandler exceptionHandler;
	
	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testHandleBusinessException() {
		BusinessException be = new BusinessException("session123", "message", "state123");
		ResponseEntity<IVRWebHookErrorResponseDto> response = exceptionHandler.handleBusinessException(be);
		HttpStatus statusCode = response.getStatusCode();
		assertEquals(HttpStatus.BAD_REQUEST, statusCode);
	}

	@Test
	void testHandleBadUserInputException() {
		BadUserInputException bie = new BadUserInputException("session123", "message", "state123");
		ResponseEntity<IVRWebHookErrorResponseDto> response = exceptionHandler.handleBadUserInputException(bie);
		HttpStatus statusCode = response.getStatusCode();
		assertEquals(HttpStatus.BAD_REQUEST, statusCode);
	}

	@Test
	void testHandleExceptionException() {
		ResponseEntity<IVRWebHookErrorResponseDto> response = exceptionHandler.handleException(new Exception("message"));
		HttpStatus statusCode = response.getStatusCode();
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, statusCode);
	}

	@Test
	void testHandleHttpRequestMethodNotSupportedHttpRequestMethodNotSupportedExceptionHttpHeadersHttpStatusWebRequest() {
		ResponseEntity<Object> response = exceptionHandler.handleHttpRequestMethodNotSupported(new HttpRequestMethodNotSupportedException("POST"), null, null, null);
		HttpStatus statusCode = response.getStatusCode();
		assertEquals(HttpStatus.BAD_REQUEST, statusCode);
		
		
	}

}
