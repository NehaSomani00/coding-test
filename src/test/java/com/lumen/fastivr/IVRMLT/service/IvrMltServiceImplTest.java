package com.lumen.fastivr.IVRMLT.service;

import static com.lumen.fastivr.IVRUtils.IVRConstants.*;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumen.fastivr.IVRCacheManagement.IVRCacheService;
import com.lumen.fastivr.IVRDto.CurrentAssignmentResponseDto;
import com.lumen.fastivr.IVRDto.IVRWebHookResponseDto;
import com.lumen.fastivr.IVRLFACS.LfacsValidation;
import com.lumen.fastivr.IVRMLT.caching.IvrMltCacheService;
import com.lumen.fastivr.IVRMLT.entity.IvrMltSession;
import com.lumen.fastivr.IVRMLT.helper.IvrMltHelper;
import com.lumen.fastivr.IVRMLT.helper.IvrMltLoopCareServices;
import com.lumen.fastivr.IVRMLT.utils.IVRMltUtilities;
import com.lumen.fastivr.IVRSessionManagement.IVRUserSession;
import com.lumen.fastivr.IVRUtils.IVRConstants;
import com.lumen.fastivr.IVRUtils.IVRHookReturnCodes;

import tollgrade.loopcare.testrequestapi.ArrayOfDETAILEDRESULTSITEM;
import tollgrade.loopcare.testrequestapi.DCSIGCRAFT;
import tollgrade.loopcare.testrequestapi.DETAILEDRESULTSITEM;
import tollgrade.loopcare.testrequestapi.MLTTESTRSP;
import tollgrade.loopcare.testrequestapi.POTSRESULTS1;
import tollgrade.loopcare.testrequestapi.MLTTESTRSP;
import tollgrade.loopcare.testrequestapi.Mdata;

@ExtendWith(MockitoExtension.class)
public class IvrMltServiceImplTest {
	

	@InjectMocks
	private IvrMltServiceImpl ivrMltServiceImpl;
	
	@Mock
	private IvrMltCacheService mockMltCacheService;

	@Mock
	private IvrMltHelper mockIvrMltHelper;

	@Mock
	private ObjectMapper mockObjectMapper;
	
	@Mock
	private LfacsValidation mockTnValidation;
	
	@Mock
	private IVRCacheService mockCacheService;
	
	@Mock
	private IvrMltLoopCareServices mockLoopCareServices;
	
	@Test
	void testIssueMLTTest() throws JsonMappingException, JsonProcessingException {
		String sessionid = "session123";
		IvrMltSession mockSession = new IvrMltSession();
		mockSession.setSessionId(sessionid);
		
		when(mockLoopCareServices.prepareRequest(anyString())).thenReturn(HOOK_RETURN_0);
		String returnCode = ivrMltServiceImpl.issueMLTTest(sessionid).getHookReturnCode();
		
		assertEquals(HOOK_RETURN_0, returnCode);
		
		when(mockLoopCareServices.prepareRequest(anyString())).thenReturn(HOOK_RETURN_1);
		returnCode = ivrMltServiceImpl.issueMLTTest(sessionid).getHookReturnCode();
		
		assertEquals(HOOK_RETURN_1, returnCode);
		
		when(mockLoopCareServices.prepareRequest(anyString())).thenReturn(HOOK_RETURN_2);
		returnCode = ivrMltServiceImpl.issueMLTTest(sessionid).getHookReturnCode();
		
		assertEquals(HOOK_RETURN_2, returnCode);
		
		when(mockLoopCareServices.prepareRequest(anyString())).thenReturn(HOOK_RETURN_3);
		returnCode = ivrMltServiceImpl.issueMLTTest(sessionid).getHookReturnCode();
		
		assertEquals(HOOK_RETURN_3, returnCode);
		
		when(mockLoopCareServices.prepareRequest(anyString())).thenReturn(HOOK_RETURN_4);
		returnCode = ivrMltServiceImpl.issueMLTTest(sessionid).getHookReturnCode();
		
		assertEquals(HOOK_RETURN_4, returnCode);
		
		when(mockLoopCareServices.prepareRequest(anyString())).thenReturn(HOOK_RETURN_5);
		returnCode = ivrMltServiceImpl.issueMLTTest(sessionid).getHookReturnCode();
		
		assertEquals(HOOK_RETURN_5, returnCode);
		
		when(mockLoopCareServices.prepareRequest(anyString())).thenReturn(HOOK_RETURN_6);
		returnCode = ivrMltServiceImpl.issueMLTTest(sessionid).getHookReturnCode();
		
		assertEquals(GPDOWN_ERR_MSG_CODE, returnCode);
	}
	
