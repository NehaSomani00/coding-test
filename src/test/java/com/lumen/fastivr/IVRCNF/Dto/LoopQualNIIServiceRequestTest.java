package com.lumen.fastivr.IVRCNF.Dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LoopQualNIIServiceRequestTest {
	
	@InjectMocks
	private LoopQualNIIServiceRequest loopQualNIIServiceRequest;
	
	@InjectMocks
	private ARTISRequestHeader artisRequestHeader;
	
	@InjectMocks
	private TN tn;
	
	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testLoopQualNIIServiceRequest() { 
		
		artisRequestHeader.setArtisCorrelationId("123");
		artisRequestHeader.setHierarchyCalloutFlag(false);
		artisRequestHeader.hashCode();
		artisRequestHeader.equals(new ARTISRequestHeader());
		artisRequestHeader.toString();	
		
		tn.setLineNumber("567");
		tn.setNpa("abc");
		tn.setNxx("nxx");
		tn.hashCode();
		tn.equals(new TN());
		tn.toString();	
	
		loopQualNIIServiceRequest.setMessageSrcSystem("msg");
		loopQualNIIServiceRequest.setArtisRequestHeader(artisRequestHeader);
		loopQualNIIServiceRequest.setTn(tn);
		
		assertEquals("567", loopQualNIIServiceRequest.getTn().getLineNumber());
		assertEquals("123", loopQualNIIServiceRequest.getArtisRequestHeader().getArtisCorrelationId());
		assertEquals("msg", loopQualNIIServiceRequest.getMessageSrcSystem());
		
	}

}
