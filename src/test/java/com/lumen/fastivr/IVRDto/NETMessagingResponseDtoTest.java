package com.lumen.fastivr.IVRDto;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NETMessagingResponseDtoTest {
	
	@InjectMocks NETMessagingResponseDto request;
	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testNETMessagingDto() {
		
		request.setEventId("");
		request.setEventTime(LocalDateTime.now().toString());
		request.setReasonCode("200");
		request.setReasonDescription("Desp");
		request.setStatus("200");
		
		request.getEventId();
		request.getEventTime();
		request.getReasonCode();
		request.getReasonDescription();
		request.getStatus();
		
		request.toString();
	}

}
