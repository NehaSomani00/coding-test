package com.lumen.fastivr.IVRCNF.Dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ARTISInfoObjectTest {
	
	@InjectMocks
	private ARTISInfoObject artisInfoObject;
	
	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testARTISInfoObject() { 
		artisInfoObject.setOverheadTime("5");
		artisInfoObject.setTotalTime("123");
		
		assertEquals("5", artisInfoObject.getOverheadTime());
		assertEquals("123", artisInfoObject.getTotalTime());
		
	}
	
	

}
