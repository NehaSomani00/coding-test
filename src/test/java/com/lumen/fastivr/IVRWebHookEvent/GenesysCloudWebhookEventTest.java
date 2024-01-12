package com.lumen.fastivr.IVRWebHookEvent;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GenesysCloudWebhookEventTest {
	
	@InjectMocks GenesysCloudWebhookEvent event;
	@InjectMocks IVRDtmfInput input;
	
	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testGenesysCloudWebhookEvent() {
		
		List<IVRDtmfInput> inputs = new ArrayList<>();
		input.setDtmfInput("1");
		inputs.add(input);
		
		input.toString();
		input.equals(new IVRDtmfInput());
		input.canEqual(new IVRDtmfInput());
		input.hashCode();
		
		event.setCurrentState("state123");
		event.setSessionId("session123");
		event.setUserDtmfInputs("1");
		
		event.getSessionId();
		event.getCurrentState();
		String data = event.getUserDtmfInputs();
		event.toString();
		
		assertEquals("1", data);
	}

}
