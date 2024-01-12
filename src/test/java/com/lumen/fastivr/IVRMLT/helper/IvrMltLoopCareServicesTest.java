package com.lumen.fastivr.IVRMLT.helper;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.net.http.HttpTimeoutException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumen.fastivr.IVRCacheManagement.IVRCacheService;
import com.lumen.fastivr.IVRDto.IVRWebHookResponseDto;
import com.lumen.fastivr.IVRMLT.caching.IvrMltCacheService;
import com.lumen.fastivr.IVRMLT.entity.IvrMltSession;
import com.lumen.fastivr.IVRMLT.exception.InvalidNPANXXException;
import com.lumen.fastivr.IVRMLT.utils.IvrMltPager;
import com.lumen.fastivr.IVRSessionManagement.IVRUserSession;

import tollgrade.loopcare.testrequestapi.DataChanObjRef;
import tollgrade.loopcare.testrequestapi.MDataChannelFactoryOperations;
import tollgrade.loopcare.testrequestapi.MDataChannelFactoryOperationsService;
import tollgrade.loopcare.testrequestapi.MLTTESTACK;
import tollgrade.loopcare.testrequestapi.Mdata;
import tollgrade.loopcare.testrequestapi.RequestToMLTOperations;

@ExtendWith(MockitoExtension.class)
class IvrMltLoopCareServicesTest {

	@InjectMocks
	private IvrMltLoopCareServices service;

	@Mock
	private IVRCacheService mockIvrCacheService;

	@Mock
	private IvrMltCacheService mockMltCacheService;

	@Mock
	private ObjectMapper mockObjectMapper;

	@Mock
	private IvrMltPager mockMltPager;

	@Mock
	private IvrMltSoapApiGenerator mltSoapApiGenerator;

	@Mock
	private IvrLoopCareOperations loopCareOperations;

	@Mock
	private IvrLoopCareAsyncService ivrLoopCareAsyncService;

	@Mock
	private MDataChannelFactoryOperationsService mockService;

	@Mock
	private MDataChannelFactoryOperations mockOps;

	@Mock
	private IvrMltHelper mltHelper;
	
	@Mock
	MltPagerText mltPagerText;

	@Mock
	private RequestToMLTOperations requestMltOps;

	private String sessionId = "testSessionId";
	private IVRUserSession ivrUserSession = new IVRUserSession();
	private IvrMltSession mltUserSession = new IvrMltSession();
	private DataChanObjRef dataChanObjRef = new DataChanObjRef();
	private MLTTESTACK mltTestAck = new MLTTESTACK();
	private Mdata mData = new Mdata();

	@BeforeEach
	void setUp() throws Exception {
		mltUserSession.setDataChannelProxyUrl("testproxyapi");
		mltUserSession.setTestRequestProxyUrl("testproxyapi");
		mltUserSession.setInquiredTn("4064682603");
		mltUserSession.setMltTestResult("{\"testRsp\":{\"f\":{\"testCode\":\"123\"}}}");

	}

	@Test
	void testPrepareRequest_WhenCanBePagedMobileIsTrueAndTechOnSameLineIsTrue_ShouldReturnHookReturnCode2()
			throws Exception {
		// Arrange

		ivrUserSession.setCanBePagedMobile(true);
		mltUserSession.setTechOnSameLine(true);
		mltUserSession.setTestType("QUICKX");
		commonStubbingForPrepareRequest();

		// Act
		String result = service.prepareRequest(sessionId);

		// Assert
		assertEquals("2", result);
	}

	private void commonStubbingForPrepareRequest() throws Exception {
		dataChanObjRef.setId("testId");
		mltTestAck.setStatus("0");
		
		when(mockIvrCacheService.getBySessionId(sessionId)).thenReturn(ivrUserSession);
		when(mockMltCacheService.getBySessionId(sessionId)).thenReturn(mltUserSession);
		when(ivrLoopCareAsyncService.fetchMltTestResult(sessionId)).thenReturn(null);

		when(loopCareOperations.dataChannelFactoryOperations(eq(mltUserSession.getDataChannelProxyUrl())))
				.thenReturn(mockOps);
		when(mockService.getMDataChannelFactoryAPIProxySoap()).thenReturn(mockOps);
		when(mockService.getMDataChannelFactoryAPIProxySoap().create()).thenReturn(dataChanObjRef);

		when(loopCareOperations.mltOperations(anyString())).thenReturn(requestMltOps);
		when(requestMltOps.sendTestRequest(any())).thenReturn(mltTestAck);
		
	}

	@Test
	void testPrepareRequest_WhenCanBePagedMobileIsTrueAndTechOnSameLineIsFalse_ShouldReturnHookReturnCode0()
			throws Exception {
		// Arrange
		ivrUserSession.setCanBePagedMobile(true);
		mltUserSession.setTechOnSameLine(false);
		mltUserSession.setTestType("QUICKX");
		commonStubbingForPrepareRequest();
		
		// Act
		String result = service.prepareRequest(sessionId);

		// Assert
		assertEquals("0", result);
	}

