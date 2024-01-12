/**
 * 
 */
package com.lumen.fastivr.IVRChangeStatusCablePair;

import static com.lumen.fastivr.IVRUtils.IVRConstants.INVALID_SESSION_ID;
import static com.lumen.fastivr.IVRUtils.IVRConstants.VALID_TN_MSG;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_1;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.net.http.HttpTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumen.fastivr.IVRCNF.Dto.changePairStatus.ChangePairStatusResponse;
import com.lumen.fastivr.IVRCNF.Dto.changePairStatus.InputData;
import com.lumen.fastivr.IVRCNF.Dto.changePairStatus.InputDataRequestBody;
import com.lumen.fastivr.IVRCNF.helper.IVRCnfHelper;
import com.lumen.fastivr.IVRCacheManagement.IVRCacheService;
import com.lumen.fastivr.IVRDto.IVRParameter;
import com.lumen.fastivr.IVRDto.IVRWebHookResponseDto;
import com.lumen.fastivr.IVRDto.MessageStatus;
import com.lumen.fastivr.IVRDto.LOSDB.TNInfoResponse;
import com.lumen.fastivr.IVRLFACS.IVRLfacsServiceHelper;
import com.lumen.fastivr.IVRLFACS.IVRLfacsServiceImpl;
import com.lumen.fastivr.IVRLFACS.LfacsValidation;
import com.lumen.fastivr.IVRSessionManagement.IVRUserSession;
import com.lumen.fastivr.IVRUtils.IVRConstants;
import com.lumen.fastivr.IVRUtils.IVRHookReturnCodes;
import com.lumen.fastivr.httpclient.IVRHttpClient;

@ExtendWith(MockitoExtension.class)
public class IVRChangeStatusCablePairServiceImplTest {


	@Mock
	private IVRCacheService mockCacheService;

	@InjectMocks
	private IVRChangeStatusCablePairServiceImpl cablePairServiceImpl;

	@Mock
	private LfacsValidation mockLfacsTNValidation;
	
	@Mock
	private IVRLfacsServiceHelper mockIVRLfacsServiceHelper;

	@Mock
	private IVRLfacsServiceImpl mockIVRLfacsServiceImpl;
	
	@Mock
	private IVRCnfHelper ivrCnfHelper;

	@Mock
	private ObjectMapper mockObjectMapper;

	@Mock
	IVRHttpClient mockIvrHttpClient;

	@Test
	void processFPD011_VALIDSESSION() {
		String sessionid = "session123";
		String userInput = "1234567";
		String currentState = IVRConstants.STATE_FPD011;
		IVRWebHookResponseDto mockResponse = new IVRWebHookResponseDto();
		mockResponse.setHookReturnMessage(VALID_TN_MSG);
		mockResponse.setHookReturnCode(HOOK_RETURN_1);
		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId(sessionid);
		when(mockCacheService.getBySessionId(sessionid)).thenReturn(mockSession);
		when(mockLfacsTNValidation.validateFacsTN(userInput, mockSession)).thenReturn(mockResponse);

		IVRWebHookResponseDto response = cablePairServiceImpl.processFPD011StateCode(sessionid, currentState, userInput);
		assertEquals(HOOK_RETURN_1, response.getHookReturnCode());
		assertEquals(VALID_TN_MSG, response.getHookReturnMessage());
	}
	
	@Test
	void processFPD011_INVALIDSESSION() {
		String sessionid = "session123";
		String userInput = "1234567";
		String currentState = IVRConstants.STATE_FPD011;
		IVRWebHookResponseDto mockResponse = new IVRWebHookResponseDto();
		mockResponse.setHookReturnMessage(INVALID_SESSION_ID);
		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId(sessionid);
		when(mockCacheService.getBySessionId(sessionid)).thenReturn(null);

		IVRWebHookResponseDto response = cablePairServiceImpl.processFPD011StateCode(sessionid, currentState, userInput);
		assertEquals(INVALID_SESSION_ID, response.getHookReturnMessage());
	}
	