	@Test
	void testIssueTonePlusRequest() throws JsonMappingException, JsonProcessingException {
		String sessionid = "session123";
		IvrMltSession mockMltSession = new IvrMltSession();
		mockMltSession.setSessionId(sessionid);
		String userInput = "1";
//		mockSession.setTechOnSameLine(false);
		
		when(mockMltCacheService.getBySessionId(sessionid)).thenReturn(mockMltSession);
		
		when(mockLoopCareServices.prepareRequest(anyString())).thenReturn(HOOK_RETURN_0);
		String returnCode = ivrMltServiceImpl.issueTonePlusRequest(sessionid, userInput).getHookReturnCode();
		
		assertEquals(HOOK_RETURN_0, returnCode);
		
		when(mockLoopCareServices.prepareRequest(anyString())).thenReturn(HOOK_RETURN_1);
		returnCode = ivrMltServiceImpl.issueTonePlusRequest(sessionid, userInput).getHookReturnCode();
		
		assertEquals(HOOK_RETURN_1, returnCode);
		
		when(mockLoopCareServices.prepareRequest(anyString())).thenReturn(HOOK_RETURN_2);
		returnCode = ivrMltServiceImpl.issueTonePlusRequest(sessionid, userInput).getHookReturnCode();
		
		assertEquals(HOOK_RETURN_2, returnCode);
		
		when(mockLoopCareServices.prepareRequest(anyString())).thenReturn(HOOK_RETURN_3);
		returnCode = ivrMltServiceImpl.issueTonePlusRequest(sessionid, userInput).getHookReturnCode();
		
		assertEquals(HOOK_RETURN_3, returnCode);
		
		when(mockLoopCareServices.prepareRequest(anyString())).thenReturn(HOOK_RETURN_4);
		returnCode = ivrMltServiceImpl.issueTonePlusRequest(sessionid, userInput).getHookReturnCode();
		
		assertEquals(HOOK_RETURN_4, returnCode);
		
		when(mockLoopCareServices.prepareRequest(anyString())).thenReturn(HOOK_RETURN_5);
		returnCode = ivrMltServiceImpl.issueTonePlusRequest(sessionid, userInput).getHookReturnCode();
		
		assertEquals(HOOK_RETURN_5, returnCode);
		
		when(mockLoopCareServices.prepareRequest(anyString())).thenReturn(HOOK_RETURN_6);
		returnCode = ivrMltServiceImpl.issueTonePlusRequest(sessionid, userInput).getHookReturnCode();
		
		assertEquals(GPDOWN_ERR_MSG_CODE, returnCode);
		
	}
	
	@Test
	void processMLD021ValidTest() {
		String sessionid = "session123";
		String currentState = "MLD021";
		List<String>dtmfInput=new ArrayList<>();
		dtmfInput.add("1");
		dtmfInput.add("1234567");
		
		IVRWebHookResponseDto mockResponse = new IVRWebHookResponseDto();
		mockResponse.setHookReturnMessage(VALID_TN_MSG);
		mockResponse.setHookReturnCode(IVRHookReturnCodes.HOOK_RETURN_0);
		
		IvrMltSession mockSession = new IvrMltSession();
		mockSession.setSessionId(sessionid);
		mockSession.setOverride("C");
		mockSession.setTestType("48");
		
		IVRUserSession session = new IVRUserSession();
		session.setSessionId(sessionid);
		session.setFacsInqType(IVRConstants.INQUIRY_BY_TN);
		session.setCurrentAssignmentResponse("xyz");
		session.setLosDbResponse("mock-losdb-response-string");
		
		when(mockCacheService.getBySessionId(sessionid)).thenReturn(session);
		when(mockMltCacheService.getBySessionId(sessionid)).thenReturn(mockSession);
		when(mockIvrMltHelper.findTestCategory("1")).thenReturn(IVRMltUtilities.QUICKX_TEST);
		when(mockTnValidation.validateFacsTN("1234567",session)).thenReturn(mockResponse);
	
		IVRWebHookResponseDto response = ivrMltServiceImpl.processMLD021(sessionid, currentState, dtmfInput);
		assertEquals(IVRHookReturnCodes.HOOK_RETURN_0, response.getHookReturnCode());
		
	}

