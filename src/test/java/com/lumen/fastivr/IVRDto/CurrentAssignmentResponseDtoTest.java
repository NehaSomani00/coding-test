/**
 * 
 */
package com.lumen.fastivr.IVRDto;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * 
 */
@ExtendWith(MockitoExtension.class)
public class CurrentAssignmentResponseDtoTest {

	@InjectMocks
	CurrentAssignmentResponseDto currentAssignmentResponseDto;
	
	@InjectMocks
	ReturnDataSet returnDataSet;
	
	@InjectMocks
	TargetSchemaVersionUsed targetSchemaVersionUsed;
	
	@InjectMocks
	MessageStatus messageStatus;
	
	@InjectMocks
	HostErrorList hostErrorList;
	
	@InjectMocks
	ARTISInformation artisInformation;
	
	@BeforeEach
	void setUp() throws Exception {
	}
	
	@Test
	void testCurrentAssignmentResponseDto() {
		
		returnDataSet.setPort1("2");
		hostErrorList.setId("1");
		
		List<HostErrorList>hostErrorLists=new ArrayList<>();
		hostErrorLists.add(hostErrorList);
		
		targetSchemaVersionUsed.setMajorVersionNumber(1);
		targetSchemaVersionUsed.setMinorVersionNumber(0);
		
		messageStatus.setErrorCode("1");
		messageStatus.setErrorMessage("Test");
		messageStatus.setErrorStatus("errorStatus");
		messageStatus.setHostErrorList(hostErrorLists);
		messageStatus.setSeverityLevel("SeverityLevel");
		
		artisInformation.setOverheadTime("overheadTime");
		artisInformation.setTotalTime("TotalTime");
		
		currentAssignmentResponseDto.setCompletedTimeStamp("tempTimeStamp");
		currentAssignmentResponseDto.setCompletedTimeStampSpecified(true);
		currentAssignmentResponseDto.setRequestId("1");
		currentAssignmentResponseDto.setWebServiceName("TempWebservice");
		currentAssignmentResponseDto.setReturnDataSet(returnDataSet);
		currentAssignmentResponseDto.setTargetSchemaVersionUsed(targetSchemaVersionUsed);
		currentAssignmentResponseDto.setMessageStatus(messageStatus);
		currentAssignmentResponseDto.setArtisInformation(artisInformation);
		
		returnDataSet.hashCode();
		returnDataSet.equals(new ReturnDataSet());
		returnDataSet.toString();
		
		targetSchemaVersionUsed.hashCode();
		targetSchemaVersionUsed.equals(new TargetSchemaVersionUsed());
		targetSchemaVersionUsed.toString();
		
		messageStatus.hashCode();
		messageStatus.equals(new MessageStatus());
		messageStatus.toString();
		
		artisInformation.hashCode();
		artisInformation.equals(new ARTISInformation());
		artisInformation.toString();
		
		currentAssignmentResponseDto.hashCode();
		currentAssignmentResponseDto.equals(new CurrentAssignmentResponseDto());
		currentAssignmentResponseDto.toString();
		
		assertEquals("1", currentAssignmentResponseDto.getRequestId());
	}
}
