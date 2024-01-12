package com.lumen.fastivr.IVRCNF.Dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ARTISRequestHeaderTest {

	@InjectMocks
	private ARTISRequestHeader artisRequestHeader;
	
	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testARTISRequestHeade() { 
		
		artisRequestHeader.setArtisCorrelationId("header123");
		artisRequestHeader.setHierarchyCalloutFlag(false);
		
		assertEquals("header123", artisRequestHeader.getArtisCorrelationId());
	}
}
