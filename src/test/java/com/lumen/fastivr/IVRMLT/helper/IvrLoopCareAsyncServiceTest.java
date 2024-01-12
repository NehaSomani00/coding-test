package com.lumen.fastivr.IVRMLT.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumen.fastivr.IVRCacheManagement.IVRCacheService;
import com.lumen.fastivr.IVRMLT.caching.IvrMltCacheService;
import com.lumen.fastivr.IVRMLT.entity.IvrMltSession;
import com.lumen.fastivr.IVRMLT.utils.IvrMltPager;
import com.lumen.fastivr.IVRSessionManagement.IVRUserSession;
import com.lumen.fastivr.httpclient.IVRHttpClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tollgrade.loopcare.testrequestapi.APIServerException;
import tollgrade.loopcare.testrequestapi.MDataChannelFactoryOperations;
import tollgrade.loopcare.testrequestapi.Mdata;

import java.net.MalformedURLException;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IvrLoopCareAsyncServiceTest {

	@InjectMocks
	private IvrLoopCareAsyncService ivrLoopCareAsyncService;

	@Mock
	private IvrMltCacheService mltCacheService;

	@Mock
	private IVRCacheService ivrCacheService;

	@Mock
	private MltPagerText mltPagerText;
	
	@Mock
	private IvrMltPager mltPager;
	
	@Mock
	private IvrLoopCareOperations loopCareOperations;
	
	@Mock
	private MDataChannelFactoryOperations mockOps;
	
	@Mock
	private ObjectMapper objectMapper;

	@Mock
	private IVRHttpClient ivrHttpClient;

	@Test
	void testFetchMltTestResultWithTonePlusAsTestType() throws Exception {
		// Arrange
		String sessionId = "session123";
		IvrMltSession mltSession = new IvrMltSession();
		mltSession.setDataChannelProxyUrl("testUrl");
		
		mltSession.setTechOnSameLine(true);
		mltSession.setTestType("TONE+");
		IVRUserSession ivrSession = new IVRUserSession();
		ivrSession.setCanBePagedMobile(true);
		String status = "1";

		Mdata mData = new ObjectMapper().readValue("{\"testRsp\":{\"f\":{\"testCode\":\"123\"},\"finalFlag\":\"89\"}}",Mdata.class);
		when(mltCacheService.getBySessionId(sessionId)).thenReturn(mltSession);
		when(ivrCacheService.getBySessionId(sessionId)).thenReturn(ivrSession);
		when(mltPagerText.getPagerText(anyString(),anyString())).thenReturn("Pager Text");
		when(mltPager.sendPage(anyString(), anyString(), any())).thenReturn(true);

		when(loopCareOperations.dataChannelFactoryOperations(eq(mltSession.getDataChannelProxyUrl())))
		.thenReturn(mockOps);
		when(mockOps.pull(any())).thenReturn(mData);
		 when(objectMapper.writeValueAsString(any())).thenReturn("stringLmosRegionRequestDto");
		// Act
		CompletableFuture<String> result = ivrLoopCareAsyncService.fetchMltTestResult(sessionId);

		// Assert
		assertEquals(status, result.get());
		verify(mltCacheService).getBySessionId(sessionId);
		verify(ivrCacheService).getBySessionId(sessionId);
		verify(mltPagerText).getPagerText(anyString(),anyString());
	}
	@Test
	void testFetchMltTestResultWithToneRemovalAsTestType() throws Exception {
		// Arrange
		String sessionId = "session123";
		IvrMltSession mltSession = new IvrMltSession();
		mltSession.setDataChannelProxyUrl("testUrl");
		
		mltSession.setTechOnSameLine(true);
		mltSession.setTestType("TONE+");
		IVRUserSession ivrSession = new IVRUserSession();
		ivrSession.setCanBePagedMobile(true);
		String status = "1";
		
		Mdata mData = new ObjectMapper().readValue("{\"testRsp\":{\"f\":{\"testCode\":\"123\"},\"finalFlag\":\"78\"}}",Mdata.class);
		when(mltCacheService.getBySessionId(sessionId)).thenReturn(mltSession);
		when(ivrCacheService.getBySessionId(sessionId)).thenReturn(ivrSession);
		when(mltPagerText.getPagerText(anyString(),anyString())).thenReturn("Pager Text");
		when(mltPager.sendPage(anyString(), anyString(), any())).thenReturn(true);

		
		when(loopCareOperations.dataChannelFactoryOperations(eq(mltSession.getDataChannelProxyUrl())))
		.thenReturn(mockOps);
		when(mockOps.pull(any())).thenReturn(mData);
		when(objectMapper.writeValueAsString(any())).thenReturn("stringLmosRegionRequestDto");
		// Act
		CompletableFuture<String> result = ivrLoopCareAsyncService.fetchMltTestResult(sessionId);
		
		// Assert
		assertEquals(status, result.get());
		verify(mltCacheService).getBySessionId(sessionId);
		verify(ivrCacheService).getBySessionId(sessionId);
		verify(mltPagerText).getPagerText(anyString(),anyString());
	}
	@Test
	void testFetchMltTestResultWithQuickTest() throws Exception {
		// Arrange
		String sessionId = "session123";
		IvrMltSession mltSession = new IvrMltSession();
		mltSession.setDataChannelProxyUrl("testUrl");
		
		mltSession.setTechOnSameLine(true);
		mltSession.setTestType("QUICKX");
		IVRUserSession ivrSession = new IVRUserSession();
		ivrSession.setCanBePagedMobile(true);
		String status = "1";
		
		Mdata mData = new Mdata();
		
		when(mltCacheService.getBySessionId(sessionId)).thenReturn(mltSession);
		when(ivrCacheService.getBySessionId(sessionId)).thenReturn(ivrSession);
		when(mltPagerText.getPagerText(anyString(),anyString())).thenReturn("Pager Text");
		when(mltPager.sendPage(anyString(), anyString(), any())).thenReturn(true);

		
		when(loopCareOperations.dataChannelFactoryOperations(eq(mltSession.getDataChannelProxyUrl())))
		.thenReturn(mockOps);
		when(mockOps.pull(any())).thenReturn(mData);
		when(objectMapper.writeValueAsString(any())).thenReturn("stringLmosRegionRequestDto");
		// Act
		CompletableFuture<String> result = ivrLoopCareAsyncService.fetchMltTestResult(sessionId);
		
		// Assert
		assertEquals(status, result.get());
		verify(mltCacheService).getBySessionId(sessionId);
		verify(ivrCacheService).getBySessionId(sessionId);
		verify(mltPagerText).getPagerText(anyString(),anyString());
	}
	
	@Test
	void testFetchMltTestResult_WithPullMltResponseException() throws Exception {
		// Arrange
		String sessionId = "session123";
		IvrMltSession mltSession = new IvrMltSession();
		mltSession.setDataChannelProxyUrl("testUrl");
		
		mltSession.setTechOnSameLine(true);
		mltSession.setTestType("QUICKX");
		IVRUserSession ivrSession = new IVRUserSession();
		ivrSession.setCanBePagedMobile(true);
		String status = "0";

		when(mltCacheService.getBySessionId(sessionId)).thenReturn(mltSession);
		when(ivrCacheService.getBySessionId(sessionId)).thenReturn(ivrSession);
		when(mltPagerText.getPagerText(anyString(),anyString())).thenReturn("Pager Text");
		when(mltPager.sendPage(anyString(), anyString(), any())).thenReturn(true);

		
		when(loopCareOperations.dataChannelFactoryOperations(eq(mltSession.getDataChannelProxyUrl())))
		.thenReturn(mockOps);
		doThrow(APIServerException.class).when(mockOps).pull(any());
		// Act
		CompletableFuture<String> result = ivrLoopCareAsyncService.fetchMltTestResult(sessionId);

		// Assert
		assertEquals(status, result.get());
		verify(mltCacheService).getBySessionId(sessionId);
		verify(ivrCacheService).getBySessionId(sessionId);
		verify(mltPagerText).getPagerText(anyString(),anyString());
	}
	
	@Test
	void testFetchMltTestResult_WithMalformedURLException() throws Exception {
		// Arrange
		String sessionId = "session123";
		IvrMltSession mltSession = new IvrMltSession();
		mltSession.setDataChannelProxyUrl("testUrl");
		
		mltSession.setTechOnSameLine(true);
		mltSession.setTestType("QUICKX");
		IVRUserSession ivrSession = new IVRUserSession();
		ivrSession.setCanBePagedMobile(true);
		String status = "0";
		
		when(mltCacheService.getBySessionId(sessionId)).thenReturn(mltSession);
		when(ivrCacheService.getBySessionId(sessionId)).thenReturn(ivrSession);
		when(mltPagerText.getPagerText(anyString(),anyString())).thenReturn("Pager Text");
		when(mltPager.sendPage(anyString(), anyString(), any())).thenReturn(true);

		doThrow(MalformedURLException.class).when(loopCareOperations).dataChannelFactoryOperations(eq(mltSession.getDataChannelProxyUrl()));
		// Act
		CompletableFuture<String> result = ivrLoopCareAsyncService.fetchMltTestResult(sessionId);
		
		// Assert
		assertEquals(status, result.get());
		verify(mltCacheService).getBySessionId(sessionId);
		verify(ivrCacheService).getBySessionId(sessionId);
		verify(mltPagerText).getPagerText(anyString(),anyString());
	}



}