	@Test
	void processMLD021ValidTest1() {
		String sessionid = "session123";
		String currentState = "MLD021";
		List<String>dtmfInput=new ArrayList<>();
		dtmfInput.add("2");
		dtmfInput.add("1");
		dtmfInput.add("1234567");
		dtmfInput.add("1");
		
		IVRWebHookResponseDto mockResponse = new IVRWebHookResponseDto();
		mockResponse.setHookReturnMessage(VALID_TN_MSG);
		mockResponse.setHookReturnCode(IVRHookReturnCodes.HOOK_RETURN_0);
		
		IvrMltSession mockSession = new IvrMltSession();
		mockSession.setSessionId(sessionid);
		mockSession.setOverride("C");
		mockSession.setTestType("48");
		
		IVRUserSession session = new IVRUserSession();
		session.setSessionId(sessionid);
		session.setFacsInqType(IVRConstants.INQUIRY_BY_TN);
		session.setCurrentAssignmentResponse("xyz");
		session.setLosDbResponse("mock-losdb-response-string");
		
		when(mockCacheService.getBySessionId(sessionid)).thenReturn(session);
		when(mockMltCacheService.getBySessionId(sessionid)).thenReturn(mockSession);
		when(mockTnValidation.validateFacsTN("1234567",session)).thenReturn(mockResponse);
	
		IVRWebHookResponseDto response = ivrMltServiceImpl.processMLD021(sessionid, currentState, dtmfInput);
		assertEquals(IVRHookReturnCodes.HOOK_RETURN_0, response.getHookReturnCode());
		
	}
	
	@Test
	void processMLD021ValidTest2() {
		String sessionid = "session123";
		String currentState = "MLD021";
		List<String>dtmfInput=new ArrayList<>();
		dtmfInput.add("2");
		dtmfInput.add("2");
		dtmfInput.add("1234567");
		dtmfInput.add("1");
		
		IVRWebHookResponseDto mockResponse = new IVRWebHookResponseDto();
		mockResponse.setHookReturnMessage(VALID_TN_MSG);
		mockResponse.setHookReturnCode(IVRHookReturnCodes.HOOK_RETURN_0);
		
		IvrMltSession mockSession = new IvrMltSession();
		mockSession.setSessionId(sessionid);
		mockSession.setOverride("C");
		mockSession.setTestType("48");
		
		IVRUserSession session = new IVRUserSession();
		session.setSessionId(sessionid);
		session.setFacsInqType(IVRConstants.INQUIRY_BY_TN);
		session.setCurrentAssignmentResponse("xyz");
		session.setLosDbResponse("mock-losdb-response-string");
		
		when(mockCacheService.getBySessionId(sessionid)).thenReturn(session);
		when(mockMltCacheService.getBySessionId(sessionid)).thenReturn(mockSession);
		when(mockTnValidation.validateFacsTN("1234567",session)).thenReturn(mockResponse);
	
		IVRWebHookResponseDto response = ivrMltServiceImpl.processMLD021(sessionid, currentState, dtmfInput);
		assertEquals(IVRHookReturnCodes.HOOK_RETURN_0, response.getHookReturnCode());
		
	}

	
	@Test
	void processMLD021InvalidValidTest() {
		String sessionid = "session123";
	
		String currentState = "MLD021";
		List<String>dtmfInput=new ArrayList<>();
		dtmfInput.add("1");
		dtmfInput.add("2");
		dtmfInput.add("1234567");
		dtmfInput.add("2");
		
		when(mockMltCacheService.getBySessionId(sessionid)).thenReturn(null);

		IVRWebHookResponseDto response = ivrMltServiceImpl.processMLD021(sessionid, currentState, dtmfInput);
		assertEquals(IVRConstants.INVALID_SESSION_ID, response.getHookReturnMessage());
		
	}
	
	
	@Test
	void checkTechWorkingFromSameLineOrNotTest() {
		String sessionid = "session123";
		String userInput = "3";
		String currentState = "MLD026";
		
		IvrMltSession mockSession = new IvrMltSession();
		mockSession.setSessionId(sessionid);
		mockSession.setOverride("C");
		mockSession.setTestType("48");
		
		when(mockMltCacheService.getBySessionId(sessionid)).thenReturn(mockSession);
	
		IVRWebHookResponseDto response = ivrMltServiceImpl.checkTechWorkingFromSameLineOrNot(sessionid, currentState, userInput);
		assertEquals(IVRHookReturnCodes.HOOK_RETURN_0, response.getHookReturnCode());
		
	}
	
