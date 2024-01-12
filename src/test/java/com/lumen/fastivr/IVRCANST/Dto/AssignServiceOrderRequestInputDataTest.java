package com.lumen.fastivr.IVRCANST.Dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lumen.fastivr.IVRDto.WireCtrPrimaryNPANXX;

@ExtendWith(MockitoExtension.class)
public class AssignServiceOrderRequestInputDataTest {
	
	@InjectMocks
	AssignServiceOrderRequestInputData assignServiceOrderRequestInputData;
	
	@InjectMocks
	private WireCtrPrimaryNPANXX wireCtrPrimaryNPANXX;

	@BeforeEach
	void setUp() throws Exception {
	}
	
	@Test
	void testChangeLoopAssignmentRequestInputDataTest() {
		
		assignServiceOrderRequestInputData.setLFACSEmployeeCode("EMP123");
		
		assignServiceOrderRequestInputData.setLfacsEntityCode("entity");
		
		wireCtrPrimaryNPANXX.setNpa("");
		wireCtrPrimaryNPANXX.setNxx("");
		
		wireCtrPrimaryNPANXX.hashCode();
		wireCtrPrimaryNPANXX.equals(new WireCtrPrimaryNPANXX());
		wireCtrPrimaryNPANXX.toString();
		
		assignServiceOrderRequestInputData.setWireCtrPrimaryNPANXX(wireCtrPrimaryNPANXX);
		
		assignServiceOrderRequestInputData.setServiceOrderNumber("123");
		
		assignServiceOrderRequestInputData.setCircuitId("CKID");
				
		assertEquals("EMP123", assignServiceOrderRequestInputData.getLFACSEmployeeCode());
		assertEquals("entity", assignServiceOrderRequestInputData.getLfacsEntityCode());
		assertEquals("123", assignServiceOrderRequestInputData.getServiceOrderNumber());
		assertEquals("CKID", assignServiceOrderRequestInputData.getCircuitId());
		assertEquals(wireCtrPrimaryNPANXX, assignServiceOrderRequestInputData.getWireCtrPrimaryNPANXX());

		
	}
	

}
