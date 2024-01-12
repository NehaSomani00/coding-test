package com.lumen.fastivr.IVRCANST.Dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ChangeLoopAssignmentReplacementLoopDetailsTest {

	@InjectMocks
	ChangeLoopAssignmentReplacementLoopDetails replacementLoopDetails;
	
	
	@BeforeEach
	void setUp() throws Exception {
	}
	
	@Test
	void testChangeLoopAssignmentResponseDtoTest() { 
		
		replacementLoopDetails.setCableId("");
		replacementLoopDetails.setCableUnitId("");
		replacementLoopDetails.setBindingPostColorCode("CC");
		replacementLoopDetails.setReplacementTerminalId("rt");
		
		replacementLoopDetails.hashCode();
		replacementLoopDetails.equals(new ChangeLoopAssignmentReplacementLoopDetails());
		replacementLoopDetails.toString();
		
		assertEquals("CC", replacementLoopDetails.getBindingPostColorCode());
		assertEquals("", replacementLoopDetails.getCableId());
		assertEquals("", replacementLoopDetails.getCableUnitId());
		assertEquals("rt", replacementLoopDetails.getReplacementTerminalId());
		
	}
}

