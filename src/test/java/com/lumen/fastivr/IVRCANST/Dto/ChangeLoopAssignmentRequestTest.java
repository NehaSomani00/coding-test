package com.lumen.fastivr.IVRCANST.Dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lumen.fastivr.IVRCNF.Dto.CurrentLoopDetails;
import com.lumen.fastivr.IVRCNF.Dto.ReplacementLoopDetails;
import com.lumen.fastivr.IVRDto.ARTISInformation;
import com.lumen.fastivr.IVRDto.AuthorizationInfo;
import com.lumen.fastivr.IVRDto.MessageStatus;
import com.lumen.fastivr.IVRDto.TargetSchemaVersionUsed;
import com.lumen.fastivr.IVRDto.WireCtrPrimaryNPANXX;

@ExtendWith(MockitoExtension.class)
public class ChangeLoopAssignmentRequestTest {

	@InjectMocks
	ChangeLoopAssignmentRequest changeLoopAssignmentRequest;
	 
	@InjectMocks
 	private ChangeLoopAssignmentInputData inputData;
	
	@InjectMocks
	private ChangeLoopAssignmentCurrentLoopDetails currentLoopDetails;
	
	@InjectMocks
	private ChangeLoopAssignmentReplacementLoopDetails replacementLoopDetails;	
	
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
		
		
		wireCtrPrimaryNPANXX.setNpa("");
		wireCtrPrimaryNPANXX.setNxx("");
		
		wireCtrPrimaryNPANXX.hashCode();
		wireCtrPrimaryNPANXX.equals(new WireCtrPrimaryNPANXX());
		wireCtrPrimaryNPANXX.toString();
		
		currentLoopDetails.setCableId("");
		currentLoopDetails.setCableUnitId("");
		
		currentLoopDetails.hashCode();
		currentLoopDetails.equals(new CurrentLoopDetails());
		currentLoopDetails.toString();
		
		replacementLoopDetails.setCableId("");
		replacementLoopDetails.setCableUnitId("");
		
		replacementLoopDetails.hashCode();
		replacementLoopDetails.equals(new ReplacementLoopDetails());
		replacementLoopDetails.toString();
		
		changeLoopAssignmentRequest.hashCode();
		changeLoopAssignmentRequest.equals(new ChangeLoopAssignmentRequest());
		changeLoopAssignmentRequest.toString();
		
		targetSchemaVersionUsed.setMajorVersionNumber(1);
		targetSchemaVersionUsed.setMinorVersionNumber(1);
		
		targetSchemaVersionUsed.hashCode();
		targetSchemaVersionUsed.equals(new TargetSchemaVersionUsed());
		targetSchemaVersionUsed.toString();
		
		authorizationInfo.setPassword("");
		authorizationInfo.setUserid("");
		
		authorizationInfo.hashCode();
		authorizationInfo.equals(new AuthorizationInfo());
		authorizationInfo.toString();
		
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
		
		changeLoopAssignmentRequest.setInputData(inputData);
		
		changeLoopAssignmentRequest.setRequestId("1");
		changeLoopAssignmentRequest.setWebServiceName("");
		changeLoopAssignmentRequest.setTimeOutSecond(1);
		changeLoopAssignmentRequest.setRequestPurpose("");
		changeLoopAssignmentRequest.setTargetSchemaVersionUsed(targetSchemaVersionUsed);
		changeLoopAssignmentRequest.setAuthorizationInfo(authorizationInfo);
		
		changeLoopAssignmentRequest.getAuthorizationInfo();
		changeLoopAssignmentRequest.getInputData();
		changeLoopAssignmentRequest.getRequestId();
		changeLoopAssignmentRequest.getRequestPurpose();
		changeLoopAssignmentRequest.getTargetSchemaVersionUsed();
		changeLoopAssignmentRequest.getTimeOutSecond();
		changeLoopAssignmentRequest.getWebServiceName();
		
		assertEquals("1", changeLoopAssignmentRequest.getRequestId());
		assertEquals("1", changeLoopAssignmentRequest.getInputData().getSegNumber());
		assertEquals("", changeLoopAssignmentRequest.getInputData().getReplacementLoopDetails().getCableId());
		assertEquals("", changeLoopAssignmentRequest.getInputData().getReplacementLoopDetails().getCableUnitId());
		assertEquals("", changeLoopAssignmentRequest.getInputData().getCurrentLoopDetails().getCableId());
		assertEquals("", changeLoopAssignmentRequest.getInputData().getCurrentLoopDetails().getCableUnitId());
		

		
	}
}
