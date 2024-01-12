package com.lumen.fastivr.IVRDto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NETMessagingRequestDtoTest {
	
	@InjectMocks NETMessagingRequestDto netRequest;
	
	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testNetRequestDto() {
		netRequest.setApplicationId("appid");
		netRequest.setApplicationKey("appKey");
		netRequest.setDevice("device");
		netRequest.setFrom("fastivr");
		netRequest.setTo("adxxx");
		netRequest.setMessageText("message");
		netRequest.setSubject("subject");
		netRequest.setSendType("page");
		
		String applicationId = netRequest.getApplicationId();
		netRequest.getApplicationKey();
		netRequest.getDevice();
		netRequest.getFrom();
		netRequest.getMessageText();
		netRequest.getSendType();
		netRequest.getSubject();
		netRequest.getTo();
		
		netRequest.toString();
		
		assertEquals("appid", applicationId);
	}

}