	@Test
	void playTipRingDcOhmTest() throws JsonMappingException, JsonProcessingException {
		String sessionid = "session123";
		String currentState = "MLD080";
		
		IvrMltSession mockSession = new IvrMltSession();
		mockSession.setSessionId(sessionid);
		mockSession.setMltTestResult("abc");
		
		when(mockMltCacheService.getBySessionId(sessionid)).thenReturn(mockSession);
		when(mockObjectMapper.readValue(mockSession.getMltTestResult(), Mdata.class)).thenReturn(null);
		
		IVRWebHookResponseDto response = ivrMltServiceImpl.playTipRingDcOhm(sessionid, currentState);
		assertEquals(IVRHookReturnCodes.HOOK_RETURN_2, response.getHookReturnCode());
		
	}
	@Test
	void playTipRingDcOhmValidTest() throws JsonMappingException, JsonProcessingException {
		String sessionid = "session123";
		String currentState = "MLD080";
		
		IvrMltSession mockSession = new IvrMltSession();
		mockSession.setSessionId(sessionid);
		mockSession.setMltTestResult("{\"testRsp\":{\"d\":{\"item\":[{\"potsResults1\":{\"dcCraft\":{\"dcResTr\":\"10\"}}}]}}}");
		
		Mdata mdata=new Mdata();
		mdata = new ObjectMapper().readValue("{\"testRsp\":{\"d\":{\"item\":[{\"potsResults1\":{\"dcCraft\":{\"dcResTr\":\"10\"}}}]}}}", Mdata.class);
	    
		when(mockMltCacheService.getBySessionId(sessionid)).thenReturn(mockSession);
		when(mockObjectMapper.readValue(mockSession.getMltTestResult(), Mdata.class)).thenReturn(mdata);
		
		IVRWebHookResponseDto response = ivrMltServiceImpl.playTipRingDcOhm(sessionid, currentState);
		assertEquals(IVRHookReturnCodes.HOOK_RETURN_1, response.getHookReturnCode());
		
	}

	@Test
	void playTipGroundDcOhmTest() throws JsonMappingException, JsonProcessingException {
		String sessionid = "session123";
		String currentState = "MLD082";
		
		IvrMltSession mockSession = new IvrMltSession();
		mockSession.setSessionId(sessionid);
		mockSession.setMltTestResult("abc");
		
		when(mockMltCacheService.getBySessionId(sessionid)).thenReturn(mockSession);
		when(mockObjectMapper.readValue(mockSession.getMltTestResult(), Mdata.class)).thenReturn(null);
		
		IVRWebHookResponseDto response = ivrMltServiceImpl.playTipGroundDcOhm(sessionid, currentState);
		assertEquals(IVRHookReturnCodes.HOOK_RETURN_2, response.getHookReturnCode());
		
	}

	
	@Test
	void playTipGroundDcOhmValidTest() throws JsonMappingException, JsonProcessingException {
		String sessionid = "session123";
		String currentState = "MLD082";
		
		IvrMltSession mockSession = new IvrMltSession();
		mockSession.setSessionId(sessionid);
		mockSession.setMltTestResult("{\"testRsp\":{\"d\":{\"item\":[{\"potsResults1\":{\"dcCraft\":{\"dcResTg\":\"10\"}}}]}}}");
		
		Mdata mdata=new Mdata();
		mdata = new ObjectMapper().readValue("{\"testRsp\":{\"d\":{\"item\":[{\"potsResults1\":{\"dcCraft\":{\"dcResTg\":\"10\"}}}]}}}", Mdata.class);
	    
		when(mockMltCacheService.getBySessionId(sessionid)).thenReturn(mockSession);
		when(mockObjectMapper.readValue(mockSession.getMltTestResult(), Mdata.class)).thenReturn(mdata);
		
		IVRWebHookResponseDto response = ivrMltServiceImpl.playTipGroundDcOhm(sessionid, currentState);
		assertEquals(IVRHookReturnCodes.HOOK_RETURN_1, response.getHookReturnCode());
		
	}
	
