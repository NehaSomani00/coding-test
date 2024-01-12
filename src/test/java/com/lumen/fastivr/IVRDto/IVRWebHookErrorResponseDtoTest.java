package com.lumen.fastivr.IVRDto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IVRWebHookErrorResponseDtoTest {

	@InjectMocks IVRWebHookErrorResponseDto errorDto;
	
	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testIVRWebHookErrorResponseDto() {
		errorDto.setSessionId("session123");
		errorDto.setMessage("message");
		errorDto.setState("state123");
		errorDto.getSessionId();
		errorDto.setResponseCode("");
		errorDto.getResponseCode();
		String state = errorDto.getState();
		errorDto.getMessage();
		
		errorDto.hashCode();
		errorDto.equals(new IVRWebHookErrorResponseDto());
		errorDto.toString();
		
		assertEquals("state123", state);
	}

}
