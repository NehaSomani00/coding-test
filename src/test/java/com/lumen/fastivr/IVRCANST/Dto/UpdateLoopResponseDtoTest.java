package com.lumen.fastivr.IVRCANST.Dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lumen.fastivr.IVRCNF.Dto.ChangeLoopAssignmentResponseReturnDataSet;
import com.lumen.fastivr.IVRDto.ARTISInformation;
import com.lumen.fastivr.IVRDto.MessageStatus;
import com.lumen.fastivr.IVRDto.TargetSchemaVersionUsed;
import com.lumen.fastivr.IVRDto.WireCtrPrimaryNPANXX;

@ExtendWith(MockitoExtension.class)
public class UpdateLoopResponseDtoTest {

	@InjectMocks
	UpdateLoopResponseDto updateLoopResponseDto;
	
	@InjectMocks
	private ChangeLoopAssignmentResponseReturnDataSet returnDataSet;	
	
	@InjectMocks
    private TargetSchemaVersionUsed targetSchemaVersionUsed;
	
	@InjectMocks
    private MessageStatus messageStatus;
	
	@InjectMocks
    private ARTISInformation artisInformation;
	
	@InjectMocks
	private WireCtrPrimaryNPANXX wireCtrPrimaryNPANXX;		
	
	@BeforeEach
	void setUp() throws Exception {
	}
	
	@Test
	void testupdateLoopResponseDto() { 
		
		wireCtrPrimaryNPANXX.setNpa("");
		wireCtrPrimaryNPANXX.setNxx("");
		
		wireCtrPrimaryNPANXX.hashCode();
		wireCtrPrimaryNPANXX.equals(new WireCtrPrimaryNPANXX());
		wireCtrPrimaryNPANXX.toString();
		
		returnDataSet.setCircuitId("");
		returnDataSet.setRequestStatus("");
		returnDataSet.setTerminalId("");
		returnDataSet.setTerminationId("");
		
		
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
		
		updateLoopResponseDto.hashCode();
		updateLoopResponseDto.equals(new UpdateLoopResponseDto());
		updateLoopResponseDto.toString();
		
		updateLoopResponseDto.setCompletedTimeStamp("1");
		updateLoopResponseDto.setCompletedTimeStampSpecified(true);
		updateLoopResponseDto.setRequestId("1");
		updateLoopResponseDto.setWebServiceName("");
		updateLoopResponseDto.setArtisInformation(artisInformation);
		updateLoopResponseDto.setMessageStatus(messageStatus);
		updateLoopResponseDto.setReturnDataSet(returnDataSet);
		updateLoopResponseDto.setTargetSchemaVersionUsed(targetSchemaVersionUsed);
		
		updateLoopResponseDto.getArtisInformation();
		updateLoopResponseDto.getCompletedTimeStamp();
		updateLoopResponseDto.getMessageStatus();
		updateLoopResponseDto.getRequestId();
		updateLoopResponseDto.getReturnDataSet();
		updateLoopResponseDto.getTargetSchemaVersionUsed();
		updateLoopResponseDto.getWebServiceName();
	
		assertEquals("1", updateLoopResponseDto.getRequestId());	
		assertEquals("", updateLoopResponseDto.getReturnDataSet().getCircuitId());
		assertEquals("", updateLoopResponseDto.getReturnDataSet().getRequestStatus());
		assertEquals("", updateLoopResponseDto.getReturnDataSet().getTerminalId());
	}
	
}