	@Test
	void playRingGroundVoltsTest() throws JsonMappingException, JsonProcessingException {
		String sessionid = "session123";
		String currentState = "MLD088";
		
		IvrMltSession mockSession = new IvrMltSession();
		mockSession.setSessionId(sessionid);
		mockSession.setMltTestResult("{\"testRsp\":{\"d\":{\"item\":[{\"potsResults1\":{\"dcCraft\":{\"dcVoltRg\":\"10\"}}}]}}}");
		
		Mdata mdata=new Mdata();
		mdata = new ObjectMapper().readValue("{\"testRsp\":{\"d\":{\"item\":[{\"potsResults1\":{\"dcCraft\":{\"dcVoltRg\":\"10\"}}}]}}}", Mdata.class);
	    
		when(mockMltCacheService.getBySessionId(sessionid)).thenReturn(mockSession);
		when(mockObjectMapper.readValue(mockSession.getMltTestResult(), Mdata.class)).thenReturn(mdata);
		
		IVRWebHookResponseDto response = ivrMltServiceImpl.playRingGroundVolts(sessionid, currentState);
		assertEquals(IVRHookReturnCodes.HOOK_RETURN_1, response.getHookReturnCode());
		
	}
	
	@Test
	void playRingGroundVoltsInvalidTest() throws JsonMappingException, JsonProcessingException {
		String sessionid = "session123";
		String currentState = "MLD088";
		
		IvrMltSession mockSession = new IvrMltSession();
		mockSession.setSessionId(sessionid);
		mockSession.setMltTestResult("abc");
		
		when(mockMltCacheService.getBySessionId(sessionid)).thenReturn(mockSession);
		when(mockObjectMapper.readValue(mockSession.getMltTestResult(), Mdata.class)).thenReturn(null);
		
		IVRWebHookResponseDto response = ivrMltServiceImpl.playRingGroundVolts(sessionid, currentState);
		assertEquals(IVRHookReturnCodes.HOOK_RETURN_2, response.getHookReturnCode());
		
	}

	@Test
	void playRingGroundAcOhmTest() throws JsonMappingException, JsonProcessingException {
		String sessionid = "session123";
		String currentState = "MLD094";
		
		IvrMltSession mockSession = new IvrMltSession();
		mockSession.setSessionId(sessionid);
		mockSession.setMltTestResult("{\"testRsp\":{\"d\":{\"item\":[{\"potsResults1\":{\"ac\":{\"acResRg\":\"10\"}}}]}}}");
		
		Mdata mdata=new Mdata();
		mdata = new ObjectMapper().readValue("{\"testRsp\":{\"d\":{\"item\":[{\"potsResults1\":{\"ac\":{\"acResRg\":\"10\"}}}]}}}", Mdata.class);
	    
		when(mockMltCacheService.getBySessionId(sessionid)).thenReturn(mockSession);
		when(mockObjectMapper.readValue(mockSession.getMltTestResult(), Mdata.class)).thenReturn(mdata);
		
		IVRWebHookResponseDto response = ivrMltServiceImpl.playRingGroundAcOhm(sessionid, currentState);
		assertEquals(IVRHookReturnCodes.HOOK_RETURN_1, response.getHookReturnCode());
		
	}
	
	@Test
	void playRingGroundAcOhmInvalidTest() throws JsonMappingException, JsonProcessingException {
		String sessionid = "session123";
		String currentState = "MLD094";
		
		IvrMltSession mockSession = new IvrMltSession();
		mockSession.setSessionId(sessionid);
		mockSession.setMltTestResult("abc");
		
		when(mockMltCacheService.getBySessionId(sessionid)).thenReturn(mockSession);
		when(mockObjectMapper.readValue(mockSession.getMltTestResult(), Mdata.class)).thenReturn(null);
		
		IVRWebHookResponseDto response = ivrMltServiceImpl.playRingGroundAcOhm(sessionid, currentState);
		assertEquals(IVRHookReturnCodes.HOOK_RETURN_2, response.getHookReturnCode());
		
	}

