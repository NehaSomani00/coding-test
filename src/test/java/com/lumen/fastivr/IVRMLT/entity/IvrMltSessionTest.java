package com.lumen.fastivr.IVRMLT.entity;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class IvrMltSessionTest {
	
	@InjectMocks 
	IvrMltSession session;
	
	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testIVRUserSession() {
		session.setSessionId("sesion123");
		session.setTechOnSameLine(true);
		session.setOverride("");
		session.setNpaPrefix("");
		session.setMltTestResult("");
		session.setLastActiveSessionTime(LocalDateTime.now());
		session.setInquiredTn("");
		session.setDatachannelId("");
		
		session.getSessionId();
		session.getTechOnSameLine();
		session.getOverride();
		session.getNpaPrefix();
		session.getMltTestResult();
		session.getLastActiveSessionTime();
		session.getInquiredTn();
		session.getDatachannelId();
	
	}


}