	@Test
	void processFPD060StateCode_3() throws JsonMappingException, JsonProcessingException,
			InterruptedException, ExecutionException, HttpTimeoutException {

		String sessionId = "session123";
		String currentState = "STATE_FPF060";


		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId(sessionId);
		mockSession.setCurrentAssignmentResponse("xyz");
		mockSession.setCanBePagedMobile(false);
		List<String> userInputDTMFList = new ArrayList<>();
		userInputDTMFList.add("10");
		userInputDTMFList.add("605");
		userInputDTMFList.add("5");

		TNInfoResponse mockTnInfo = new TNInfoResponse();
		mockTnInfo.setTn("1234567890");
		mockTnInfo.setPrimaryNPA("123");
		mockTnInfo.setPrimaryNXX("456");
		String jsonRequestString = "mock-request-string";
		String mockLfacsResponse = "mock-lfacs-response";
		String mockLosDbJsonString = "mock-losdb-response-string";
		InputData inputData = new InputData();
		MessageStatus ms = new MessageStatus();
		ms.setErrorStatus("S");
		ChangePairStatusResponse changePairStatusResponse = new ChangePairStatusResponse();
		changePairStatusResponse.setMessageStatus(ms);
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		IVRParameter param = new  IVRParameter();
		param.setData("GRG");
		
		List<IVRParameter> ivrList = new ArrayList<IVRParameter>();
		ivrList.add(param);
		response.setParameters(ivrList);
		InputDataRequestBody mockRequest = new InputDataRequestBody();
		mockRequest.setInputData(inputData);
		mockSession.setLosDbResponse(mockLosDbJsonString);

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(mockSession);
		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(mockLosDbJsonString)).thenReturn(mockTnInfo);
		when(ivrCnfHelper.buildChangePairStatusRequest(any(), any(), anyString(), anyString(),
				anyString())).thenReturn(inputData);
		when(mockObjectMapper.writeValueAsString(any())).thenReturn(jsonRequestString);
		when(mockIvrHttpClient.httpPostCall(anyString(), any(), anyString(), anyString())).thenReturn(mockLfacsResponse);
		when(mockIVRLfacsServiceHelper.cleanResponseString(mockLfacsResponse)).thenReturn(mockLfacsResponse);
		when(mockObjectMapper.readValue(mockLfacsResponse, ChangePairStatusResponse.class))
				.thenReturn(changePairStatusResponse);

