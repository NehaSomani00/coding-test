package com.lumen.fastivr.IVRCANST.Dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lumen.fastivr.IVRDto.WireCtrPrimaryNPANXX;

@ExtendWith(MockitoExtension.class)
public class ChangeLoopAssignmentInputDataTest {

	@InjectMocks
	ChangeLoopAssignmentInputData inputData;
	
	@InjectMocks
	private ChangeLoopAssignmentCurrentLoopDetails currentLoopDetails;
	
	@InjectMocks
	private ChangeLoopAssignmentReplacementLoopDetails replacementLoopDetails;	
	
	@InjectMocks
	private WireCtrPrimaryNPANXX wireCtrPrimaryNPANXX;
	
	@BeforeEach
	void setUp() throws Exception {
	}
	
	@Test
	void testChangeLoopAssignmentInputData() { 
		
		wireCtrPrimaryNPANXX.setNpa("");
		wireCtrPrimaryNPANXX.setNxx("");
		
		wireCtrPrimaryNPANXX.hashCode();
		wireCtrPrimaryNPANXX.equals(new WireCtrPrimaryNPANXX());
		wireCtrPrimaryNPANXX.toString();
		
		currentLoopDetails.setCableId("");
		currentLoopDetails.setCableUnitId("");
		
		currentLoopDetails.hashCode();
		currentLoopDetails.equals(new ChangeLoopAssignmentCurrentLoopDetails());
		currentLoopDetails.toString();
		
		replacementLoopDetails.setCableId("");
		replacementLoopDetails.setCableUnitId("");
		
		replacementLoopDetails.hashCode();
		replacementLoopDetails.equals(new ChangeLoopAssignmentReplacementLoopDetails());
		replacementLoopDetails.toString();
		
		
		inputData.hashCode();
		inputData.equals(new ChangeLoopAssignmentInputData());
		inputData.toString();
		
		inputData.setAutoSelectionFlag(false);
		inputData.setCableTroubleTicketIdentifier("tt");
		inputData.setChangeActionCode("A");
		inputData.setCircuitId("CID");
		inputData.setFacilityChangeReasonCode("");
		inputData.setLFACSEmployeeCode("EMP");
		inputData.setLfacsEntity("");
		inputData.setSegmentNumberSpecified(false);
		inputData.setSegNumber("1");
		inputData.setServiceOrderNumber("");
		inputData.setWiredOutOfLimit("");
		inputData.setWireCtrPrimaryNPANXX(wireCtrPrimaryNPANXX);		
		inputData.setCurrentLoopDetails(currentLoopDetails);
		inputData.setReplacementLoopDetails(replacementLoopDetails);
		
		assertEquals("1", inputData.getSegNumber());
		assertEquals("", inputData.getReplacementLoopDetails().getCableId());
		assertEquals("", inputData.getReplacementLoopDetails().getCableUnitId());
		assertEquals("", inputData.getCurrentLoopDetails().getCableId());
		assertEquals("", inputData.getCurrentLoopDetails().getCableUnitId());
	}
}
