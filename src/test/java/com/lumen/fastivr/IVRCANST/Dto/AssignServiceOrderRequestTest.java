package com.lumen.fastivr.IVRCANST.Dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lumen.fastivr.IVRDto.ARTISInformation;
import com.lumen.fastivr.IVRDto.AuthorizationInfo;
import com.lumen.fastivr.IVRDto.MessageStatus;
import com.lumen.fastivr.IVRDto.TargetSchemaVersionUsed;
import com.lumen.fastivr.IVRDto.WireCtrPrimaryNPANXX;

@ExtendWith(MockitoExtension.class)
public class AssignServiceOrderRequestTest {
	
	@InjectMocks
	AssignServiceOrderRequest assignServiceOrderRequest;
	 
	@InjectMocks
	AssignServiceOrderRequestInputData assignServiceOrderRequestInputData;
	
	@InjectMocks
    private MessageStatus messageStatus;
	
	@InjectMocks
    private ARTISInformation artisInformation;
	
	@InjectMocks
	private WireCtrPrimaryNPANXX wireCtrPrimaryNPANXX;	
	
	@InjectMocks
	TargetSchemaVersionUsed targetSchemaVersionUsed;
	
	@InjectMocks
	AuthorizationInfo authorizationInfo;	
	
	@BeforeEach
	void setUp() throws Exception {
	}
	
	@Test
	void testChangeLoopAssignmentRequest() {
		
		assignServiceOrderRequestInputData.hashCode();
		assignServiceOrderRequestInputData.equals(new AssignServiceOrderRequestInputData());
		assignServiceOrderRequestInputData.toString();
		
		assignServiceOrderRequestInputData.setLFACSEmployeeCode("EMP123");
		
		assignServiceOrderRequestInputData.setLfacsEntityCode("entity");
		
		wireCtrPrimaryNPANXX.hashCode();
		wireCtrPrimaryNPANXX.equals(new WireCtrPrimaryNPANXX());
		wireCtrPrimaryNPANXX.toString();
		
		wireCtrPrimaryNPANXX.setNpa("");
		wireCtrPrimaryNPANXX.setNxx("");
		
		assignServiceOrderRequestInputData.hashCode();
		assignServiceOrderRequestInputData.equals(new AssignServiceOrderRequestInputData());
		assignServiceOrderRequestInputData.toString();
		
		assignServiceOrderRequestInputData.setWireCtrPrimaryNPANXX(wireCtrPrimaryNPANXX);
		
		assignServiceOrderRequestInputData.setServiceOrderNumber("123");
		
		assignServiceOrderRequestInputData.setCircuitId("CKID");
		
		assignServiceOrderRequest.setInputData(assignServiceOrderRequestInputData);
				
		assertEquals("EMP123", assignServiceOrderRequestInputData.getLFACSEmployeeCode());
		assertEquals("entity", assignServiceOrderRequestInputData.getLfacsEntityCode());
		assertEquals("123", assignServiceOrderRequestInputData.getServiceOrderNumber());
		assertEquals("CKID", assignServiceOrderRequestInputData.getCircuitId());
		assertEquals(wireCtrPrimaryNPANXX, assignServiceOrderRequestInputData.getWireCtrPrimaryNPANXX());

		
	}


}
