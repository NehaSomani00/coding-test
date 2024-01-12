package com.lumen.fastivr.IVRUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.lumen.fastivr.IVRUtils.IVRConstants.SIGNON_EVENT;

@ExtendWith(MockitoExtension.class)
class IVRUtilityTest {
	
	@InjectMocks IVRUtility utility;

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testConvertStateToEvent() {
		String event = utility.convertStateToEvent("SSD110", "");
		utility.convertStateToEvent("FID", "");
		utility.convertStateToEvent("MLD", " ");
		utility.convertStateToEvent("FND", " ");
		utility.convertStateToEvent("FPD"," ");
		utility.convertStateToEvent("IDD", " ");
		utility.convertStateToEvent("FTD", " ");
		String responseNull = utility.convertStateToEvent("XXX", " ");
		
		assertNull(responseNull);
		assertEquals(SIGNON_EVENT, event);
	}

}
