package com.lumen.fastivr.IVRCNF.Dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lumen.fastivr.IVRDto.WireCtrPrimaryNPANXX;

@ExtendWith(MockitoExtension.class)
public class ChangeLoopAssignmentRequestInputDataTest {

	@InjectMocks
	ChangeLoopAssignmentRequestInputData changeLoopAssignmentRequestInputData;
	
	@InjectMocks
	private WireCtrPrimaryNPANXX wireCtrPrimaryNPANXX;

	@InjectMocks
	private ChangeLoopAssignmentReqInputDataCurrentLoopDetails currentLoopDetails;

	@InjectMocks
	private ChangeLoopAssignmentReqInputDataReplacementLoopDetails replacementLoopDetails;

	@BeforeEach
	void setUp() throws Exception {
	}
	
	@Test
	void testChangeLoopAssignmentRequestInputDataTest() {
		
		changeLoopAssignmentRequestInputData.setLFACSEmployeeCode("EMP123");
		
		changeLoopAssignmentRequestInputData.setLfacsEntity("entity");
		
		wireCtrPrimaryNPANXX.setNpa("");
		wireCtrPrimaryNPANXX.setNxx("");
		
		wireCtrPrimaryNPANXX.hashCode();
		wireCtrPrimaryNPANXX.equals(new WireCtrPrimaryNPANXX());
		wireCtrPrimaryNPANXX.toString();
		
		changeLoopAssignmentRequestInputData.setServiceOrderNumber("123");
		
		changeLoopAssignmentRequestInputData.setCircuitId("CKID");
		
		currentLoopDetails.setCableId("");
		currentLoopDetails.setCableUnitId("");
		currentLoopDetails.setTerminalId("");
		
		currentLoopDetails.hashCode();
		currentLoopDetails.equals(new ChangeLoopAssignmentReqInputDataCurrentLoopDetails());
		currentLoopDetails.toString();
		
		replacementLoopDetails.setCableId("");
		replacementLoopDetails.setBindingPostColorCode("");
		replacementLoopDetails.setCableUnitId("");
		replacementLoopDetails.setReplacementTerminalId("");
	
		replacementLoopDetails.hashCode();
		replacementLoopDetails.equals(new ChangeLoopAssignmentReqInputDataReplacementLoopDetails());
		replacementLoopDetails.toString();
		
		changeLoopAssignmentRequestInputData.setChangeLoopAssignmentReqInputDataCurrentLoopDetails(currentLoopDetails);
		changeLoopAssignmentRequestInputData.setChangeLoopAssignmentReqInputDataReplacementLoopDetails(replacementLoopDetails);
		
		changeLoopAssignmentRequestInputData.setFacilityChangeReasonCode("");
		changeLoopAssignmentRequestInputData.setSegmentNumberSpecified(false);
		changeLoopAssignmentRequestInputData.setSegNumber("2");
		changeLoopAssignmentRequestInputData.setChangeActionCode("A");
		changeLoopAssignmentRequestInputData.setCableTroubleTicketIdentifier("");
		changeLoopAssignmentRequestInputData.setWiredOutOfLimit("");
		changeLoopAssignmentRequestInputData.setAutoSelectionFlag(false);
		
		assertEquals("EMP123", changeLoopAssignmentRequestInputData.getLFACSEmployeeCode());
		assertEquals("2", changeLoopAssignmentRequestInputData.getSegNumber());

		
	}
}
