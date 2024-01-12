package com.lumen.fastivr.IVRCANST.utils;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class IVRCANSTConstantsTest {
	
	@InjectMocks
	private IVRCANSTConstants ivrcanstConstants;
	
	@BeforeEach
	void setUp() throws Exception {
	}
	
	@Test
	void testIVRCANSTConstants() {
		
		assertEquals(IVRCANSTConstants.CANST_INVALID_SESSION_ID, ivrcanstConstants.CANST_INVALID_SESSION_ID);
		assertEquals(IVRCANSTConstants.STATE_FT0200, ivrcanstConstants.STATE_FT0200);
		assertEquals(IVRCANSTConstants.STATE_FTD210, ivrcanstConstants.STATE_FTD210);
		
	}	

}