	@Test
	void testPrepareRequest_WhenCanBePagedMobileIsFalseAndTechOnSameLineIsTrue_ShouldReturnHookReturnCode1()
			throws Exception {
		// Arrange
		ivrUserSession.setCanBePagedMobile(false);
		mltUserSession.setTechOnSameLine(true);
		mltUserSession.setTestType("X");
		commonStubbingForPrepareRequest();

		// Act
		String result = service.prepareRequest(sessionId);

		// Assert
		assertEquals("1", result);
	}

	@Test
	void testPrepareRequest_WhenCanBePagedMobileIsFalseAndTechOnSameLineIsFalse_ShouldReturnHookReturnCode1()
			throws Exception {
		// Arrange
		ivrUserSession.setCanBePagedMobile(false);
		mltUserSession.setTechOnSameLine(false);
		mltUserSession.setTestType("TONE+");
		commonStubbingForPrepareRequest();
		// Act
		String result = service.prepareRequest(sessionId);

		// Assert
		assertEquals("1", result);
	}

	@Test
	void testPrepareRequest_WhenMltSoapApiGeneratorThrowsJsonException_ShouldReturnHookReturnCode4()
			throws JsonProcessingException, HttpTimeoutException {
		// Arrange
		ivrUserSession.setCanBePagedMobile(true);
		mltUserSession.setTechOnSameLine(true);

		when(mockIvrCacheService.getBySessionId(sessionId)).thenReturn(ivrUserSession);
		when(mockMltCacheService.getBySessionId(sessionId)).thenReturn(mltUserSession);
		doThrow(JsonProcessingException.class).when(mltSoapApiGenerator).generateLoopCareSoapApi(sessionId);

		// Act
		String result = service.prepareRequest(sessionId);

		// Assert
		assertEquals("4", result);
	}
	@Test
	void testPrepareRequest_WhenMltSoapApiGeneratorThrowsException_ShouldReturnHookReturnCode4()
			throws JsonProcessingException, HttpTimeoutException {
		// Arrange
		ivrUserSession.setCanBePagedMobile(true);
		mltUserSession.setTechOnSameLine(true);
		
		when(mockIvrCacheService.getBySessionId(sessionId)).thenReturn(ivrUserSession);
		when(mockMltCacheService.getBySessionId(sessionId)).thenReturn(mltUserSession);
		doThrow(JsonMappingException.class).when(mltSoapApiGenerator).generateLoopCareSoapApi(sessionId);
		
		// Act
		String result = service.prepareRequest(sessionId);
		
		// Assert
		assertEquals("4", result);
	}
	@Test
	void testPrepareRequest_WhenMltSoapApiGeneratorThrowsInvalidNPANXXException_ShouldReturnHookReturnCode5()
			throws JsonProcessingException, HttpTimeoutException {
		// Arrange
		ivrUserSession.setCanBePagedMobile(true);
		mltUserSession.setTechOnSameLine(true);
		
		when(mockIvrCacheService.getBySessionId(sessionId)).thenReturn(ivrUserSession);
		when(mockMltCacheService.getBySessionId(sessionId)).thenReturn(mltUserSession);
		doThrow(InvalidNPANXXException.class).when(mltSoapApiGenerator).generateLoopCareSoapApi(sessionId);
		
		// Act
		String result = service.prepareRequest(sessionId);
		
		// Assert
		assertEquals("5", result);
	}

	@Test
	void testIssueMltTestReq_ShouldReturn_Zero() throws Exception {

		ivrUserSession.setCanBePagedMobile(false);
		mltUserSession.setTechOnSameLine(true);
		dataChanObjRef.setId("testId");
		mltTestAck.setStatus("1");
		mltUserSession.setTestType("LOOPX");

		when(mockMltCacheService.getBySessionId(sessionId)).thenReturn(mltUserSession);

		when(loopCareOperations.dataChannelFactoryOperations(eq(mltUserSession.getDataChannelProxyUrl())))
				.thenReturn(mockOps);
		when(mockService.getMDataChannelFactoryAPIProxySoap()).thenReturn(mockOps);
		when(mockService.getMDataChannelFactoryAPIProxySoap().create()).thenReturn(dataChanObjRef);

		when(loopCareOperations.mltOperations(anyString())).thenReturn(requestMltOps);
		when(requestMltOps.sendTestRequest(any())).thenReturn(mltTestAck);
		

		// Act
		String result = service.issueMltTestReq(sessionId);

		// Assert
		assertEquals("0", result);
	}

