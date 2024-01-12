package com.lumen.fastivr.IVRCANST.Dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lumen.fastivr.IVRDto.ARTISInformation;
import com.lumen.fastivr.IVRDto.MessageStatus;
import com.lumen.fastivr.IVRDto.TargetSchemaVersionUsed;

@ExtendWith(MockitoExtension.class)
public class ChangeLoopAssignmentResponseTest {

	@InjectMocks
	ChangeLoopAssignmentResponse changeLoopAssignmentResponse;
	
	@InjectMocks
	private ChangeLoopAssignmentReturnDataSet returnDataSet;

	@InjectMocks
    private TargetSchemaVersionUsed targetSchemaVersionUsed;
	
	@InjectMocks
    private MessageStatus messageStatus;
	
	@InjectMocks
    private ARTISInformation artisInformation;
	
	@BeforeEach
	void setUp() throws Exception {
	}
	
	@Test
	void testChangeLoopAssignmentResponse() { 
		
		returnDataSet.setRequestStatus("");
		returnDataSet.setCircuitId("");
		returnDataSet.setTerminalId("");
		returnDataSet.setTerminationId("");
		
		returnDataSet.hashCode();
		returnDataSet.equals(new ChangeLoopAssignmentReturnDataSet());
		returnDataSet.toString();

		targetSchemaVersionUsed.setMajorVersionNumber(1);
		targetSchemaVersionUsed.setMinorVersionNumber(1);
		
		targetSchemaVersionUsed.hashCode();
		targetSchemaVersionUsed.equals(new TargetSchemaVersionUsed());
		targetSchemaVersionUsed.toString();
		
		messageStatus.setErrorCode("");
		messageStatus.setErrorMessage("");
		messageStatus.setErrorStatus("");
		
		messageStatus.hashCode();
		messageStatus.equals(new MessageStatus());
		messageStatus.toString();
		
		artisInformation.hashCode();
		artisInformation.equals(new ARTISInformation());
		artisInformation.toString();
		
		changeLoopAssignmentResponse.hashCode();
		changeLoopAssignmentResponse.equals(new ChangeLoopAssignmentResponse());
		changeLoopAssignmentResponse.toString();
		
		changeLoopAssignmentResponse.setCompletedTimeStamp("1");
		changeLoopAssignmentResponse.setCompletedTimeStampSpecified(true);
		changeLoopAssignmentResponse.setRequestId("1");
		changeLoopAssignmentResponse.setWebServiceName("");
		changeLoopAssignmentResponse.setArtisInformation(artisInformation);
		changeLoopAssignmentResponse.setMessageStatus(messageStatus);
		changeLoopAssignmentResponse.setReturnDataSet(returnDataSet);
		changeLoopAssignmentResponse.setTargetSchemaVersionUsed(targetSchemaVersionUsed);
		
		changeLoopAssignmentResponse.getArtisInformation();
		changeLoopAssignmentResponse.getCompletedTimeStamp();
		changeLoopAssignmentResponse.getMessageStatus();
		changeLoopAssignmentResponse.getRequestId();
		changeLoopAssignmentResponse.getReturnDataSet();
		changeLoopAssignmentResponse.getTargetSchemaVersionUsed();
		changeLoopAssignmentResponse.getWebServiceName();
		
		assertEquals("1", changeLoopAssignmentResponse.getCompletedTimeStamp());

	}	
	
}
