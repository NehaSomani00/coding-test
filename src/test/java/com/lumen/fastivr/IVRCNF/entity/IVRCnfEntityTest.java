package com.lumen.fastivr.IVRCNF.entity;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class IVRCnfEntityTest {

	@InjectMocks 
	IVRCnfEntity ivrCnfEntity;
	
	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testIVRCnfEntity() {
		
		ivrCnfEntity.setCnfInqType("");
		ivrCnfEntity.setGetInstReplPrsResponse("");
		ivrCnfEntity.setGetMntReplPrsResponse("");
		ivrCnfEntity.setLastActiveSessionTime(LocalDateTime.now());
		ivrCnfEntity.setSegmentRead("");
		ivrCnfEntity.setSessionId("");
		
		ivrCnfEntity.getCnfInqType();
		ivrCnfEntity.getGetInstReplPrsResponse();
		ivrCnfEntity.getGetMntReplPrsResponse();
		ivrCnfEntity.getLastActiveSessionTime();
		ivrCnfEntity.getSegmentRead();
		ivrCnfEntity.getSessionId();
	}
}