	@Test
	public void testRetriveMLTResult() throws JsonMappingException, JsonProcessingException {
		
		String sessionid = "session123";
		IvrMltSession mockMltSession = new IvrMltSession();
		mockMltSession.setSessionId(sessionid);
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		
		when(mockMltCacheService.getBySessionId(sessionid)).thenReturn(mockMltSession);
		
		
		when(mockLoopCareServices.retrieveMLTTestResults(anyString())).thenReturn(null);
		response.setHookReturnCode(HOOK_RETURN_9);
		mockMltSession.setTestType("TONE+");
		when(mockLoopCareServices.validateMltResult(sessionid)).thenReturn(response);
		String returnCode = ivrMltServiceImpl.retriveMLTResult(sessionid).getHookReturnCode();
		assertEquals(HOOK_RETURN_9, returnCode);
		
		when(mockLoopCareServices.retrieveMLTTestResults(anyString())).thenReturn("testResult");
		
		response.setHookReturnCode(HOOK_RETURN_1);
		mockMltSession.setTestType("FULLX");
		when(mockLoopCareServices.validateMltResult(sessionid)).thenReturn(response);
		returnCode = ivrMltServiceImpl.retriveMLTResult(sessionid).getHookReturnCode();
		assertEquals(HOOK_RETURN_1, returnCode);
		
		response.setHookReturnCode(HOOK_RETURN_2);
		mockMltSession.setTestType("FULLX");
		when(mockLoopCareServices.validateMltResult(sessionid)).thenReturn(response);
		returnCode = ivrMltServiceImpl.retriveMLTResult(sessionid).getHookReturnCode();
		assertEquals(HOOK_RETURN_2, returnCode);
		
		response.setHookReturnCode(HOOK_RETURN_3);
		mockMltSession.setTestType("LOOPX");
		when(mockLoopCareServices.validateMltResult(sessionid)).thenReturn(response);
		returnCode = ivrMltServiceImpl.retriveMLTResult(sessionid).getHookReturnCode();
		assertEquals(HOOK_RETURN_3, returnCode);
		
		response.setHookReturnCode(HOOK_RETURN_4);
		mockMltSession.setTestType("LOOPX");
		when(mockLoopCareServices.validateMltResult(sessionid)).thenReturn(response);
		returnCode = ivrMltServiceImpl.retriveMLTResult(sessionid).getHookReturnCode();
		assertEquals(HOOK_RETURN_4, returnCode);
		
		response.setHookReturnCode(HOOK_RETURN_5);
		mockMltSession.setTestType("QUICKX");
		when(mockLoopCareServices.validateMltResult(sessionid)).thenReturn(response);
		returnCode = ivrMltServiceImpl.retriveMLTResult(sessionid).getHookReturnCode();
		assertEquals(HOOK_RETURN_5, returnCode);
		
		response.setHookReturnCode(HOOK_RETURN_6);
		mockMltSession.setTestType("QUICKX");
		when(mockLoopCareServices.validateMltResult(sessionid)).thenReturn(response);
		returnCode = ivrMltServiceImpl.retriveMLTResult(sessionid).getHookReturnCode();
		assertEquals(HOOK_RETURN_6, returnCode);
		
		response.setHookReturnCode(HOOK_RETURN_7);
		mockMltSession.setTestType("FULLX");
		when(mockLoopCareServices.validateMltResult(sessionid)).thenReturn(response);
		returnCode = ivrMltServiceImpl.retriveMLTResult(sessionid).getHookReturnCode();
		assertEquals(HOOK_RETURN_7, returnCode);
		
		response.setHookReturnCode(HOOK_RETURN_1);
		mockMltSession.setTestType("TONE+");
		when(mockLoopCareServices.validateMltResult(sessionid)).thenReturn(response);
		returnCode = ivrMltServiceImpl.retriveMLTResult(sessionid).getHookReturnCode();
		assertEquals(HOOK_RETURN_1, returnCode);
		
		response.setHookReturnCode(HOOK_RETURN_2);
		mockMltSession.setTestType("TONE+");
		when(mockLoopCareServices.validateMltResult(sessionid)).thenReturn(response);
		returnCode = ivrMltServiceImpl.retriveMLTResult(sessionid).getHookReturnCode();
		assertEquals(HOOK_RETURN_2, returnCode);
		
		response.setHookReturnCode(HOOK_RETURN_3);
		mockMltSession.setTestType("TONE+");
		when(mockLoopCareServices.validateMltResult(sessionid)).thenReturn(response);
		returnCode = ivrMltServiceImpl.retriveMLTResult(sessionid).getHookReturnCode();
		assertEquals(HOOK_RETURN_3, returnCode);
		
		response.setHookReturnCode(HOOK_RETURN_4);
		mockMltSession.setTestType("TONE+");
		when(mockLoopCareServices.validateMltResult(sessionid)).thenReturn(response);
		returnCode = ivrMltServiceImpl.retriveMLTResult(sessionid).getHookReturnCode();
		assertEquals(HOOK_RETURN_4, returnCode);
		
		response.setHookReturnCode(HOOK_RETURN_5);
		mockMltSession.setTestType("TONE+");
		when(mockLoopCareServices.validateMltResult(sessionid)).thenReturn(response);
		returnCode = ivrMltServiceImpl.retriveMLTResult(sessionid).getHookReturnCode();
		assertEquals(HOOK_RETURN_5, returnCode);
		
		response.setHookReturnCode(HOOK_RETURN_6);
		mockMltSession.setTestType("TONE+");
		when(mockLoopCareServices.validateMltResult(sessionid)).thenReturn(response);
		returnCode = ivrMltServiceImpl.retriveMLTResult(sessionid).getHookReturnCode();
		assertEquals(HOOK_RETURN_6, returnCode);
		
		response.setHookReturnCode(HOOK_RETURN_8);
		mockMltSession.setTestType("TONE+");
		when(mockLoopCareServices.validateMltResult(sessionid)).thenReturn(response);
		returnCode = ivrMltServiceImpl.retriveMLTResult(sessionid).getHookReturnCode();
		assertEquals(HOOK_RETURN_8, returnCode);
	}
	
