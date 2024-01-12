package com.lumen.fastivr.IVRCNF.Dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lumen.fastivr.IVRDto.AuthorizationInfo;
import com.lumen.fastivr.IVRDto.TargetSchemaVersionUsed;
import com.lumen.fastivr.IVRDto.WireCtrPrimaryNPANXX;

@ExtendWith(MockitoExtension.class)
public class MainChangeTicketLoopInputRequestTest {

	@InjectMocks
	MainChangeTicketLoopRequest mainChangeTricketLoopRequest;
	
	@InjectMocks
	TargetSchemaVersionUsed targetSchemaVersionUsed;
	
	@InjectMocks
	AuthorizationInfo authorizationInfo;
	
	@InjectMocks
	MainChangeTicketLoopInputRequest mainChangeTicketLoopInputRequest;
	
	@InjectMocks
	private WireCtrPrimaryNPANXX wireCtrPrimaryNPANXX;

	@InjectMocks
	private CurrentLoopDetails currentLoopDetails;

	@InjectMocks
	private ReplacementLoopDetails replacementLoopDetails;

	@BeforeEach
	void setUp() throws Exception {
	}
	
	@Test
	void testMainChangeTicketLoopInputRequestDto() {
		
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
		replacementLoopDetails.setCableTroubleTicketNumber("");
		replacementLoopDetails.setCableUnitId("");
		replacementLoopDetails.setFacilityChangeReason("");
		
		replacementLoopDetails.hashCode();
		replacementLoopDetails.equals(new ReplacementLoopDetails());
		replacementLoopDetails.toString();
		
		mainChangeTicketLoopInputRequest.setEmployeeId("");
		mainChangeTicketLoopInputRequest.setSegNumber("1");
		mainChangeTicketLoopInputRequest.setWireCtrPrimaryNPANXX(wireCtrPrimaryNPANXX);
		mainChangeTicketLoopInputRequest.setCurrentLoopDetails(currentLoopDetails);
		mainChangeTicketLoopInputRequest.setReplacementLoopDetails(replacementLoopDetails);
		
		mainChangeTicketLoopInputRequest.hashCode();
		mainChangeTicketLoopInputRequest.equals(new MainChangeTicketLoopInputRequest());
		mainChangeTicketLoopInputRequest.toString();
		
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
		
		mainChangeTricketLoopRequest.setInputData(mainChangeTicketLoopInputRequest);
		mainChangeTricketLoopRequest.setRequestId("");
		mainChangeTricketLoopRequest.setWebServiceName("");
		mainChangeTricketLoopRequest.setTimeOutSecond(1);
		mainChangeTricketLoopRequest.setRequestPurpose("");
		mainChangeTricketLoopRequest.setTargetSchemaVersionUsed(targetSchemaVersionUsed);
		mainChangeTricketLoopRequest.setAuthorizationInfo(authorizationInfo);
		
		mainChangeTricketLoopRequest.getAuthorizationInfo();
		mainChangeTricketLoopRequest.getInputData();
		mainChangeTricketLoopRequest.getRequestId();
		mainChangeTricketLoopRequest.getRequestPurpose();
		mainChangeTricketLoopRequest.getTargetSchemaVersionUsed();
		mainChangeTricketLoopRequest.getTimeOutSecond();
		mainChangeTricketLoopRequest.getWebServiceName();
		
		assertEquals("1", mainChangeTicketLoopInputRequest.getSegNumber());
	}
}
