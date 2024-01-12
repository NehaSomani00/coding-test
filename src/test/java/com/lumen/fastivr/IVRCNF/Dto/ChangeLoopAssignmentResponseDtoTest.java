package com.lumen.fastivr.IVRCNF.Dto;

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
public class ChangeLoopAssignmentResponseDtoTest {
	
	@InjectMocks
	ChangeLoopAssignmentResponseDto changeLoopAssignmentResponseDto;
	
	@InjectMocks
	private ChangeLoopAssignmentResponseReturnDataSet returnDataSet;

	@InjectMocks
    private TargetSchemaVersionUsed targetSchemaVersionUsed;
	
	@InjectMocks
    private MessageStatus messageStatus;
	
	@InjectMocks
    private ARTISInformation artisInformation;
	
	@InjectMocks
	private ChangeLoopAssignmentResponseReturnDataSetCandidatePairInfo candidatePairInfo ;

	
	@BeforeEach
	void setUp() throws Exception {
	}
	
	@Test
	void testChangeLoopAssignmentResponseDtoTest() { 
		
		//List<String> ChangeLoopAssignmentResponseReturnDataSetCandidatePairInfo = new ChangeLoopAssignmentResponseReturnDataSetCandidatePairInfo
		candidatePairInfo.setBindingPostColorCode("");
		candidatePairInfo.setCableId("");
		candidatePairInfo.setCableUnitId("");
		
		candidatePairInfo.hashCode();
		candidatePairInfo.equals(new ChangeLoopAssignmentResponseReturnDataSetCandidatePairInfo());
		candidatePairInfo.toString();
		
		returnDataSet.setRequestStatus("");
		returnDataSet.setCircuitId("");
		returnDataSet.setTerminalId("");
		returnDataSet.setTerminationId("");
		//returnDataSet.setChangeLoopAssignmentResponseReturnDataSetCandidatePairInfo(candidatePairInfo);
		
		returnDataSet.hashCode();
		returnDataSet.equals(new ChangeLoopAssignmentResponseReturnDataSet());
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
		
		changeLoopAssignmentResponseDto.setCompletedTimeStamp("1");
		changeLoopAssignmentResponseDto.setCompletedTimeStampSpecified(true);
		changeLoopAssignmentResponseDto.setRequestId("1");
		changeLoopAssignmentResponseDto.setWebServiceName("");
		changeLoopAssignmentResponseDto.setArtisInformation(artisInformation);
		changeLoopAssignmentResponseDto.setMessageStatus(messageStatus);
		changeLoopAssignmentResponseDto.setReturnDataSet(returnDataSet);
		changeLoopAssignmentResponseDto.setTargetSchemaVersionUsed(targetSchemaVersionUsed);
		
		changeLoopAssignmentResponseDto.getArtisInformation();
		changeLoopAssignmentResponseDto.getCompletedTimeStamp();
		changeLoopAssignmentResponseDto.getMessageStatus();
		changeLoopAssignmentResponseDto.getRequestId();
		changeLoopAssignmentResponseDto.getReturnDataSet();
		changeLoopAssignmentResponseDto.getTargetSchemaVersionUsed();
		changeLoopAssignmentResponseDto.getWebServiceName();
		
		assertEquals("1", changeLoopAssignmentResponseDto.getCompletedTimeStamp());
		
	}
}
