package com.lumen.fastivr.IVRCANST.Dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lumen.fastivr.IVRDto.WireCtrPrimaryNPANXX;

@ExtendWith(MockitoExtension.class)
public class OrderStatusReturnDataSetTest {

	@InjectMocks
	private OrderStatusReturnDataSet returnDataSet;	
	
	@InjectMocks
	private WireCtrPrimaryNPANXX wireCtrPrimaryNPANXX;		
	
	@BeforeEach
	void setUp() throws Exception {
	}
	
	@Test
	void testOrderStatusResponse() { 
		
		wireCtrPrimaryNPANXX.setNpa("");
		wireCtrPrimaryNPANXX.setNxx("");
		
		wireCtrPrimaryNPANXX.hashCode();
		wireCtrPrimaryNPANXX.equals(new WireCtrPrimaryNPANXX());
		wireCtrPrimaryNPANXX.toString();
		
		returnDataSet.setAssignmentSectionPendingFlag("");
		returnDataSet.setLfacsMode("");
		returnDataSet.setLoopAssignmentStatus("");
		returnDataSet.setWireCtrPrimaryNPANXX(wireCtrPrimaryNPANXX);
		
		returnDataSet.hashCode();
		returnDataSet.equals(new OrderStatusReturnDataSet());
		returnDataSet.toString();
		
		assertEquals("", returnDataSet.getLfacsMode());
		assertEquals("", returnDataSet.getLoopAssignmentStatus());
		assertEquals(wireCtrPrimaryNPANXX, returnDataSet.getWireCtrPrimaryNPANXX());		
	}
	
}
