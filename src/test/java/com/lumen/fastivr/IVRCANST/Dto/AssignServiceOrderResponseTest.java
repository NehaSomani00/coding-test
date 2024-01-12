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
public class AssignServiceOrderResponseTest {
	
	@InjectMocks
	AssignServiceOrderResponse assignServiceOrderResponse;
	
	@InjectMocks
    private TargetSchemaVersionUsed targetSchemaVersionUsed;
	
	@InjectMocks
    private MessageStatus messageStatus;
	
	@InjectMocks
    private ARTISInformation artisInformation;
	
	@InjectMocks
	private AssignServiceOrderResponseReturnDataSet returnDataSet;

	@InjectMocks
	AssignServiceOrderResponseReturnDataSetCandidatePairInfo candidatePairInfo;
	
	@BeforeEach
	void setUp() throws Exception {
	}
	
	@Test
	void testAssignServiceOrderResponseTest() { 
		
		candidatePairInfo.setBindingPostColorCode("");
		candidatePairInfo.setCableId("");
		candidatePairInfo.setCableUnitId("");
		
		candidatePairInfo.hashCode();
		candidatePairInfo.equals(new AssignServiceOrderResponseReturnDataSetCandidatePairInfo());
		candidatePairInfo.toString();
		
		returnDataSet.setRequestStatus("");
		returnDataSet.setCircuitId("");

		returnDataSet.hashCode();
		returnDataSet.equals(new AssignServiceOrderResponseReturnDataSet());
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
		
		assignServiceOrderResponse.setCompletedTimeStamp("1");
		assignServiceOrderResponse.setCompletedTimeStampSpecified(true);
		assignServiceOrderResponse.setRequestId("1");
		assignServiceOrderResponse.setWebServiceName("");
		assignServiceOrderResponse.setArtisInformation(artisInformation);
		assignServiceOrderResponse.setMessageStatus(messageStatus);
		assignServiceOrderResponse.setReturnDataSet(returnDataSet);
		assignServiceOrderResponse.setTargetSchemaVersionUsed(targetSchemaVersionUsed);
		
		assignServiceOrderResponse.getArtisInformation();
		assignServiceOrderResponse.getCompletedTimeStamp();
		assignServiceOrderResponse.getMessageStatus();
		assignServiceOrderResponse.getRequestId();
		assignServiceOrderResponse.getReturnDataSet();
		assignServiceOrderResponse.getTargetSchemaVersionUsed();
		assignServiceOrderResponse.getWebServiceName();
		
		assertEquals("1", assignServiceOrderResponse.getCompletedTimeStamp());
	}
	
	

}