	@Test
	void validateFacsTnToneTest() {
		String sessionid = "session123";
		String currentState = "MLD021";
		List<String>dtmfInput=new ArrayList<>();
		dtmfInput.add("4");
		dtmfInput.add("1234567");
		
		
		IVRWebHookResponseDto mockResponse = new IVRWebHookResponseDto();
		mockResponse.setHookReturnMessage(VALID_TN_MSG);
		mockResponse.setHookReturnCode(IVRHookReturnCodes.HOOK_RETURN_1);
		
		IvrMltSession mockSession = new IvrMltSession();
		mockSession.setSessionId(sessionid);
		mockSession.setOverride("C");
		mockSession.setTestType("48");
		
		IVRUserSession session = new IVRUserSession();
		session.setSessionId(sessionid);
		session.setFacsInqType(IVRConstants.INQUIRY_BY_TN);
		session.setCurrentAssignmentResponse("xyz");
		session.setLosDbResponse("mock-losdb-response-string");
		
		when(mockCacheService.getBySessionId(sessionid)).thenReturn(session);
		when(mockMltCacheService.getBySessionId(sessionid)).thenReturn(mockSession);
		when(mockTnValidation.validateFacsTN("1234567",session)).thenReturn(mockResponse);
	
		IVRWebHookResponseDto response = ivrMltServiceImpl.validateFacsTnTone(sessionid, currentState, dtmfInput);
		assertEquals(IVRHookReturnCodes.HOOK_RETURN_1, response.getHookReturnCode());
		
	}

	@Test
	void validateFacsTnToneRemoveTest() {
		String sessionid = "session123";
		String currentState = "MLD021";
		List<String>dtmfInput=new ArrayList<>();
		dtmfInput.add("5");
		dtmfInput.add("1234567");
		
		
		IVRWebHookResponseDto mockResponse = new IVRWebHookResponseDto();
		mockResponse.setHookReturnMessage(VALID_TN_MSG);
		mockResponse.setHookReturnCode(IVRHookReturnCodes.HOOK_RETURN_1);
		
		IvrMltSession mockSession = new IvrMltSession();
		mockSession.setSessionId(sessionid);
		mockSession.setOverride("C");
		mockSession.setTestType("48");
		
		IVRUserSession session = new IVRUserSession();
		session.setSessionId(sessionid);
		session.setFacsInqType(IVRConstants.INQUIRY_BY_TN);
		session.setCurrentAssignmentResponse("xyz");
		session.setLosDbResponse("mock-losdb-response-string");
		
		when(mockCacheService.getBySessionId(sessionid)).thenReturn(session);
		when(mockMltCacheService.getBySessionId(sessionid)).thenReturn(mockSession);
		when(mockTnValidation.validateFacsTN("1234567",session)).thenReturn(mockResponse);
	
		IVRWebHookResponseDto response = ivrMltServiceImpl.validateFacsTnTone(sessionid, currentState, dtmfInput);
		assertEquals(IVRHookReturnCodes.HOOK_RETURN_1, response.getHookReturnCode());
		
	}

	
}
