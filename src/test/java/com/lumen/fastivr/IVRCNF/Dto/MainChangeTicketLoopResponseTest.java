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
public class MainChangeTicketLoopResponseTest {

	@InjectMocks
	MainChangeTicketLoopResponse mainChangeTicketLoopResponse;
	 
	@InjectMocks
 	private MainChangeTicketLoopDataSet returnDataSet;
	
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
	void testMainChangeTicketLoopInputResponseDto() {
		
		returnDataSet.setCableId("");
		returnDataSet.setCableUnitId("");
		returnDataSet.setCircuitId("");
		
		returnDataSet.hashCode();
		returnDataSet.equals(new MainChangeTicketLoopDataSet());
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
		
		mainChangeTicketLoopResponse.setCompletedTimeStamp("1");
		mainChangeTicketLoopResponse.setCompletedTimeStampSpecified(true);
		mainChangeTicketLoopResponse.setRequestId("1");
		mainChangeTicketLoopResponse.setWebServiceName("");
		mainChangeTicketLoopResponse.setArtisInformation(artisInformation);
		mainChangeTicketLoopResponse.setMessageStatus(messageStatus);
		mainChangeTicketLoopResponse.setReturnDataSet(returnDataSet);
		mainChangeTicketLoopResponse.setTargetSchemaVersionUsed(targetSchemaVersionUsed);
		
		mainChangeTicketLoopResponse.getArtisInformation();
		mainChangeTicketLoopResponse.getCompletedTimeStamp();
		mainChangeTicketLoopResponse.getMessageStatus();
		mainChangeTicketLoopResponse.getRequestId();
		mainChangeTicketLoopResponse.getReturnDataSet();
		mainChangeTicketLoopResponse.getTargetSchemaVersionUsed();
		mainChangeTicketLoopResponse.getWebServiceName();
		
		assertEquals("1", mainChangeTicketLoopResponse.getCompletedTimeStamp());
	}
}
