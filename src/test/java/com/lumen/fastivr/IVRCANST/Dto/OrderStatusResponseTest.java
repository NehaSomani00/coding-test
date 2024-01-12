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
import com.lumen.fastivr.IVRDto.WireCtrPrimaryNPANXX;

@ExtendWith(MockitoExtension.class)
public class OrderStatusResponseTest {

	@InjectMocks
	OrderStatusResponse orderStatusResponse;
	
	@InjectMocks
	private OrderStatusReturnDataSet returnDataSet;	
	
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
	void testOrderStatusResponse() { 
		
		wireCtrPrimaryNPANXX.setNpa("");
		wireCtrPrimaryNPANXX.setNxx("");
		
		wireCtrPrimaryNPANXX.hashCode();
		wireCtrPrimaryNPANXX.equals(new WireCtrPrimaryNPANXX());
		wireCtrPrimaryNPANXX.toString();
		
		returnDataSet.setAssignmentSectionPendingFlag("");
		returnDataSet.setLfacsMode("");
		returnDataSet.setLoopAssignmentStatus("");
		returnDataSet.setWireCtrPrimaryNPANXX(wireCtrPrimaryNPANXX);
		
		returnDataSet.hashCode();
		returnDataSet.equals(new OrderStatusReturnDataSet());
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
		
		orderStatusResponse.hashCode();
		orderStatusResponse.equals(new OrderStatusResponse());
		orderStatusResponse.toString();
		
		orderStatusResponse.setCompletedTimeStamp("1");
		orderStatusResponse.setCompletedTimeStampSpecified(true);
		orderStatusResponse.setRequestId("1");
		orderStatusResponse.setWebServiceName("");
		orderStatusResponse.setArtisInformation(artisInformation);
		orderStatusResponse.setMessageStatus(messageStatus);
		orderStatusResponse.setReturnDataSet(returnDataSet);
		orderStatusResponse.setTargetSchemaVersionUsed(targetSchemaVersionUsed);
		
		orderStatusResponse.getArtisInformation();
		orderStatusResponse.getCompletedTimeStamp();
		orderStatusResponse.getMessageStatus();
		orderStatusResponse.getRequestId();
		orderStatusResponse.getReturnDataSet();
		orderStatusResponse.getTargetSchemaVersionUsed();
		orderStatusResponse.getWebServiceName();
	
		assertEquals("1", orderStatusResponse.getRequestId());	
		assertEquals("", orderStatusResponse.getReturnDataSet().getLfacsMode());
		assertEquals("", orderStatusResponse.getReturnDataSet().getLoopAssignmentStatus());
		assertEquals(wireCtrPrimaryNPANXX, orderStatusResponse.getReturnDataSet().getWireCtrPrimaryNPANXX());
	}
	
}
