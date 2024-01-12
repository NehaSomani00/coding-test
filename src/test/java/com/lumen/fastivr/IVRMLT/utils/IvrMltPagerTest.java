package com.lumen.fastivr.IVRMLT.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumen.fastivr.IVRDto.NETMessagingRequestDto;
import com.lumen.fastivr.IVRLFACS.IVRLfacsServiceHelper;
import com.lumen.fastivr.IVRMLT.caching.IvrMltCacheService;
import com.lumen.fastivr.IVRMLT.entity.IvrMltSession;
import com.lumen.fastivr.IVRSessionManagement.IVRUserSession;
import com.lumen.fastivr.httpclient.IVRHttpClient;

@ExtendWith(MockitoExtension.class)
class IvrMltPagerTest {

	@InjectMocks
	IvrMltPager ivrMltPager;
	
	@Mock
	private ObjectMapper mockObjectMapper;
	
	@Mock
	private IVRLfacsServiceHelper ivrLfacsServiceHelper;

	@Mock 
	IVRHttpClient mockIvrHttpClient;
	
	@Mock
	private IvrMltCacheService mockMltCacheService;
	
	@Test
	void sendPageTest() throws JsonProcessingException {
		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId("session123");
		
		IvrMltSession mockMltSession = new IvrMltSession();
		mockMltSession.setSessionId("session123");
		
		when(ivrLfacsServiceHelper.sendTestResultToTech(IVRMltConstants.NET_MAIL_SUBJECT_MLT,"","",mockSession)).thenReturn(true);
		assertTrue(ivrMltPager.sendPage("", "", mockSession));
		
	}
	
	@Test
	void sendPageInvalidTest() throws JsonProcessingException {
		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId("");
		when(ivrLfacsServiceHelper.sendTestResultToTech(IVRMltConstants.NET_MAIL_SUBJECT_MLT,"","",mockSession)).thenReturn(false);
		assertFalse(ivrMltPager.sendPage("", "", mockSession));
	
	}
	
	@Test
	void getMltPageTextSubjectTest() throws JsonProcessingException {
		
		String sessionId="sessionId";
		
		IvrMltSession mockMltSession = new IvrMltSession();
		mockMltSession.setSessionId("session123");
		mockMltSession.setTestType(IVRMltUtilities.QUICKX_TEST);
		
		when(mockMltCacheService.getBySessionId(sessionId)).thenReturn(mockMltSession);
		String response=ivrMltPager.getMltPageTextSubject(sessionId);
		assertEquals(IVRMltConstants.NET_MAIL_SUBJECT_MLT_QUICK_TEST,response);
		
		mockMltSession.setTestType(IVRMltUtilities.LOOPX_TEST);
		String response1=ivrMltPager.getMltPageTextSubject(sessionId);
		assertEquals(IVRMltConstants.NET_MAIL_SUBJECT_MLT_LOOP_TEST,response1);
		
		mockMltSession.setTestType(IVRMltUtilities.FULLX_TEST);
		String response2=ivrMltPager.getMltPageTextSubject(sessionId);
		assertEquals(IVRMltConstants.NET_MAIL_SUBJECT_MLT_FULL_TEST,response2);
		
		mockMltSession.setTestType(IVRMltUtilities.TONE_PLUS_TEST);
		String response3=ivrMltPager.getMltPageTextSubject(sessionId);
		assertEquals(IVRMltConstants.NET_MAIL_SUBJECT_MLT_TONE_PLUS_TEST,response3);
		
		mockMltSession.setTestType(IVRMltUtilities.TONE_REMOVAL_TEST);
		String response4=ivrMltPager.getMltPageTextSubject(sessionId);
		assertEquals(IVRMltConstants.NET_MAIL_SUBJECT_MLT_TONE_REMOVAL_TEST,response4);
		
	}

}
