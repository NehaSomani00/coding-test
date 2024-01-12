package com.lumen.fastivr.IVRCNF.utils;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lumen.fastivr.IVRCNF.utils.IVRCNFConstants;

@ExtendWith(MockitoExtension.class)
public class IVRCNFConstantsTest {
	
	@InjectMocks
	private IVRCNFConstants ivrcnfConstants;
	
	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testIVRCNFConstants() {
		
		assertEquals(IVRCNFConstants.INQUIRY_BY_SERVICE_ORDER,ivrcnfConstants.INQUIRY_BY_SERVICE_ORDER);
		
	}
	
	

}
