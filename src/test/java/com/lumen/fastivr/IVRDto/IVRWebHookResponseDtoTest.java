package com.lumen.fastivr.IVRDto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IVRWebHookResponseDtoTest {

	@InjectMocks IVRWebHookResponseDto dto;
	@InjectMocks IVRParameter ivrParameter;
	
	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testIVRWebHookResponseDto() {
		List<IVRParameter> params = new ArrayList<>();
		params.add(ivrParameter);
		dto.setCurrentState("state123");
		dto.setHookReturnCode("1");
		dto.setHookReturnMessage("hook message");
		dto.setParameters(params);
		
		String currentState = dto.getCurrentState();
		dto.getSessionId();
		dto.getHookReturnCode();
		dto.getHookReturnMessage();
		dto.getParameters();
		ivrParameter.getData();
		
		dto.hashCode();
		dto.equals(new IVRWebHookErrorResponseDto());
		dto.toString();
		
		ivrParameter.hashCode();
		ivrParameter.toString();
		ivrParameter.equals(new IVRParameter());
		
		assertEquals("state123", currentState);
	}
}
