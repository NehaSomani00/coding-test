package com.lumen.fastivr.IVRDto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IVRPagerConfigTest {
	
	@InjectMocks IVRPagerConfig pagerConfig;

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testIVRPagerConfig() {
		pagerConfig.setPhoneEnabled(true);
		pagerConfig.setMailEnabled(true);
		
		boolean mailEnabled = pagerConfig.isMailEnabled();
		pagerConfig.isPhoneEnabled();
		
		pagerConfig.toString();
		
		assertTrue(mailEnabled);
		
	}

}