	//validate mlt result
	@Test
	void testValidateMltResult_WhenTestCodeContainsT_ShouldReturnHookReturnCode4() throws Exception {

		
		  when(mockMltCacheService.getBySessionId(sessionId)).thenReturn(mltUserSession); 
		  mData = new ObjectMapper().readValue("{\"testRsp\":{\"f\":{\"testCode\":\"T123\"}}}",Mdata.class); 
		  when(mockObjectMapper.readValue(anyString(), eq(Mdata.class))).thenReturn(mData);
		 
		mltUserSession.setTestType("QUICKX");

		// Act
		IVRWebHookResponseDto result = service.validateMltResult(sessionId);

		// Assert
		assertEquals("4", result.getHookReturnCode());
	}

	@Test
	void testValidateMltResult_WhenTestTypeIsQuickX_ShouldReturnHookReturnCode8() throws Exception {
		// Arrange
		mltUserSession.setTestType("QUICKX");
		 when(mockMltCacheService.getBySessionId(sessionId)).thenReturn(mltUserSession); 
		  mData = new ObjectMapper().readValue("{\"testRsp\":{\"f\":{\"testCodePrefix\":\"\"}}}",Mdata.class); 
		  when(mockObjectMapper.readValue(anyString(), eq(Mdata.class))).thenReturn(mData);

		// Act
		IVRWebHookResponseDto result = service.validateMltResult(sessionId);

		// Assert
		assertEquals("8", result.getHookReturnCode());
	}

	@Test
	void testValidateMltResult_WhenTestTypeIsLoopXOrFullX_ShouldReturnHookReturnCode7() throws Exception {
		// Arrange
		mltUserSession.setTestType("LOOPX");
		commonStubbingForValidateResponse();

		// Act
		IVRWebHookResponseDto result = service.validateMltResult(sessionId);

		// Assert
		assertEquals("7", result.getHookReturnCode());
	}

	@Test
	void testValidateMltResult_WhenTestTypeIsNotQuickXOrLoopXOrFullX_ShouldReturnHookReturnCode7() throws Exception {
		// Arrange
		mltUserSession.setTestType("FULLX");
		commonStubbingForValidateResponse();

		// Act
		IVRWebHookResponseDto result = service.validateMltResult(sessionId);

		// Assert
		assertEquals("7", result.getHookReturnCode());
	}
	
	@Test
	void testValidateMltResult_WhenTestTypeIsNotQuickXOrLoopXOrTONEPlus_ShouldReturnHookReturnCode8() throws Exception {
		// Arrange
		mltUserSession.setTestType("TONE+");
		commonStubbingForValidateResponse();
		
		// Act
		IVRWebHookResponseDto result = service.validateMltResult(sessionId);
		
		// Assert
		assertEquals("8", result.getHookReturnCode());
	}

	private void commonStubbingForValidateResponse() throws JsonMappingException, JsonProcessingException
    {
    	when(mockMltCacheService.getBySessionId(sessionId)).thenReturn(mltUserSession);
    	mData = new ObjectMapper().readValue("{\"testRsp\":{\"f\":{\"testCode\":\"123\"},\"finalFlag\":\"89\"}}",Mdata.class);
        when(mockObjectMapper.readValue(anyString(), eq(Mdata.class))).thenReturn(mData);
    }

	// retrieve Mlt test cases
	@Test
	void testRetrieveMLTTestResults_WhenTestResultIsNotNull_ShouldReturnTestResult() {
		// Arrange
		String sessionId = "testSessionId";
		String expectedTestResult = "Test Result";
		IvrMltSession mltSession = new IvrMltSession();
		mltSession.setMltTestResult(expectedTestResult);
		when(mockMltCacheService.getBySessionId(sessionId)).thenReturn(mltSession);

		// Act
		String actualTestResult = service.retrieveMLTTestResults(sessionId);

		// Assert
		assertEquals(expectedTestResult, actualTestResult);
	}

	@Test
	void testRetrieveMLTTestResults_WhenTestResultIsNull_ShouldWaitAndReturnTestResult() throws InterruptedException {
		// Arrange
		String sessionId = "testSessionId";
		String expectedTestResult = "Test Result";
		IvrMltSession mltSession = new IvrMltSession();
		mltSession.setMltTestResult("Test Result");
		when(mockMltCacheService.getBySessionId(sessionId)).thenReturn(mltSession);

		// Mocking the Thread.sleep() method to avoid actual waiting during the test

		// Act
		String actualTestResult = service.retrieveMLTTestResults(sessionId);

		// Assert
		assertEquals(expectedTestResult, actualTestResult);
	}

	@Test
	void testRetrieveMLTTestResults_WhenMaxTimesReached_ShouldReturnNull() throws InterruptedException {
		// Arrange
		String sessionId = "testSessionId";
		IvrMltSession mltSession = new IvrMltSession();
		mltSession.setMltTestResult(null);
		when(mockMltCacheService.getBySessionId(sessionId)).thenReturn(mltSession);

		// Mocking the Thread.sleep() method to avoid actual waiting during the test
		// Act
		String actualTestResult = service.retrieveMLTTestResults(sessionId);

		// Assert
		assertNull(actualTestResult);
	}

}