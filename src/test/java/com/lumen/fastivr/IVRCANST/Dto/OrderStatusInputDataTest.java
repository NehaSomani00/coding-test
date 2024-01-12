package com.lumen.fastivr.IVRCANST.Dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lumen.fastivr.IVRDto.WireCtrPrimaryNPANXX;

@ExtendWith(MockitoExtension.class)
public class OrderStatusInputDataTest {

	@InjectMocks
	private OrderStatusInputData orderStatusInputData;	
	
	@InjectMocks
	private WireCtrPrimaryNPANXX wireCtrPrimaryNPANXX;		
	
	@BeforeEach
	void setUp() throws Exception {
	}
	
	@Test
	void testOrderStatusInputData() { 
		
		wireCtrPrimaryNPANXX.setNpa("");
		wireCtrPrimaryNPANXX.setNxx("");
		
		wireCtrPrimaryNPANXX.hashCode();
		wireCtrPrimaryNPANXX.equals(new WireCtrPrimaryNPANXX());
		wireCtrPrimaryNPANXX.toString();
		
		orderStatusInputData.hashCode();
		orderStatusInputData.equals(new OrderStatusInputData());
		orderStatusInputData.toString();
		
		orderStatusInputData.setLFACSEmployeeCode("");
		orderStatusInputData.setLfacsEntity("");
		orderStatusInputData.setServiceOrderNumber("1");
		orderStatusInputData.setWireCtrPrimaryNPANXX(wireCtrPrimaryNPANXX);
		
		assertEquals("", orderStatusInputData.getLfacsEntity());
		assertEquals("", orderStatusInputData.getLFACSEmployeeCode());
		assertEquals(wireCtrPrimaryNPANXX, orderStatusInputData.getWireCtrPrimaryNPANXX());	
		assertEquals("1", orderStatusInputData.getServiceOrderNumber());	
	}
}