		IVRWebHookResponseDto actualResponse = cablePairServiceImpl.processFPD060StateCode(sessionId, currentState,response, userInputDTMFList);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_3, actualResponse.getHookReturnCode());
	}
	
	@Test
	void processFPD060StateCode_2() throws JsonMappingException, JsonProcessingException,
			InterruptedException, ExecutionException, HttpTimeoutException {

		String sessionId = "session123";
		String currentState = "STATE_FPD060";


		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId(sessionId);
		mockSession.setCurrentAssignmentResponse("xyz");
		mockSession.setCanBePagedMobile(true);
		List<String> userInputDTMFList = new ArrayList<>();
		userInputDTMFList.add("10");
		userInputDTMFList.add("605");
		userInputDTMFList.add("5");

		TNInfoResponse mockTnInfo = new TNInfoResponse();
		mockTnInfo.setTn("1234567890");
		mockTnInfo.setPrimaryNPA("123");
		mockTnInfo.setPrimaryNXX("456");
		String jsonRequestString = "mock-request-string";
		String mockLfacsResponse = "mock-lfacs-response";
		String mockLosDbJsonString = "mock-losdb-response-string";
		InputData inputData = new InputData();
		MessageStatus ms = new MessageStatus();
		ms.setErrorStatus("S");
		ChangePairStatusResponse changePairStatusResponse = new ChangePairStatusResponse();
		changePairStatusResponse.setMessageStatus(ms);
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		IVRParameter param = new  IVRParameter();
		param.setData("GRG");
		
		List<IVRParameter> ivrList = new ArrayList<IVRParameter>();
		ivrList.add(param);
		response.setParameters(ivrList);
		InputDataRequestBody mockRequest = new InputDataRequestBody();
		mockRequest.setInputData(inputData);
		mockSession.setLosDbResponse(mockLosDbJsonString);

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(mockSession);
		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(mockLosDbJsonString)).thenReturn(mockTnInfo);
		when(ivrCnfHelper.buildChangePairStatusRequest(any(), any(), anyString(), anyString(),
				anyString())).thenReturn(inputData);
		when(mockObjectMapper.writeValueAsString(any())).thenReturn(jsonRequestString);
		when(mockIvrHttpClient.httpPostCall(anyString(), any(), anyString(), anyString())).thenReturn(mockLfacsResponse);
		when(mockIVRLfacsServiceHelper.cleanResponseString(mockLfacsResponse)).thenReturn(mockLfacsResponse);
		when(mockObjectMapper.readValue(mockLfacsResponse, ChangePairStatusResponse.class))
				.thenReturn(changePairStatusResponse);

		IVRWebHookResponseDto actualResponse = cablePairServiceImpl.processFPD060StateCode(sessionId, currentState,response, userInputDTMFList);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_2, actualResponse.getHookReturnCode());
	}
	
	@Test
	void processFPD060StateCode_Error() throws JsonMappingException, JsonProcessingException,
			InterruptedException, ExecutionException, HttpTimeoutException {

		String sessionId = "session123";
		String currentState = "STATE_FPD060";


		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId(sessionId);
		mockSession.setCurrentAssignmentResponse("xyz");
		mockSession.setCanBePagedMobile(true);
		List<String> userInputDTMFList = new ArrayList<>();
		userInputDTMFList.add("10");
		userInputDTMFList.add("605");
		userInputDTMFList.add("5");

		TNInfoResponse mockTnInfo = new TNInfoResponse();
		mockTnInfo.setTn("1234567890");
		mockTnInfo.setPrimaryNPA("123");
		mockTnInfo.setPrimaryNXX("456");
		String jsonRequestString = "mock-request-string";
		String mockLfacsResponse = "mock-lfacs-response";
		String mockLosDbJsonString = "mock-losdb-response-string";
		InputData inputData = new InputData();
		MessageStatus ms = new MessageStatus();
		ms.setErrorStatus("F");
		ChangePairStatusResponse changePairStatusResponse = new ChangePairStatusResponse();
		changePairStatusResponse.setMessageStatus(ms);
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		IVRParameter param = new  IVRParameter();
		param.setData("GRG");
		
		List<IVRParameter> ivrList = new ArrayList<IVRParameter>();
		ivrList.add(param);
		response.setParameters(ivrList);
		InputDataRequestBody mockRequest = new InputDataRequestBody();
		mockRequest.setInputData(inputData);
		mockSession.setLosDbResponse(mockLosDbJsonString);

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(mockSession);
		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(mockLosDbJsonString)).thenReturn(mockTnInfo);
		when(ivrCnfHelper.buildChangePairStatusRequest(any(), any(), anyString(), anyString(),
				anyString())).thenReturn(inputData);
		when(mockObjectMapper.writeValueAsString(any())).thenReturn(jsonRequestString);
		when(mockIvrHttpClient.httpPostCall(anyString(), any(), anyString(), anyString())).thenReturn(mockLfacsResponse);
		when(mockIVRLfacsServiceHelper.cleanResponseString(mockLfacsResponse)).thenReturn(mockLfacsResponse);
		when(mockObjectMapper.readValue(mockLfacsResponse, ChangePairStatusResponse.class))
				.thenReturn(changePairStatusResponse);

		IVRWebHookResponseDto actualResponse = cablePairServiceImpl.processFPD060StateCode(sessionId, currentState,response, userInputDTMFList);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN, actualResponse.getHookReturnCode());
	}
	
	@Test
	void processFPD060StateCode_InvalidSession() throws JsonMappingException, JsonProcessingException,
			InterruptedException, ExecutionException, HttpTimeoutException {

		String sessionId = "1245";
		
		IVRUserSession mockSession = null;
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
	

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(mockSession);

		IVRWebHookResponseDto actualResponse = cablePairServiceImpl.processFPD060StateCode(sessionId, null,response, null);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN, actualResponse.getHookReturnCode());
	}
	
	@Test
	void processFPD060DefectiveCodeTest() throws JsonMappingException, JsonProcessingException,
			InterruptedException, ExecutionException, HttpTimeoutException {
		
		String sessionId = "1245";
		String currentState = "FP0040";
		String nextState = "FPD060";
		String userInputDTMFList = "7389117,10,605,5";

		IVRWebHookResponseDto actualResponse = cablePairServiceImpl.processFPD060DefectiveCode(sessionId, currentState,nextState, userInputDTMFList);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_1, actualResponse.getHookReturnCode());
		currentState = "FP0050";
		IVRWebHookResponseDto actualResponse1 = cablePairServiceImpl.processFPD060DefectiveCode(sessionId, currentState,nextState, userInputDTMFList);
		assertEquals(IVRHookReturnCodes.HOOK_RETURN_1, actualResponse1.getHookReturnCode());
	}
	
	@Test
	void processFPD100StateCode_Test() {
		IVRWebHookResponseDto mockResponse = new IVRWebHookResponseDto();
		
		IVRWebHookResponseDto response = cablePairServiceImpl.processFPD100StateCode(mockResponse);
		assertEquals(IVRHookReturnCodes.HOOK_RETURN_8, response.getHookReturnCode());
	}
	
	@Test
	void processFPD005StateCodeNormalFlow_Test() {
		String sessionid = "session123";
		String userInput = "1234567";
		String previousState = "MM0001";
		String currentState = IVRConstants.STATE_FPD011;
		IVRWebHookResponseDto mockResponse = new IVRWebHookResponseDto();
		
		mockResponse.setHookReturnCode(IVRHookReturnCodes.HOOK_RETURN_0);
		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId(sessionid);
		when(mockCacheService.getBySessionId(sessionid)).thenReturn(mockSession);

		IVRWebHookResponseDto response = cablePairServiceImpl.processFPD005StateCode(sessionid, previousState, currentState);
		assertEquals(IVRHookReturnCodes.HOOK_RETURN_0, response.getHookReturnCode());
		
	}
	
	@Test
	void processFPD005StateCode_Test() {
		String sessionid = "session123";
		String userInput = "1234567";
		String previousState = "FND741";
		String currentState = IVRConstants.STATE_FPD011;
		IVRWebHookResponseDto mockResponse = new IVRWebHookResponseDto();
		
		mockResponse.setHookReturnCode(IVRHookReturnCodes.HOOK_RETURN_1);
		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId(sessionid);
		when(mockCacheService.getBySessionId(sessionid)).thenReturn(mockSession);

		IVRWebHookResponseDto response = cablePairServiceImpl.processFPD005StateCode(sessionid, previousState, currentState);
		assertEquals(IVRHookReturnCodes.HOOK_RETURN_1, response.getHookReturnCode());
		
	}
	
	@Test
	void processFPD005_INVALIDSESSION() {
		String sessionid = "session123";
		String userInput = "1234567";
		String currentState = IVRConstants.STATE_FPD011;
		IVRWebHookResponseDto mockResponse = new IVRWebHookResponseDto();
		mockResponse.setHookReturnMessage(INVALID_SESSION_ID);
		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId(sessionid);
		when(mockCacheService.getBySessionId(sessionid)).thenReturn(null);

		IVRWebHookResponseDto response = cablePairServiceImpl.processFPD005StateCode(sessionid, null, currentState);
		assertEquals(INVALID_SESSION_ID, response.getHookReturnMessage());
	}
	
	@Test
	void processFPD020StateCode_3() {

		String sessionId = "session123";
		String currentState = "STATE_FPD020";
		List<String> userInputDTMFList = new ArrayList<>();
		userInputDTMFList.add("*7141*57#");
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		IVRParameter param = new  IVRParameter();
		param.setData("GRG");
		
		List<IVRParameter> ivrList = new ArrayList<IVRParameter>();
		ivrList.add(param);
		response.setParameters(ivrList);

		
		when(ivrCnfHelper.convertInputCodesToAlphabets(anyString())).thenReturn(anyString());

		IVRWebHookResponseDto actualResponse = cablePairServiceImpl.processFPD020StateCode(sessionId, currentState, userInputDTMFList.get(0));

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_3, actualResponse.getHookReturnCode());
	}
	
	@Test
	void processFPD020StateCode_Error() {

		String sessionId = "session123";
		String currentState = "STATE_FPD020";
		List<String> userInputDTMFList = new ArrayList<>();
		userInputDTMFList.add("*714157#");
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		IVRParameter param = new  IVRParameter();
		param.setData("GRG");
		
		List<IVRParameter> ivrList = new ArrayList<IVRParameter>();
		ivrList.add(param);
		response.setParameters(ivrList);

		
		when(ivrCnfHelper.convertInputCodesToAlphabets(anyString())).thenReturn(null);

		IVRWebHookResponseDto actualResponse = cablePairServiceImpl.processFPD020StateCode(sessionId, currentState, userInputDTMFList.get(0));

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_0, actualResponse.getHookReturnCode());
	}
	
}