package com.lumen.fastivr.IVRCANST.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class IVRCanstEntityTest {
	
	@InjectMocks 
	IVRCanstEntity ivrCanstEntity;
	
	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testivrCanstEntity() {
		
		ivrCanstEntity.setCanstInqType("");
		ivrCanstEntity.setLastActiveSessionTime(LocalDateTime.now());
		ivrCanstEntity.setSegmentRead("1");
		ivrCanstEntity.setSessionId("");
		
		ivrCanstEntity.setCable("CA");
		ivrCanstEntity.setPair("");
		ivrCanstEntity.setServiceOrderNo("");
		
		ivrCanstEntity.setOrderStatusResp("");
		ivrCanstEntity.setAssignOrderServiceResp("");
		ivrCanstEntity.setNewTea("");
		ivrCanstEntity.setOldTea("");
		ivrCanstEntity.setChangeServTermResp("");
		
		
		ivrCanstEntity.getCanstInqType();
		ivrCanstEntity.getLastActiveSessionTime();
		ivrCanstEntity.getSegmentRead();
		ivrCanstEntity.getSessionId();
		
		ivrCanstEntity.getCable();
		ivrCanstEntity.getPair();
		ivrCanstEntity.getServiceOrderNo();
		
		ivrCanstEntity.getAssignOrderServiceResp();
		ivrCanstEntity.getAssignOrderServiceResp();
		ivrCanstEntity.getOldTea();
		ivrCanstEntity.getNewTea();
		ivrCanstEntity.getChangeServTermResp();
		ivrCanstEntity.getOrderStatusResp();
		
		
		assertEquals("1", ivrCanstEntity.getSegmentRead());
	}
}
