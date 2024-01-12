package com.lumen.fastivr.IVRCNF;

import static com.lumen.fastivr.IVRCNF.utils.IVRCNFConstants.INQUIRY_BY_SERVICE_ORDER;
import static com.lumen.fastivr.IVRCNF.utils.IVRCNFConstants.INQUIRY_BY_TROUBLE_TICKET;
import static com.lumen.fastivr.IVRCNF.utils.IVRCNFConstants.STATE_FND055;
import static com.lumen.fastivr.IVRCNF.utils.IVRCNFConstants.STATE_FND075;
import static com.lumen.fastivr.IVRCNF.utils.IVRCNFConstants.TN_NOT_FOUND_IN_TABLE;
import static com.lumen.fastivr.IVRUtils.IVRConstants.INVALID_SESSION_ID;
import static com.lumen.fastivr.IVRUtils.IVRConstants.VALID_TN_MSG;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_0;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_1;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_2;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_3;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_4;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_5;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_6;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_7;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_8;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.net.http.HttpTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumen.fastivr.IVRCNF.Dto.ChangeLoopAssignmentRequestDto;
import com.lumen.fastivr.IVRCNF.Dto.ChangeLoopAssignmentRequestInputData;
import com.lumen.fastivr.IVRCNF.Dto.ChangeLoopAssignmentResponseDto;
import com.lumen.fastivr.IVRCNF.Dto.LoopQualNIIServiceRequest;
import com.lumen.fastivr.IVRCNF.Dto.MainChangeTicketLoopRequest;
import com.lumen.fastivr.IVRCNF.Dto.MainChangeTicketLoopResponse;
import com.lumen.fastivr.IVRCNF.Dto.NIIServiceResponse;
import com.lumen.fastivr.IVRCNF.Dto.NetworkInfrastructure;
import com.lumen.fastivr.IVRCNF.entity.IVRCnfEntity;
import com.lumen.fastivr.IVRCNF.helper.IVRCnfHelper;
import com.lumen.fastivr.IVRCNF.repository.IVRCnfCacheService;
import com.lumen.fastivr.IVRCNF.service.IVRCnfPagerTextFormation;
import com.lumen.fastivr.IVRCNF.service.IVRCnfServiceImpl;
import com.lumen.fastivr.IVRCacheManagement.IVRCacheService;
import com.lumen.fastivr.IVRDto.CurrentAssignmentResponseDto;
import com.lumen.fastivr.IVRDto.ErrorList;
import com.lumen.fastivr.IVRDto.HostErrorList;
import com.lumen.fastivr.IVRDto.IVRParameter;
import com.lumen.fastivr.IVRDto.IVRWebHookResponseDto;
import com.lumen.fastivr.IVRDto.LOOP;
import com.lumen.fastivr.IVRDto.MessageStatus;
import com.lumen.fastivr.IVRDto.ReturnDataSet;
import com.lumen.fastivr.IVRDto.SEG;
import com.lumen.fastivr.IVRDto.SO;
import com.lumen.fastivr.IVRDto.LOSDB.TNInfoResponse;
import com.lumen.fastivr.IVRDto.RetrieveMaintenanceChangeTicket.RetrieveMaintenanceChangeTicketRequest;
import com.lumen.fastivr.IVRDto.RetrieveMaintenanceChangeTicket.RetrieveMaintenanceChangeTicketResponse;
import com.lumen.fastivr.IVRDto.retrieveLoopAssignment.RetLoopAssigInputData;
import com.lumen.fastivr.IVRDto.retrieveLoopAssignment.RetrieveLoopAssignmentRequest;
import com.lumen.fastivr.IVRDto.retrieveLoopAssignment.RetrieveLoopAssignmentResponse;
import com.lumen.fastivr.IVRLFACS.IVRLfacsPagerTextFormation;
import com.lumen.fastivr.IVRLFACS.IVRLfacsServiceHelper;
import com.lumen.fastivr.IVRLFACS.IVRLfacsServiceImpl;
import com.lumen.fastivr.IVRLFACS.LfacsValidation;
import com.lumen.fastivr.IVRLFACS.SparePairPageBuilder;
import com.lumen.fastivr.IVRSessionManagement.IVRUserSession;
import com.lumen.fastivr.IVRUtils.IVRConstants;
import com.lumen.fastivr.IVRUtils.IVRHookReturnCodes;
import com.lumen.fastivr.IVRUtils.IVRLfacsConstants;
import com.lumen.fastivr.httpclient.IVRHttpClient;

@ExtendWith(MockitoExtension.class)
public class IVRCnfServiceImplTest {

	@Mock
	private IVRCnfHelper mockIvrCnfHelper;

	@Mock
	private IVRCacheService mockCacheService;

	@Mock
	private IVRCnfCacheService mockIvrCnfCacheService;

	@Mock
	private LfacsValidation mockLfacsTNValidation;

	@Mock
	private IVRLfacsServiceHelper mockIVRLfacsServiceHelper;

	@Mock
	private IVRLfacsServiceImpl mockIVRLfacsServiceImpl;

	@Mock
	private IVRLfacsPagerTextFormation mockIvrLfacespagerTextFormation;

	@Mock
	private ObjectMapper mockObjectMapper;

	@Mock
	IVRHttpClient mockIvrHttpClient;
	
	@Mock
	IVRCnfPagerTextFormation ivrCnfPagerTextFormation;	

	@InjectMocks
	private IVRCnfServiceImpl ivrCnfServiceImpl;

	MessageStatus messageStatus = new MessageStatus();
	HostErrorList hostErrorList = new HostErrorList();
	List<HostErrorList> HostErrorLists = new ArrayList<>();
	ErrorList errorList = new ErrorList();
	List<ErrorList> errorLists = new ArrayList<>();
	ReturnDataSet returnDataSet = new ReturnDataSet();
	IVRWebHookResponseDto ivrWebHookResponseDto = new IVRWebHookResponseDto();
	CurrentAssignmentResponseDto currentAssignmentResponseDto = null;
	TNInfoResponse tnInfoResponse = null;
	String currentAssignment = null;

	SO so = new SO();
	List<SO> soList = new ArrayList<>();
	LOOP loop = new LOOP();
	List<LOOP> loopList = new ArrayList<>();
	RetrieveLoopAssignmentResponse retrieveLoopAssignmentResponse = new RetrieveLoopAssignmentResponse();

	@BeforeEach
	void setUp() throws Exception {

		currentAssignmentResponseDto = new CurrentAssignmentResponseDto();
		ReturnDataSet dataSet = new ReturnDataSet();
		List<LOOP> loopList = new ArrayList<LOOP>();
		LOOP loop = new LOOP();

		List<SEG> segList = new ArrayList<SEG>();

		SEG seg = new SEG();

		segList.add(seg);
		loop.setSEG(segList);
		loopList.add(loop);
		dataSet.setLoop(loopList);
		currentAssignmentResponseDto.setReturnDataSet(dataSet);
	}

	@Test
	void testProcessFND035Code_HookCode3() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";

		String currentState = "FND035";

		String result = "AT";

		IVRUserSession session = new IVRUserSession();

		session.setSessionId(sessionId);

		List<String> userInputDTMFList = new ArrayList<String>();

		userInputDTMFList.add("*2181*12345");

		userInputDTMFList.add("1");

		when(mockIvrCnfHelper.convertInputCodesToAlphabets(anyString())).thenReturn(result);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND035(sessionId, currentState,
				userInputDTMFList);

		assertEquals(HOOK_RETURN_2, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFND035Code_HookCode3_2() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";

		String currentState = "FND035";

		IVRUserSession session = new IVRUserSession();

		session.setSessionId(sessionId);

		List<String> userInputDTMFList = new ArrayList<String>();

		userInputDTMFList.add("*2181*12345");

		userInputDTMFList.add("2");

		when(mockIvrCnfHelper.convertInputCodesToAlphabets(anyString())).thenReturn("AT");

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND035(sessionId, currentState,
				userInputDTMFList);

		assertEquals(HOOK_RETURN_2, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFND035Code_HookCode3_3() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";

		String currentState = "FND035";

		IVRUserSession session = new IVRUserSession();

		session.setSessionId(sessionId);

		List<String> userInputDTMFList = new ArrayList<String>();

		userInputDTMFList.add("*2181*12345");

		userInputDTMFList.add("3");

		when(mockIvrCnfHelper.convertInputCodesToAlphabets(anyString())).thenReturn("AT");

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND035(sessionId, currentState,
				userInputDTMFList);

		assertEquals(HOOK_RETURN_2, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFND035Code_HookCode3_EMPTY() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";

		String currentState = "FND035";

		IVRUserSession session = new IVRUserSession();

		session.setSessionId(sessionId);

		List<String> userInputDTMFList = new ArrayList<String>();

		userInputDTMFList.add("*2181*12345");

		userInputDTMFList.add("");

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND035(sessionId, currentState,
				userInputDTMFList);

		assertEquals(HOOK_RETURN_0, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFND035Code_HookCode3_WithBeginIndex() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";

		String currentState = "FND035";

		IVRUserSession session = new IVRUserSession();

		session.setSessionId(sessionId);

		List<String> userInputDTMFList = new ArrayList<String>();

		userInputDTMFList.add("*2381*21345");

		userInputDTMFList.add("1");

		when(mockIvrCnfHelper.convertInputCodesToAlphabets(anyString())).thenReturn("AT");

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND035(sessionId, currentState,
				userInputDTMFList);

		assertEquals(HOOK_RETURN_2, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFND035Code_HookCode() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";

		String currentState = "FND035";

		IVRUserSession session = new IVRUserSession();

		session.setSessionId(sessionId);

		List<String> userInputDTMFList = new ArrayList<String>();

		userInputDTMFList.add("*218112345");

		userInputDTMFList.add("1");

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND035(sessionId, currentState,
				userInputDTMFList);

		assertEquals(HOOK_RETURN, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFND035Code_HookCode_0() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";

		String currentState = "FND035";

		IVRUserSession session = new IVRUserSession();

		session.setSessionId(sessionId);

		List<String> userInputDTMFList = new ArrayList<String>();

		userInputDTMFList.add("*2181*123");

		userInputDTMFList.add("1");

		when(mockIvrCnfHelper.convertInputCodesToAlphabets(anyString())).thenReturn("AT");

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND035(sessionId, currentState,
				userInputDTMFList);

		assertEquals(HOOK_RETURN_0, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFND035Code_HookCode_1() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";

		String currentState = "FND035";

		IVRUserSession session = new IVRUserSession();

		session.setSessionId(sessionId);

		List<String> userInputDTMFList = new ArrayList<String>();

		userInputDTMFList.add("*2181*12345678");

		userInputDTMFList.add("1");

		when(mockIvrCnfHelper.convertInputCodesToAlphabets(anyString())).thenReturn("AT");

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND035(sessionId, currentState,
				userInputDTMFList);

		assertEquals(HOOK_RETURN_1, actualResponse.getHookReturnCode());
	}

	@Test
	void processFND055_VALIDSESSION() {

		String sessionid = "session123";

		String userInput = "1234567";

		String currentState = STATE_FND055;

		IVRWebHookResponseDto mockResponse = new IVRWebHookResponseDto();

		mockResponse.setHookReturnMessage(VALID_TN_MSG);

		mockResponse.setHookReturnCode(HOOK_RETURN_1);

		IVRUserSession mockSession = new IVRUserSession();

		mockSession.setSessionId(sessionid);

		IVRCnfEntity mockCnfSession = new IVRCnfEntity();

		mockCnfSession.setSessionId(sessionid);

		when(mockCacheService.getBySessionId(sessionid)).thenReturn(mockSession);

		when(mockIvrCnfCacheService.getBySessionId(sessionid)).thenReturn(mockCnfSession);

		when(mockLfacsTNValidation.validateFacsTN(userInput, mockSession)).thenReturn(mockResponse);

		IVRWebHookResponseDto response = ivrCnfServiceImpl.processFND055(sessionid, currentState, userInput);

		assertEquals(HOOK_RETURN_1, response.getHookReturnCode());

		assertEquals(VALID_TN_MSG, response.getHookReturnMessage());
	}

	@Test
	void processFND055_TN_NOTFOUND() {

		String sessionid = "session123";

		String userInput = "1234567";

		String currentState = STATE_FND055;

		IVRWebHookResponseDto mockResponse = new IVRWebHookResponseDto();

		mockResponse.setHookReturnMessage(VALID_TN_MSG);

		mockResponse.setHookReturnCode(HOOK_RETURN_0);

		IVRUserSession mockSession = new IVRUserSession();

		mockSession.setSessionId(sessionid);

		IVRCnfEntity mockCnfSession = new IVRCnfEntity();

		mockCnfSession.setSessionId(sessionid);

		when(mockCacheService.getBySessionId(sessionid)).thenReturn(mockSession);

		when(mockLfacsTNValidation.validateFacsTN(userInput, mockSession)).thenReturn(mockResponse);

		IVRWebHookResponseDto response = ivrCnfServiceImpl.processFND055(sessionid, currentState, userInput);

		assertEquals(HOOK_RETURN_0, response.getHookReturnCode());

		assertEquals(TN_NOT_FOUND_IN_TABLE, response.getHookReturnMessage());
	}

	@Test
	void processFND055_INVALIDSESSION() {

		String sessionid = "session123";

		String userInput = "1234567";

		String currentState = STATE_FND055;

		IVRWebHookResponseDto mockResponse = new IVRWebHookResponseDto();

		mockResponse.setHookReturnMessage(INVALID_SESSION_ID);

		IVRUserSession mockSession = new IVRUserSession();

		mockSession.setSessionId(sessionid);

		when(mockCacheService.getBySessionId(sessionid)).thenReturn(null);

		IVRWebHookResponseDto response = ivrCnfServiceImpl.processFND055(sessionid, currentState, userInput);

		assertEquals(INVALID_SESSION_ID, response.getHookReturnMessage());
	}

	@Test
	void testProcessFND059CodeRetLoopAssign_0() throws JsonMappingException, JsonProcessingException,
			InterruptedException, ExecutionException, HttpTimeoutException {

		String sessionId = "session123";
		String currentState = "STATE_FND059";

		so.setORD("True");
		soList.add(so);
		loop.setCKID("763 757-4229");
		loop.setSO(soList);
		loop.setTID("DPAAB");
		loopList.add(loop);
		returnDataSet.setLoop(loopList);
		returnDataSet.setPort1("test");
		currentAssignmentResponseDto.setReturnDataSet(returnDataSet);
		
		MessageStatus messageStatus = new MessageStatus();
		messageStatus.setErrorStatus("S");
		currentAssignmentResponseDto.setMessageStatus(messageStatus);
		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId(sessionId);
		mockSession.setCurrentAssignmentResponse("xyz");
		List<String> userInputDTMFList = new ArrayList<>();
		userInputDTMFList.add("1");

		TNInfoResponse mockTnInfo = new TNInfoResponse();
		mockTnInfo.setTn("1234567890");
		mockTnInfo.setPrimaryNPA("123");
		mockTnInfo.setPrimaryNXX("456");
		String jsonRequestString = "mock-request-string";
		String mockLfacsResponse = "mock-lfacs-response";
		String mockLosDbJsonString = "mock-losdb-response-string";
		RetrieveLoopAssignmentRequest mockRequest = new RetrieveLoopAssignmentRequest();
		RetLoopAssigInputData inputData = new RetLoopAssigInputData();
		mockRequest.setInputData(inputData);
		RetrieveLoopAssignmentResponse mockResponse = new RetrieveLoopAssignmentResponse();
		mockSession.setLosDbResponse(mockLosDbJsonString);
		mockSession.setRtrvLoopAssgMsgName(mockLosDbJsonString);

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(mockSession);
		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(mockLosDbJsonString)).thenReturn(mockTnInfo);
		when(mockIVRLfacsServiceHelper.buildRetriveLoopAssignInqRequest(anyString(), anyString(), anyString(), any(),
				any(), anyInt())).thenReturn(mockRequest);
		when(mockObjectMapper.writeValueAsString(mockRequest)).thenReturn(jsonRequestString);
		when(mockIVRLfacsServiceHelper.callSparePairLoopAssignInquiryLfacs(jsonRequestString, mockSession))
				.thenReturn(mockLfacsResponse);
		// when(mockIVRLfacsServiceHelper.cleanResponseString(mockLfacsResponse)).thenReturn(mockLfacsResponse);
		when(mockObjectMapper.readValue(mockSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponseDto);
		when(mockObjectMapper.readValue(mockLfacsResponse, RetrieveLoopAssignmentResponse.class))
				.thenReturn(mockResponse);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND059(sessionId, currentState);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_0, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFND059CodeRetLoopAssign_1_Ex() throws JsonMappingException, JsonProcessingException,
			InterruptedException, ExecutionException, HttpTimeoutException {

		String sessionId = "session123";
		String currentState = "STATE_FND059";

		so.setORD("True");
		soList.add(so);
		loop.setCKID("763 757-4229");
		loop.setSO(soList);
		loop.setTID("DPAAB");
		loopList.add(loop);
		returnDataSet.setLoop(loopList);
		returnDataSet.setPort1("test");
		currentAssignmentResponseDto.setReturnDataSet(returnDataSet);

		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId(sessionId);
		mockSession.setCurrentAssignmentResponse("xyz");
		List<String> userInputDTMFList = new ArrayList<>();
		userInputDTMFList.add("1");

		TNInfoResponse mockTnInfo = new TNInfoResponse();
		mockTnInfo.setTn("1234567890");
		mockTnInfo.setPrimaryNPA("123");
		mockTnInfo.setPrimaryNXX("456");
		String jsonRequestString = "mock-request-string";
		String mockLfacsResponse = "mock-lfacs-response";
		String mockLosDbJsonString = "mock-losdb-response-string";
		RetrieveLoopAssignmentRequest mockRequest = new RetrieveLoopAssignmentRequest();
		RetLoopAssigInputData inputData = new RetLoopAssigInputData();
		mockRequest.setInputData(inputData);
		RetrieveLoopAssignmentResponse mockResponse = new RetrieveLoopAssignmentResponse();
		mockSession.setLosDbResponse(mockLosDbJsonString);
		mockSession.setRtrvLoopAssgMsgName(mockLosDbJsonString);

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(mockSession);
		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(mockLosDbJsonString)).thenReturn(mockTnInfo);
		when(mockIVRLfacsServiceHelper.buildRetriveLoopAssignInqRequest(anyString(), anyString(), anyString(), any(),
				any(), anyInt())).thenReturn(mockRequest);
		// when(mockObjectMapper.writeValueAsString(mockRequest)).thenReturn(jsonRequestString);
//		when(mockIVRLfacsServiceHelper.callSparePairLoopAssignInquiryLfacs(jsonRequestString, mockSession))
//				.thenReturn(mockLfacsResponse);
//		when(mockIVRLfacsServiceHelper.cleanResponseString(mockLfacsResponse)).thenReturn(mockLfacsResponse);
		when(mockObjectMapper.readValue(mockSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(null);
//		when(mockObjectMapper.readValue(mockLfacsResponse, RetrieveLoopAssignmentResponse.class))
//				.thenReturn(mockResponse);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND059(sessionId, currentState);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_1, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFND059CodeRetLoopAssign_1()
			throws JsonMappingException, JsonProcessingException, InterruptedException, ExecutionException {

		String sessionId = "session123";

		String currentState = "STATE_FND059";

		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId(sessionId);

		when(mockCacheService.getBySessionId(Mockito.any(String.class))).thenReturn(null);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND059(sessionId, currentState);

		assertEquals(HOOK_RETURN, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFND060CodeRetLoopAssign_8() throws JsonMappingException, JsonProcessingException,
			InterruptedException, ExecutionException, HttpTimeoutException {

		String sessionId = "session123";
		String currentState = "STATE_FND060";

		so.setORD("True");
		soList.add(so);
		loop.setCKID("763 757-4229");
		loop.setSO(soList);
		loop.setTID("DPAAB");
		loopList.add(loop);
		returnDataSet.setLoop(loopList);
		returnDataSet.setPort1("test");
		currentAssignmentResponseDto.setReturnDataSet(returnDataSet);

		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId(sessionId);
		mockSession.setCurrentAssignmentResponse("xyz");
		List<String> userInputDTMFList = new ArrayList<>();
		userInputDTMFList.add("1");

		TNInfoResponse mockTnInfo = new TNInfoResponse();
		mockTnInfo.setTn("1234567890");
		mockTnInfo.setPrimaryNPA("123");
		mockTnInfo.setPrimaryNXX("456");
		String jsonRequestString = "mock-request-string";
		String mockLfacsResponse = "mock-lfacs-response";
		String mockLosDbJsonString = "mock-losdb-response-string";
		RetrieveLoopAssignmentRequest mockRequest = new RetrieveLoopAssignmentRequest();
		RetrieveLoopAssignmentResponse mockResponse = new RetrieveLoopAssignmentResponse();
		mockSession.setLosDbResponse(mockLosDbJsonString);
		mockSession.setRtrvLoopAssgMsgName(mockLfacsResponse);

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		response.setHookReturnCode("8");
		messageStatus.setErrorStatus("S");
		mockResponse.setMessageStatus(messageStatus);

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(mockSession);

		when(mockObjectMapper.readValue(mockSession.getRtrvLoopAssgMsgName(), RetrieveLoopAssignmentResponse.class))
				.thenReturn(mockResponse);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND060(sessionId, currentState);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_8, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFND060CodeRetLoopAssign_Hook_Return() throws JsonMappingException, JsonProcessingException,
			InterruptedException, ExecutionException, HttpTimeoutException {

		String sessionId = "session123";
		String currentState = "STATE_FND060";

		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId(sessionId);
		List<String> userInputDTMFList = new ArrayList<>();
		userInputDTMFList.add("1");

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		response.setSessionId(sessionId);

		response.setCurrentState(currentState);

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(null);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND060(sessionId, currentState);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFND060CodeRetLoopAssign_Exception() throws JsonMappingException, JsonProcessingException,
			InterruptedException, ExecutionException, HttpTimeoutException {

		String sessionId = "session123";
		String currentState = "STATE_FND060";

		so.setORD("True");
		soList.add(so);
		loop.setCKID("763 757-4229");
		loop.setSO(soList);
		loop.setTID("DPAAB");
		loopList.add(loop);
		returnDataSet.setLoop(loopList);
		returnDataSet.setPort1("test");
		currentAssignmentResponseDto.setReturnDataSet(returnDataSet);

		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId(sessionId);
		mockSession.setCurrentAssignmentResponse("xyz");
		List<String> userInputDTMFList = new ArrayList<>();
		userInputDTMFList.add("1");

		TNInfoResponse mockTnInfo = new TNInfoResponse();
		mockTnInfo.setTn("1234567890");
		mockTnInfo.setPrimaryNPA("123");
		mockTnInfo.setPrimaryNXX("456");
		String jsonRequestString = "mock-request-string";
		String mockLfacsResponse = "mock-lfacs-response";
		String mockLosDbJsonString = "mock-losdb-response-string";
		RetrieveLoopAssignmentRequest mockRequest = new RetrieveLoopAssignmentRequest();
		RetrieveLoopAssignmentResponse mockResponse = new RetrieveLoopAssignmentResponse();
		mockSession.setLosDbResponse(mockLosDbJsonString);
		mockSession.setRtrvLoopAssgMsgName(mockLfacsResponse);

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		response.setHookReturnCode("8");
		messageStatus.setErrorStatus("S");
		mockResponse.setMessageStatus(messageStatus);

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(mockSession);
//		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(mockLosDbJsonString)).thenReturn(mockTnInfo);
//		when(mockIVRLfacsServiceHelper.buildRetriveLoopAssignInqRequest(anyString(),anyString(),anyString(),any(),any(),anyInt())).thenReturn(mockRequest);
//		when(mockObjectMapper.writeValueAsString(mockRequest)).thenReturn(jsonRequestString);
//		when(mockIVRLfacsServiceHelper.callSparePairLoopAssignInquiryLfacs(jsonRequestString, mockSession))
//				.thenReturn(mockLfacsResponse);
//		when(mockObjectMapper.readValue(mockSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
//				.thenReturn(currentAssignmentResponseDto);
//		when(mockIVRLfacsServiceHelper.cleanResponseString(mockLfacsResponse)).thenReturn(null);
		when(mockObjectMapper.readValue(mockLfacsResponse, RetrieveLoopAssignmentResponse.class)).thenReturn(null);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND060(sessionId, currentState);

		assertEquals(null, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFND060CodeRetLoopAssign_0() throws JsonMappingException, JsonProcessingException,
			InterruptedException, ExecutionException, HttpTimeoutException {

		String sessionId = "session123";
		String currentState = "STATE_FND060";

		so.setORD("True");
		soList.add(so);
		loop.setCKID("763 757-4229");
		loop.setSO(soList);
		loop.setTID("DPAAB");
		loopList.add(loop);
		returnDataSet.setLoop(loopList);
		returnDataSet.setPort1("test");
		currentAssignmentResponseDto.setReturnDataSet(returnDataSet);

		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId(sessionId);
		mockSession.setCurrentAssignmentResponse("xyz");
		List<String> userInputDTMFList = new ArrayList<>();
		userInputDTMFList.add("1");

		TNInfoResponse mockTnInfo = new TNInfoResponse();
		mockTnInfo.setTn("1234567890");
		mockTnInfo.setPrimaryNPA("123");
		mockTnInfo.setPrimaryNXX("456");
		String jsonRequestString = "mock-request-string";
		String mockLfacsResponse = "mock-lfacs-response";
		String mockLosDbJsonString = "mock-losdb-response-string";
		RetrieveLoopAssignmentRequest mockRequest = new RetrieveLoopAssignmentRequest();
		RetrieveLoopAssignmentResponse mockResponse = new RetrieveLoopAssignmentResponse();
		mockSession.setLosDbResponse(mockLosDbJsonString);
		mockSession.setRtrvLoopAssgMsgName(mockLosDbJsonString);

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		response.setHookReturnCode("0");

		messageStatus.setErrorCode("L150-435");
		messageStatus.setErrorStatus("F");

		errorList.setErrorCode("I");
		errorList.setErrorMessage("L150-435");
		errorLists.add(errorList);

		hostErrorList.setId("LFACS");
		hostErrorList.setErrorList(errorLists);
		HostErrorLists.add(hostErrorList);

		messageStatus.setHostErrorList(HostErrorLists);

		mockResponse.setMessageStatus(messageStatus);

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(mockSession);

		when(mockObjectMapper.readValue(mockSession.getRtrvLoopAssgMsgName(), RetrieveLoopAssignmentResponse.class))
				.thenReturn(mockResponse);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND060(sessionId, currentState);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_0, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFND060CodeRetLoopAssign_5() throws JsonMappingException, JsonProcessingException,
			InterruptedException, ExecutionException, HttpTimeoutException {

		String sessionId = "session123";
		String currentState = "STATE_FND060";

		so.setORD("True");
		soList.add(so);
		loop.setCKID("763 757-4229");
		loop.setSO(soList);
		loop.setTID("DPAAB");
		loopList.add(loop);
		returnDataSet.setLoop(loopList);
		returnDataSet.setPort1("test");
		currentAssignmentResponseDto.setReturnDataSet(returnDataSet);

		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId(sessionId);
		mockSession.setCurrentAssignmentResponse("xyz");
		List<String> userInputDTMFList = new ArrayList<>();
		userInputDTMFList.add("1");

		TNInfoResponse mockTnInfo = new TNInfoResponse();
		mockTnInfo.setTn("1234567890");
		mockTnInfo.setPrimaryNPA("123");
		mockTnInfo.setPrimaryNXX("456");
		String jsonRequestString = "mock-request-string";
		String mockLfacsResponse = "mock-lfacs-response";
		String mockLosDbJsonString = "mock-losdb-response-string";
		RetrieveLoopAssignmentRequest mockRequest = new RetrieveLoopAssignmentRequest();
		RetrieveLoopAssignmentResponse mockResponse = new RetrieveLoopAssignmentResponse();
		mockSession.setLosDbResponse(mockLosDbJsonString);
		mockSession.setRtrvLoopAssgMsgName(mockLosDbJsonString);

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		response.setHookReturnCode("5");

		messageStatus.setErrorCode("L150-002");
		messageStatus.setErrorStatus("F");

		errorList.setErrorCode("I");
		errorList.setErrorMessage("L150-002");
		errorLists.add(errorList);

		hostErrorList.setId("LFACS");
		hostErrorList.setErrorList(errorLists);
		HostErrorLists.add(hostErrorList);

		messageStatus.setHostErrorList(HostErrorLists);

		mockResponse.setMessageStatus(messageStatus);

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(mockSession);

		when(mockObjectMapper.readValue(mockSession.getRtrvLoopAssgMsgName(), RetrieveLoopAssignmentResponse.class))
				.thenReturn(mockResponse);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND060(sessionId, currentState);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_5, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFND060CodeRetLoopAssign_7() throws JsonMappingException, JsonProcessingException,
			InterruptedException, ExecutionException, HttpTimeoutException {

		String sessionId = "session123";
		String currentState = "STATE_FND060";

		so.setORD("True");
		soList.add(so);
		loop.setCKID("763 757-4229");
		loop.setSO(soList);
		loop.setTID("DPAAB");
		loopList.add(loop);
		returnDataSet.setLoop(loopList);
		returnDataSet.setPort1("test");
		currentAssignmentResponseDto.setReturnDataSet(returnDataSet);

		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId(sessionId);
		mockSession.setCurrentAssignmentResponse("xyz");
		List<String> userInputDTMFList = new ArrayList<>();
		userInputDTMFList.add("1");

		TNInfoResponse mockTnInfo = new TNInfoResponse();
		mockTnInfo.setTn("1234567890");
		mockTnInfo.setPrimaryNPA("123");
		mockTnInfo.setPrimaryNXX("456");
		String jsonRequestString = "mock-request-string";
		String mockLfacsResponse = "mock-lfacs-response";
		String mockLosDbJsonString = "mock-losdb-response-string";
		RetrieveLoopAssignmentRequest mockRequest = new RetrieveLoopAssignmentRequest();
		RetrieveLoopAssignmentResponse mockResponse = new RetrieveLoopAssignmentResponse();
		mockSession.setLosDbResponse(mockLosDbJsonString);
		mockSession.setRtrvLoopAssgMsgName(mockLfacsResponse);

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		response.setHookReturnCode("7");

		messageStatus.setErrorCode("L150-451");
		messageStatus.setErrorStatus("F");

		errorList.setErrorCode("I");
		errorList.setErrorMessage("L150-451");
		errorLists.add(errorList);

		hostErrorList.setId("LFACS");
		hostErrorList.setErrorList(errorLists);
		HostErrorLists.add(hostErrorList);

		messageStatus.setHostErrorList(HostErrorLists);

		mockResponse.setMessageStatus(messageStatus);

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(mockSession);
//		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(mockLosDbJsonString)).thenReturn(mockTnInfo);
//		when(mockIVRLfacsServiceHelper.buildRetriveLoopAssignInqRequest(anyString(),anyString(),anyString(),any(),any(),anyInt())).thenReturn(mockRequest);
//		when(mockObjectMapper.writeValueAsString(mockRequest)).thenReturn(jsonRequestString);
//		when(mockIVRLfacsServiceHelper.callSparePairLoopAssignInquiryLfacs(jsonRequestString, mockSession))
//				.thenReturn(mockLfacsResponse);
//		when(mockObjectMapper.readValue(mockSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
//				.thenReturn(currentAssignmentResponseDto);
//		when(mockIVRLfacsServiceHelper.cleanResponseString(mockLfacsResponse)).thenReturn(mockLfacsResponse);
//		when(mockObjectMapper.readValue(mockLfacsResponse, RetrieveLoopAssignmentResponse.class))
//				.thenReturn(mockResponse);

		when(mockObjectMapper.readValue(mockSession.getRtrvLoopAssgMsgName(), RetrieveLoopAssignmentResponse.class))
				.thenReturn(mockResponse);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND060(sessionId, currentState);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_7, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFND060CodeRetLoopAssign_6() throws JsonMappingException, JsonProcessingException,
			InterruptedException, ExecutionException, HttpTimeoutException {

		String sessionId = "session123";
		String currentState = "STATE_FND060";

		so.setORD("True");
		soList.add(so);
		loop.setCKID("763 757-4229");
		loop.setSO(soList);
		loop.setTID("DPAAB");
		loopList.add(loop);
		returnDataSet.setLoop(loopList);
		returnDataSet.setPort1("test");
		currentAssignmentResponseDto.setReturnDataSet(returnDataSet);

		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId(sessionId);
		mockSession.setCurrentAssignmentResponse("xyz");
		List<String> userInputDTMFList = new ArrayList<>();
		userInputDTMFList.add("1");

		TNInfoResponse mockTnInfo = new TNInfoResponse();
		mockTnInfo.setTn("1234567890");
		mockTnInfo.setPrimaryNPA("123");
		mockTnInfo.setPrimaryNXX("456");
		String jsonRequestString = "mock-request-string";
		String mockLfacsResponse = "mock-lfacs-response";
		String mockLosDbJsonString = "mock-losdb-response-string";
		RetrieveLoopAssignmentRequest mockRequest = new RetrieveLoopAssignmentRequest();
		RetrieveLoopAssignmentResponse mockResponse = new RetrieveLoopAssignmentResponse();
		mockSession.setLosDbResponse(mockLosDbJsonString);
		mockSession.setRtrvLoopAssgMsgName(mockLfacsResponse);

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		response.setHookReturnCode("6");

		messageStatus.setErrorCode("L150-460");
		messageStatus.setErrorStatus("F");

		errorList.setErrorCode("I");
		errorList.setErrorMessage("L150-460");
		errorLists.add(errorList);

		hostErrorList.setId("LFACS");
		hostErrorList.setErrorList(errorLists);
		HostErrorLists.add(hostErrorList);

		messageStatus.setHostErrorList(HostErrorLists);

		mockResponse.setMessageStatus(messageStatus);

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(mockSession);

		when(mockObjectMapper.readValue(mockSession.getRtrvLoopAssgMsgName(), RetrieveLoopAssignmentResponse.class))
				.thenReturn(mockResponse);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND060(sessionId, currentState);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_6, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFND060CodeRetLoopAssign_1() throws JsonMappingException, JsonProcessingException,
			InterruptedException, ExecutionException, HttpTimeoutException {

		String sessionId = "session123";
		String currentState = "STATE_FND060";

		so.setORD("True");
		soList.add(so);
		loop.setCKID("763 757-4229");
		loop.setSO(soList);
		loop.setTID("DPAAB");
		loopList.add(loop);
		returnDataSet.setLoop(loopList);
		returnDataSet.setPort1("test");
		currentAssignmentResponseDto.setReturnDataSet(returnDataSet);

		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId(sessionId);
		mockSession.setCurrentAssignmentResponse("xyz");
		List<String> userInputDTMFList = new ArrayList<>();
		userInputDTMFList.add("1");

		TNInfoResponse mockTnInfo = new TNInfoResponse();
		mockTnInfo.setTn("1234567890");
		mockTnInfo.setPrimaryNPA("123");
		mockTnInfo.setPrimaryNXX("456");
		String jsonRequestString = "mock-request-string";
		String mockLfacsResponse = "mock-lfacs-response";
		String mockLosDbJsonString = "mock-losdb-response-string";
		RetrieveLoopAssignmentRequest mockRequest = new RetrieveLoopAssignmentRequest();
		RetrieveLoopAssignmentResponse mockResponse = new RetrieveLoopAssignmentResponse();
		mockSession.setLosDbResponse(mockLosDbJsonString);
		mockSession.setRtrvLoopAssgMsgName(mockLosDbJsonString);

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		response.setHookReturnCode("1");

		messageStatus.setErrorCode("500");
		messageStatus.setErrorStatus("F");

		errorList.setErrorCode("I");
		errorList.setErrorMessage("500");
		errorLists.add(errorList);

		hostErrorList.setId("LFACS");
		hostErrorList.setErrorList(errorLists);
		HostErrorLists.add(hostErrorList);

		messageStatus.setHostErrorList(HostErrorLists);

		mockResponse.setMessageStatus(messageStatus);

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(mockSession);

		when(mockIVRLfacsServiceHelper.findFastivrError(sessionId, currentState, messageStatus)).thenReturn(response);

		when(mockObjectMapper.readValue(mockSession.getRtrvLoopAssgMsgName(), RetrieveLoopAssignmentResponse.class))
				.thenReturn(mockResponse);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND060(sessionId, currentState);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_1, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFND060CodeRetLoopAssign_2() throws JsonMappingException, JsonProcessingException,
			InterruptedException, ExecutionException, HttpTimeoutException {

		String sessionId = "session123";
		String currentState = "STATE_FND060";

		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId(sessionId);
		mockSession.setCurrentAssignmentResponse("xyz");

		TNInfoResponse mockTnInfo = new TNInfoResponse();
		mockTnInfo.setTn("1234567890");
		mockTnInfo.setPrimaryNPA("123");
		mockTnInfo.setPrimaryNXX("456");
		String jsonRequestString = "mock-request-string";
		String mockLfacsResponse = "mock-lfacs-response";
		String mockLosDbJsonString = "mock-losdb-response-string";
		RetrieveLoopAssignmentRequest mockRequest = new RetrieveLoopAssignmentRequest();
		RetrieveLoopAssignmentResponse mockResponse = new RetrieveLoopAssignmentResponse();
		mockSession.setLosDbResponse(mockLosDbJsonString);
		mockSession.setRtrvLoopAssgMsgName(mockLfacsResponse);

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		response.setHookReturnCode("2");

		messageStatus.setErrorCode("504");
		messageStatus.setErrorStatus("F");

		errorList.setErrorCode("I");
		errorList.setErrorMessage("504");
		errorLists.add(errorList);

		hostErrorList.setId("LFACS");
		hostErrorList.setErrorList(errorLists);
		HostErrorLists.add(hostErrorList);

		messageStatus.setHostErrorList(HostErrorLists);

		mockResponse.setMessageStatus(messageStatus);

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(mockSession);

		when(mockIVRLfacsServiceHelper.findFastivrError(sessionId, currentState, messageStatus)).thenReturn(response);

		when(mockObjectMapper.readValue(mockSession.getRtrvLoopAssgMsgName(), RetrieveLoopAssignmentResponse.class))
				.thenReturn(mockResponse);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND060(sessionId, currentState);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_2, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFND060CodeRetLoopAssign_3() throws JsonMappingException, JsonProcessingException,
			InterruptedException, ExecutionException, HttpTimeoutException {

		String sessionId = "session123";
		String currentState = "STATE_FND060";

		so.setORD("True");
		soList.add(so);
		loop.setCKID("763 757-4229");
		loop.setSO(soList);
		loop.setTID("DPAAB");
		loopList.add(loop);
		returnDataSet.setLoop(loopList);
		returnDataSet.setPort1("test");
		currentAssignmentResponseDto.setReturnDataSet(returnDataSet);

		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId(sessionId);
		mockSession.setCurrentAssignmentResponse("xyz");
		List<String> userInputDTMFList = new ArrayList<>();
		userInputDTMFList.add("1");

		TNInfoResponse mockTnInfo = new TNInfoResponse();
		mockTnInfo.setTn("1234567890");
		mockTnInfo.setPrimaryNPA("123");
		mockTnInfo.setPrimaryNXX("456");
		String jsonRequestString = "mock-request-string";
		String mockLfacsResponse = "mock-lfacs-response";
		String mockLosDbJsonString = "mock-losdb-response-string";
		RetrieveLoopAssignmentRequest mockRequest = new RetrieveLoopAssignmentRequest();
		RetrieveLoopAssignmentResponse mockResponse = new RetrieveLoopAssignmentResponse();
		mockSession.setLosDbResponse(mockLosDbJsonString);
		mockSession.setRtrvLoopAssgMsgName(mockLosDbJsonString);

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		response.setHookReturnCode("3");

		messageStatus.setErrorCode("503");
		messageStatus.setErrorStatus("F");

		errorList.setErrorCode("I");
		errorList.setErrorMessage("503");
		errorLists.add(errorList);

		hostErrorList.setId("LFACS");
		hostErrorList.setErrorList(errorLists);
		HostErrorLists.add(hostErrorList);

		messageStatus.setHostErrorList(HostErrorLists);

		mockResponse.setMessageStatus(messageStatus);

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(mockSession);

		when(mockIVRLfacsServiceHelper.findFastivrError(sessionId, currentState, messageStatus)).thenReturn(response);

		when(mockObjectMapper.readValue(mockSession.getRtrvLoopAssgMsgName(), RetrieveLoopAssignmentResponse.class))
				.thenReturn(mockResponse);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND060(sessionId, currentState);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_3, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFND060CodeRetLoopAssign_4() throws JsonMappingException, JsonProcessingException,
			InterruptedException, ExecutionException, HttpTimeoutException {

		String sessionId = "session123";
		String currentState = "STATE_FND060";

		so.setORD("True");
		soList.add(so);
		loop.setCKID("763 757-4229");
		loop.setSO(soList);
		loop.setTID("DPAAB");
		loopList.add(loop);
		returnDataSet.setLoop(loopList);
		returnDataSet.setPort1("test");
		currentAssignmentResponseDto.setReturnDataSet(returnDataSet);

		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId(sessionId);
		mockSession.setCurrentAssignmentResponse("xyz");
		List<String> userInputDTMFList = new ArrayList<>();
		userInputDTMFList.add("1");

		TNInfoResponse mockTnInfo = new TNInfoResponse();
		mockTnInfo.setTn("1234567890");
		mockTnInfo.setPrimaryNPA("123");
		mockTnInfo.setPrimaryNXX("456");
		String jsonRequestString = "mock-request-string";
		String mockLfacsResponse = "mock-lfacs-response";
		String mockLosDbJsonString = "mock-losdb-response-string";
		RetrieveLoopAssignmentRequest mockRequest = new RetrieveLoopAssignmentRequest();
		RetrieveLoopAssignmentResponse mockResponse = new RetrieveLoopAssignmentResponse();
		mockSession.setLosDbResponse(mockLosDbJsonString);
		mockSession.setRtrvLoopAssgMsgName(mockLfacsResponse);

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		response.setHookReturnCode("4");

		messageStatus.setErrorCode("400");
		messageStatus.setErrorStatus("F");

		errorList.setErrorCode("I");
		errorList.setErrorMessage("400");
		errorLists.add(errorList);

		hostErrorList.setId("LFACS");
		hostErrorList.setErrorList(errorLists);
		HostErrorLists.add(hostErrorList);

		messageStatus.setHostErrorList(HostErrorLists);

		mockResponse.setMessageStatus(messageStatus);

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(mockSession);

		when(mockIVRLfacsServiceHelper.findFastivrError(sessionId, currentState, messageStatus)).thenReturn(response);

		when(mockObjectMapper.readValue(mockSession.getRtrvLoopAssgMsgName(), RetrieveLoopAssignmentResponse.class))
				.thenReturn(mockResponse);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND060(sessionId, currentState);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_4, actualResponse.getHookReturnCode());
	}

	@Test
	void processFND075_VALIDSESSION() {

		String sessionid = "session123";

		String userInput = "1234567";

		String currentState = STATE_FND075;

		IVRWebHookResponseDto mockResponse = new IVRWebHookResponseDto();

		mockResponse.setHookReturnMessage(VALID_TN_MSG);

		mockResponse.setHookReturnCode(HOOK_RETURN_1);

		IVRUserSession mockSession = new IVRUserSession();

		mockSession.setSessionId(sessionid);

		IVRCnfEntity mockCnfSession = new IVRCnfEntity();

		mockCnfSession.setSessionId(sessionid);

		when(mockIvrCnfCacheService.getBySessionId(sessionid)).thenReturn(mockCnfSession);

		when(mockCacheService.getBySessionId(sessionid)).thenReturn(mockSession);

		when(mockLfacsTNValidation.validateFacsTN(userInput, mockSession)).thenReturn(mockResponse);

		IVRWebHookResponseDto response = ivrCnfServiceImpl.processFND075(sessionid, currentState, userInput);

		assertEquals(HOOK_RETURN_1, response.getHookReturnCode());

		assertEquals(VALID_TN_MSG, response.getHookReturnMessage());
	}

	@Test
	void processFND075_CNF_INVALID_SESSION() {

		String sessionid = "session123";

		String userInput = "1234567";

		String currentState = STATE_FND075;

		IVRWebHookResponseDto mockResponse = new IVRWebHookResponseDto();

		mockResponse.setHookReturnMessage(VALID_TN_MSG);

		mockResponse.setHookReturnCode(HOOK_RETURN_1);

		IVRUserSession mockSession = new IVRUserSession();

		mockSession.setSessionId(sessionid);

		when(mockIvrCnfCacheService.getBySessionId(sessionid)).thenReturn(null);

		IVRCnfEntity mockCnfSession = new IVRCnfEntity();

		mockCnfSession.setSessionId(sessionid);

		when(mockIvrCnfCacheService.addSession(Mockito.any(IVRCnfEntity.class))).thenReturn(mockCnfSession);

		when(mockCacheService.getBySessionId(sessionid)).thenReturn(mockSession);

		when(mockLfacsTNValidation.validateFacsTN(userInput, mockSession)).thenReturn(mockResponse);

		IVRWebHookResponseDto response = ivrCnfServiceImpl.processFND075(sessionid, currentState, userInput);

		assertEquals(HOOK_RETURN_1, response.getHookReturnCode());

		assertEquals(VALID_TN_MSG, response.getHookReturnMessage());
	}

	@Test
	void processFND075_INVALIDSESSION() {

		String sessionid = "session123";

		String userInput = "1234567";

		String currentState = STATE_FND075;

		IVRWebHookResponseDto mockResponse = new IVRWebHookResponseDto();

		mockResponse.setHookReturnMessage(INVALID_SESSION_ID);

		IVRUserSession mockSession = new IVRUserSession();

		mockSession.setSessionId(sessionid);

		when(mockCacheService.getBySessionId(sessionid)).thenReturn(null);

		IVRWebHookResponseDto response = ivrCnfServiceImpl.processFND075(sessionid, currentState, userInput);

		assertEquals(INVALID_SESSION_ID, response.getHookReturnMessage());
	}

	@Test
	void testProcessFID085Code_Success() throws JsonMappingException, JsonProcessingException, IllegalArgumentException,
			IllegalAccessException, InterruptedException, ExecutionException {

		String sessionId = "session123";

		String currentState = "FID085";

		IVRUserSession mockSession = new IVRUserSession();

		mockSession.setSessionId(sessionId);

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		response.setSessionId(sessionId);

		response.setCurrentState(currentState);

		response.setHookReturnCode("0");

		List<String> userInputDTMFList = new ArrayList<>();

		userInputDTMFList.add("1");

		when(mockIVRLfacsServiceImpl.processFID020Code(sessionId, currentState)).thenReturn(response);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND085(sessionId, currentState);

		assertEquals(HOOK_RETURN_0, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID090Code_HookCode1() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID090";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		response.setHookReturnCode("1");
		response.setHookReturnMessage(IVRLfacsConstants.SYSTEM_DOWN_ERR);

		IVRUserSession userSession = new IVRUserSession();
		messageStatus.setErrorCode("500");
		messageStatus.setErrorStatus("F");

		errorList.setErrorCode("I");
		errorList.setErrorMessage("500");
		errorLists.add(errorList);

		hostErrorList.setId("LFACS");
		hostErrorList.setErrorList(errorLists);
		HostErrorLists.add(hostErrorList);

		messageStatus.setHostErrorList(HostErrorLists);
		currentAssignmentResponseDto.setMessageStatus(messageStatus);
		userSession.setCurrentAssignmentResponse(currentAssignmentResponseDto.toString());

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockIVRLfacsServiceHelper.findFastivrError(sessionId, currentState, messageStatus)).thenReturn(response);
		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponseDto);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND090(sessionId, currentState);

		assertEquals(HOOK_RETURN_1, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID090Code_HookCode2() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID090";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		response.setHookReturnCode("2");

		IVRUserSession userSession = new IVRUserSession();
		messageStatus.setErrorCode("504");
		messageStatus.setErrorStatus("F");

		errorList.setErrorCode("I");
		errorList.setErrorMessage("504");
		errorLists.add(errorList);

		hostErrorList.setId("LFACS");
		hostErrorList.setErrorList(errorLists);
		HostErrorLists.add(hostErrorList);

		messageStatus.setHostErrorList(HostErrorLists);
		currentAssignmentResponseDto.setMessageStatus(messageStatus);
		userSession.setCurrentAssignmentResponse(currentAssignmentResponseDto.toString());

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockIVRLfacsServiceHelper.findFastivrError(sessionId, currentState, messageStatus)).thenReturn(response);
		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponseDto);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND090(sessionId, currentState);

		assertEquals(HOOK_RETURN_2, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID090Code_HookCode3() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID090";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		response.setHookReturnCode("3");

		IVRUserSession userSession = new IVRUserSession();
		messageStatus.setErrorCode("503");
		messageStatus.setErrorStatus("F");

		errorList.setErrorCode("I");
		errorList.setErrorMessage("503");
		errorLists.add(errorList);

		hostErrorList.setId("LFACS");
		hostErrorList.setErrorList(errorLists);
		HostErrorLists.add(hostErrorList);

		messageStatus.setHostErrorList(HostErrorLists);
		currentAssignmentResponseDto.setMessageStatus(messageStatus);
		userSession.setCurrentAssignmentResponse(currentAssignmentResponseDto.toString());

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockIVRLfacsServiceHelper.findFastivrError(sessionId, currentState, messageStatus)).thenReturn(response);
		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponseDto);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND090(sessionId, currentState);

		assertEquals(HOOK_RETURN_3, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID090Code_HookCode4() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID090";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		response.setHookReturnCode("4");

		IVRUserSession userSession = new IVRUserSession();
		messageStatus.setErrorCode("400");
		messageStatus.setErrorStatus("F");

		errorList.setErrorCode("I");
		errorList.setErrorMessage("400");
		errorLists.add(errorList);

		hostErrorList.setId("LFACS");
		hostErrorList.setErrorList(errorLists);
		HostErrorLists.add(hostErrorList);

		messageStatus.setHostErrorList(HostErrorLists);
		currentAssignmentResponseDto.setMessageStatus(messageStatus);
		userSession.setCurrentAssignmentResponse(currentAssignmentResponseDto.toString());

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockIVRLfacsServiceHelper.findFastivrError(sessionId, currentState, messageStatus)).thenReturn(response);
		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponseDto);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND090(sessionId, currentState);

		assertEquals(HOOK_RETURN_4, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID090Code_HookCode5() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID090";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		response.setHookReturnCode("5");

		IVRUserSession userSession = new IVRUserSession();
		messageStatus.setErrorStatus("S");

		currentAssignmentResponseDto.setMessageStatus(messageStatus);
		currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG().get(0).setCA("1");
		userSession.setCurrentAssignmentResponse(currentAssignmentResponseDto.toString());

		List<IVRParameter> parameterList = new ArrayList<IVRParameter>();

		IVRParameter parameter = new IVRParameter();

		parameter.setData("");
		parameterList.add(parameter);

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockIVRLfacsServiceHelper.getCablePair(Mockito.any(CurrentAssignmentResponseDto.class), anyInt()))
				.thenReturn(parameterList);
		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponseDto);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND090(sessionId, currentState);

		assertEquals(HOOK_RETURN_5, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID090Code_HookCode6() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID090";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		response.setHookReturnCode("6");

		IVRUserSession userSession = new IVRUserSession();
		messageStatus.setErrorStatus("S");

		currentAssignmentResponseDto.setMessageStatus(messageStatus);
		currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG().get(0).setCA("1");
		userSession.setCurrentAssignmentResponse(currentAssignmentResponseDto.toString());

		List<IVRParameter> parameterList = new ArrayList<IVRParameter>();

		IVRParameter parameterCA = new IVRParameter();

		parameterCA.setData("1");

		IVRParameter parameterPR = new IVRParameter();

		parameterPR.setData("");
		parameterList.add(parameterCA);
		parameterList.add(parameterPR);

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockIVRLfacsServiceHelper.getCablePair(Mockito.any(CurrentAssignmentResponseDto.class), anyInt()))
				.thenReturn(parameterList);
		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponseDto);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND090(sessionId, currentState);

		assertEquals(HOOK_RETURN_6, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID090Code_HookCode7() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID090";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		response.setHookReturnCode("7");

		IVRUserSession userSession = new IVRUserSession();
		messageStatus.setErrorStatus("S");

		currentAssignmentResponseDto.setMessageStatus(messageStatus);
		currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG().get(0).setCA("1");
		List<SO> soList = new ArrayList<SO>();
		SO so = new SO();
		so.setORD("Test");
		soList.add(so);
		currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).setSO(soList);
		userSession.setCurrentAssignmentResponse(currentAssignmentResponseDto.toString());

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockIVRLfacsServiceHelper.isSeviceOrder(Mockito.any(CurrentAssignmentResponseDto.class))).thenReturn(true);
		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponseDto);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND090(sessionId, currentState);

		assertEquals(HOOK_RETURN_7, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID090Code_HookCode8() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID090";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		response.setHookReturnCode("8");

		IVRUserSession userSession = new IVRUserSession();
		messageStatus.setErrorStatus("S");

		currentAssignmentResponseDto.setMessageStatus(messageStatus);
		userSession.setCurrentAssignmentResponse(currentAssignmentResponseDto.toString());

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponseDto);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND090(sessionId, currentState);

		assertEquals(HOOK_RETURN_8, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID090Code_HookCode0() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID090";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		response.setHookReturnCode("0");
		IVRUserSession userSession = new IVRUserSession();
		messageStatus.setErrorCode("L400-192");
		messageStatus.setErrorStatus("F");

		errorList.setErrorCode("I");
		errorList.setErrorMessage("L400-192");
		errorLists.add(errorList);

		hostErrorList.setId("LFACS");
		hostErrorList.setErrorList(errorLists);
		HostErrorLists.add(hostErrorList);

		messageStatus.setHostErrorList(HostErrorLists);
		currentAssignmentResponseDto.setMessageStatus(messageStatus);
		userSession.setCurrentAssignmentResponse("xyz");

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponseDto);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND090(sessionId, currentState);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_0, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID135Code_HookCode0() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID135";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		response.setHookReturnCode("0");

		IVRUserSession userSession = new IVRUserSession();

		currentAssignmentResponseDto.setMessageStatus(messageStatus);
		currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG().get(0).setCA("1");
		currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG().get(0).setPR("10");
		userSession.setCurrentAssignmentResponse(currentAssignmentResponseDto.toString());

		List<IVRParameter> parameterList = new ArrayList<IVRParameter>();

		IVRParameter parameterCA = new IVRParameter();

		parameterCA.setData("1");

		IVRParameter parameterPR = new IVRParameter();

		parameterPR.setData("");
		parameterList.add(parameterCA);
		parameterList.add(parameterPR);

		IVRCnfEntity mockCnfSession = new IVRCnfEntity();

		mockCnfSession.setSessionId(sessionId);

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockIvrCnfCacheService.getBySessionId(sessionId)).thenReturn(mockCnfSession);
		when(mockIVRLfacsServiceHelper.getCablePair(Mockito.any(CurrentAssignmentResponseDto.class), anyInt()))
				.thenReturn(parameterList);
		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponseDto);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND135(sessionId, currentState);

		assertEquals(HOOK_RETURN_0, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID135Code_HookCode() throws JsonMappingException, JsonProcessingException {

		String sessionId = "";
		String currentState = "FID135";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		response.setHookReturnCode("-1");

		IVRUserSession userSession = new IVRUserSession();

		userSession.setCurrentAssignmentResponse(null);

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND135(sessionId, currentState);

		assertEquals(HOOK_RETURN, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID135Code_HookCode0_WithCNFSession() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID135";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		response.setHookReturnCode("0");

		IVRUserSession userSession = new IVRUserSession();

		currentAssignmentResponseDto.setMessageStatus(messageStatus);
		currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG().get(0).setCA("1");
		currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG().get(0).setPR("10");
		userSession.setCurrentAssignmentResponse(currentAssignmentResponseDto.toString());

		List<IVRParameter> parameterList = new ArrayList<IVRParameter>();

		IVRParameter parameterCA = new IVRParameter();

		parameterCA.setData("1");

		IVRParameter parameterPR = new IVRParameter();

		parameterPR.setData("");
		parameterList.add(parameterCA);
		parameterList.add(parameterPR);

		IVRCnfEntity mockCnfSession = new IVRCnfEntity();

		mockCnfSession.setSessionId(sessionId);

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockIvrCnfCacheService.getBySessionId(sessionId)).thenReturn(mockCnfSession);
		when(mockIVRLfacsServiceHelper.getCablePair(Mockito.any(CurrentAssignmentResponseDto.class), anyInt()))
				.thenReturn(parameterList);
		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponseDto);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND135(sessionId, currentState);

		assertEquals(HOOK_RETURN_0, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID135Code_HookCode0_WithoutCurrentAssg() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID135";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		IVRUserSession userSession = new IVRUserSession();

		currentAssignmentResponseDto.setMessageStatus(messageStatus);
		currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG().get(0).setCA("1");
		currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG().get(0).setPR("10");
		userSession.setCurrentAssignmentResponse(currentAssignmentResponseDto.toString());

		List<IVRParameter> parameterList = new ArrayList<IVRParameter>();

		IVRParameter parameterCA = new IVRParameter();

		parameterCA.setData("1");

		IVRParameter parameterPR = new IVRParameter();

		parameterPR.setData("");
		parameterList.add(parameterCA);
		parameterList.add(parameterPR);

		IVRCnfEntity mockCnfSession = new IVRCnfEntity();

		mockCnfSession.setSessionId(sessionId);

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);

		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(null);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND135(sessionId, currentState);

		assertEquals(HOOK_RETURN, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFND145Code_HookCode() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID145";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(null);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND145(sessionId, currentState, "8");

		assertEquals(HOOK_RETURN, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFND145Code_HookCode5() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID145";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		IVRUserSession userSession = new IVRUserSession();
		
		IVRCnfEntity cnfUserSession = new IVRCnfEntity();
		
		cnfUserSession.setSessionId(sessionId);

		currentAssignmentResponseDto.setMessageStatus(messageStatus);
		currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG().get(0).setCA("1");
		currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG().get(0).setPR("10");
		userSession.setCurrentAssignmentResponse(currentAssignmentResponseDto.toString());

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockIvrCnfCacheService.getBySessionId(sessionId)).thenReturn(cnfUserSession);
		when(mockIvrCnfHelper.isSwitchAvailable(Mockito.any(IVRUserSession.class))).thenReturn(true);
		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponseDto);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND145(sessionId, currentState, "8");

		assertEquals(HOOK_RETURN_5, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID145Code_HookCode2() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID145";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		IVRUserSession userSession = new IVRUserSession();
		
		IVRCnfEntity cnfUserSession = new IVRCnfEntity();
		
		cnfUserSession.setSessionId(sessionId);

		currentAssignmentResponseDto.setMessageStatus(messageStatus);
		currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG().get(0).setCA("1");
		currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG().get(0).setPR("10");
		userSession.setCurrentAssignmentResponse(currentAssignmentResponseDto.toString());

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponseDto);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND145(sessionId, currentState, "1");

		assertEquals(HOOK_RETURN_2, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID145Code_HookCode0() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID145";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		IVRUserSession userSession = new IVRUserSession();
		
		IVRCnfEntity cnfUserSession = new IVRCnfEntity();
		
		cnfUserSession.setSessionId(sessionId);

		currentAssignmentResponseDto.setMessageStatus(messageStatus);
		currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG().get(0).setCA("1");
		currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG().get(0).setPR("10");
		userSession.setCurrentAssignmentResponse(currentAssignmentResponseDto.toString());
		userSession.setCanBePagedMobile(true);

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockIvrCnfCacheService.getBySessionId(sessionId)).thenReturn(cnfUserSession);
		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponseDto);
		when(mockIvrLfacespagerTextFormation.getPageCurrentAssignment(Mockito.any(IVRWebHookResponseDto.class),
				Mockito.any(IVRUserSession.class), Mockito.any(CurrentAssignmentResponseDto.class)))
				.thenReturn(IVRConstants.SUCCESS);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND145(sessionId, currentState, "8");

		assertEquals(HOOK_RETURN_0, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID145Code_HookCode1() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID145";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		IVRUserSession userSession = new IVRUserSession();
		
		IVRCnfEntity cnfUserSession = new IVRCnfEntity();
		
		cnfUserSession.setSessionId(sessionId);

		currentAssignmentResponseDto.setMessageStatus(messageStatus);
		currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG().get(0).setCA("1");
		currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG().get(0).setPR("10");
		userSession.setCurrentAssignmentResponse(currentAssignmentResponseDto.toString());
		userSession.setCanBePagedEmail(true);

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockIvrCnfCacheService.getBySessionId(sessionId)).thenReturn(cnfUserSession);
		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponseDto);

		when(mockIvrLfacespagerTextFormation.getPageCurrentAssignment(Mockito.any(IVRWebHookResponseDto.class),
				Mockito.any(IVRUserSession.class), Mockito.any(CurrentAssignmentResponseDto.class)))
				.thenReturn(IVRConstants.SUCCESS);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND145(sessionId, currentState, "8");

		assertEquals(HOOK_RETURN_1, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID145Code_HookCode3() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID145";

		List<SEG> segList = currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG();
		SEG seg = new SEG();
		seg.setCA("2");
		seg.setPR("3");
		segList.add(seg);

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		IVRUserSession userSession = new IVRUserSession();

		List<IVRParameter> parameterList = new ArrayList<IVRParameter>();

		IVRParameter parameterCA = new IVRParameter();

		parameterCA.setData("1");

		IVRParameter parameterPR = new IVRParameter();

		parameterPR.setData("2");
		parameterList.add(parameterCA);
		parameterList.add(parameterPR);

		currentAssignmentResponseDto.setMessageStatus(messageStatus);
		currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG().get(0).setCA("1");
		currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG().get(0).setPR("10");
		userSession.setCurrentAssignmentResponse(currentAssignmentResponseDto.toString());

		when(mockIVRLfacsServiceHelper.getCablePair(Mockito.any(CurrentAssignmentResponseDto.class), anyInt()))
				.thenReturn(parameterList);

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponseDto);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND145(sessionId, currentState, "1");

		assertEquals(HOOK_RETURN_3, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID155Code_HookCode2() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID155";

		IVRCnfEntity mockCnfSession = new IVRCnfEntity();

		mockCnfSession.setSessionId(sessionId);

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		when(mockIvrCnfCacheService.getBySessionId(sessionId)).thenReturn(mockCnfSession);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND155(sessionId, currentState, "1");

		assertEquals(HOOK_RETURN_2, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID155Code_HookCode_WithoutCNFSession() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID155";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		when(mockIvrCnfCacheService.getBySessionId(sessionId)).thenReturn(null);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND155(sessionId, currentState, "1");

		assertEquals(HOOK_RETURN, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID155Code_HookCode() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID155";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(null);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND155(sessionId, currentState, "2");

		assertEquals(HOOK_RETURN, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID155Code_HookCode0() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID155";

		List<SEG> segList = currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG();
		SEG seg = new SEG();
		seg.setCA("2");
		seg.setPR("3");

		SEG seg1 = new SEG();
		seg1.setCA("2");
		seg1.setPR("3");
		segList.add(seg1);

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		IVRUserSession userSession = new IVRUserSession();

		IVRCnfEntity mockCnfSession = new IVRCnfEntity();

		mockCnfSession.setSessionId(sessionId);

		currentAssignmentResponseDto.setMessageStatus(messageStatus);
		currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG().get(0).setCA("1");
		currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG().get(0).setPR("10");
		userSession.setCurrentAssignmentResponse(currentAssignmentResponseDto.toString());

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponseDto);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND155(sessionId, currentState, "2");

		assertEquals(HOOK_RETURN_0, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID155Code_HookCode1() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID155";

		List<SEG> segList = currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG();
		SEG seg = new SEG();
		seg.setCA("2");
		seg.setPR("3");

		SEG seg1 = new SEG();
		seg1.setCA("2");
		seg1.setPR("3");
		segList.add(seg1);

		SEG seg2 = new SEG();
		seg2.setCA("2");
		seg2.setPR("3");
		segList.add(seg2);

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		IVRUserSession userSession = new IVRUserSession();

		IVRCnfEntity mockCnfSession = new IVRCnfEntity();

		mockCnfSession.setSessionId(sessionId);

		List<IVRParameter> parameterList = new ArrayList<IVRParameter>();

		IVRParameter parameterCA = new IVRParameter();

		parameterCA.setData("1");

		IVRParameter parameterPR = new IVRParameter();

		parameterPR.setData("2");
		parameterList.add(parameterCA);
		parameterList.add(parameterPR);

		currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG().get(0).setCA("1");
		currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG().get(0).setPR("10");
		userSession.setCurrentAssignmentResponse(currentAssignmentResponseDto.toString());

		when(mockIVRLfacsServiceHelper.getCablePair(Mockito.any(CurrentAssignmentResponseDto.class), anyInt()))
				.thenReturn(parameterList);

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponseDto);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND155(sessionId, currentState, "2");

		assertEquals(HOOK_RETURN_1, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID170Code_HookCode0() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID170";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND170(sessionId, currentState, "1");

		assertEquals(HOOK_RETURN_0, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID170Code_HookCode_F3() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID170";

		IVRCnfEntity mockCnfSession = new IVRCnfEntity();

		mockCnfSession.setSessionId(sessionId);

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		when(mockIvrCnfCacheService.getBySessionId(sessionId)).thenReturn(mockCnfSession);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND170(sessionId, currentState, "1");

		assertEquals(HOOK_RETURN_0, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID700Code_HookCode_3() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID700";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		when(mockIvrCnfHelper.convertInputCodesToAlphabets("*2132*12")).thenReturn("AT");

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND700(sessionId, currentState, "*2132*12");

		assertEquals(HOOK_RETURN_3, actualResponse.getHookReturnCode());
	}

//	@Test
//	void testProcessFID700Code_HookCode_2() throws JsonMappingException, JsonProcessingException {
//
//		String sessionId = "session123";
//		String currentState = "FID700";
//
//		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
//		response.setSessionId(sessionId);
//		response.setCurrentState(currentState);
//
//		when(mockIvrCnfHelper.convertInputCodesToAlphabets("*2132*")).thenReturn(null);
//
//		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND700(sessionId, currentState, "*2132*");
//
//		assertEquals(HOOK_RETURN_2, actualResponse.getHookReturnCode());
//	}

	@Test
	void testProcessFID700Code_HookCode_3_WithNumeric() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID700";
		
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND700(sessionId, currentState, "213212");

		assertEquals(HOOK_RETURN_3, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID740Code_HookCode_0() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID740";

		List<String> userInputDTMFList = new ArrayList<String>();

		userInputDTMFList.add("1");
		userInputDTMFList.add("2");

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		IVRUserSession userSession = new IVRUserSession();

		userSession.setSessionId(sessionId);

		IVRCnfEntity mockCnfSession = new IVRCnfEntity();

		mockCnfSession.setSessionId(sessionId);
		mockCnfSession.setCable("1");
		mockCnfSession.setPair("2");

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockIvrCnfCacheService.getBySessionId(sessionId)).thenReturn(mockCnfSession);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND740(sessionId, currentState,
				userInputDTMFList);

		assertEquals(HOOK_RETURN_0, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID740Code_HookCode_1() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID740";

		List<String> userInputDTMFList = new ArrayList<String>();

		userInputDTMFList.add("1");
		userInputDTMFList.add("2");

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		IVRUserSession userSession = new IVRUserSession();

		userSession.setSessionId(sessionId);

		IVRCnfEntity mockCnfSession = new IVRCnfEntity();

		mockCnfSession.setSessionId(sessionId);
		mockCnfSession.setCable("1");
		mockCnfSession.setPair("3");
		mockCnfSession.setCnfInqType(INQUIRY_BY_SERVICE_ORDER);

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockIvrCnfCacheService.getBySessionId(sessionId)).thenReturn(mockCnfSession);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND740(sessionId, currentState,
				userInputDTMFList);

		assertEquals(HOOK_RETURN_1, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID740Code_HookCode_2() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID740";

		List<String> userInputDTMFList = new ArrayList<String>();

		userInputDTMFList.add("1");
		userInputDTMFList.add("2");

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		IVRUserSession userSession = new IVRUserSession();

		userSession.setSessionId(sessionId);

		IVRCnfEntity mockCnfSession = new IVRCnfEntity();

		mockCnfSession.setSessionId(sessionId);
		mockCnfSession.setCable("1");
		mockCnfSession.setPair("3");
		mockCnfSession.setCnfInqType(INQUIRY_BY_TROUBLE_TICKET);

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockIvrCnfCacheService.getBySessionId(sessionId)).thenReturn(mockCnfSession);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND740(sessionId, currentState,
				userInputDTMFList);

		assertEquals(HOOK_RETURN_2, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID740Code_HookCode_WithoutCNFSession() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID740";

		List<String> userInputDTMFList = new ArrayList<String>();

		userInputDTMFList.add("1");
		userInputDTMFList.add("2");

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		IVRUserSession userSession = new IVRUserSession();

		userSession.setSessionId(sessionId);

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockIvrCnfCacheService.getBySessionId(sessionId)).thenReturn(null);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND740(sessionId, currentState,
				userInputDTMFList);

		assertEquals(HOOK_RETURN, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFND740Code_HookCode_WithoutSession() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FND740";

		List<String> userInputDTMFList = new ArrayList<String>();

		userInputDTMFList.add("1");
		userInputDTMFList.add("2");

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(null);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND740(sessionId, currentState,
				userInputDTMFList);

		assertEquals(HOOK_RETURN, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFND215Code_FN0190() {

		String sessionId = "session123";
		String currentState = "FN0190";
		String nextState = "FND215";

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND215(sessionId, currentState, nextState, "1");

		assertEquals(HOOK_RETURN_1, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFND215Code_200() {

		String sessionId = "session123";
		String currentState = "FN0200";
		String nextState = "FND215";

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND215(sessionId, currentState, nextState, "1");

		assertEquals(HOOK_RETURN_1, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFND215Code_210() {

		String sessionId = "session123";
		String currentState = "FN0210";
		String nextState = "FND215";

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND215(sessionId, currentState, nextState, "1");

		assertEquals(HOOK_RETURN_1, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFND215Code_211() {

		String sessionId = "session123";
		String currentState = "FN0211";
		String nextState = "FND215";

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND215(sessionId, currentState, nextState, "1");

		assertEquals(HOOK_RETURN_1, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFND215Code_212() {

		String sessionId = "session123";
		String currentState = "FN0212";
		String nextState = "FND215";

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND215(sessionId, currentState, nextState, "1");

		assertEquals(HOOK_RETURN_1, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFND215Code_213() {

		String sessionId = "session123";
		String currentState = "FN0213";
		String nextState = "FND215";

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND215(sessionId, currentState, nextState, "1");

		assertEquals(HOOK_RETURN_1, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID741Code_HookCode_WithoutCNFSession() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID741";
		
		List<String> userInputs = new ArrayList<>();
		userInputs.add("12");
		userInputs.add("247");

		TNInfoResponse losDbResponse = new TNInfoResponse();
		losDbResponse.setTn("7637574229");
		losDbResponse.setPrimaryNPA("763");
		losDbResponse.setPrimaryNXX("757");

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		IVRUserSession userSession = new IVRUserSession();

		userSession.setSessionId(sessionId);

		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(userSession.getLosDbResponse()))
				.thenReturn(losDbResponse);

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockIvrCnfCacheService.getBySessionId(sessionId)).thenReturn(null);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND741(sessionId, currentState, userInputs);

		assertEquals(HOOK_RETURN, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFND741Code_HookCode_WithoutSession() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session321";
		String currentState = "FND741";

		List<String> userInputs = new ArrayList<>();
		userInputs.add("12");
		userInputs.add("247");
		
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(null);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND741(sessionId, currentState, userInputs);

		assertEquals(HOOK_RETURN, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFND741Code_HookCode_3() throws JsonMappingException, JsonProcessingException, HttpTimeoutException, InterruptedException, ExecutionException {

		// test when pager is TRUE and isSwitchAvailable is FALSE
		String sessionId = "session123";
		String currentState = "FID741";
		
		List<String> userInputs = new ArrayList<>();
		userInputs.add("12");
		userInputs.add("247");

		so.setORD("True");
		soList.add(so);
		loop.setCKID("763 757-4229");
		loop.setSO(soList);
		loop.setTID("DPAAB");
		loopList.add(loop);
		returnDataSet.setLoop(loopList);
		returnDataSet.setPort1("test");
		currentAssignmentResponseDto.setReturnDataSet(returnDataSet);

		TNInfoResponse losDbResponse = new TNInfoResponse();
		losDbResponse.setTn("7637574229");
		losDbResponse.setPrimaryNPA("763");
		losDbResponse.setPrimaryNXX("757");

		ChangeLoopAssignmentRequestDto mockRequest = new ChangeLoopAssignmentRequestDto();
		ChangeLoopAssignmentRequestInputData inputData = new ChangeLoopAssignmentRequestInputData();
		mockRequest.setInputData(inputData);

		ChangeLoopAssignmentResponseDto mockResponse = new ChangeLoopAssignmentResponseDto();
		MessageStatus messageStatus = new MessageStatus();
		messageStatus.setErrorStatus("S");
		mockResponse.setMessageStatus(messageStatus);

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		response.setHookReturnCode(HOOK_RETURN_3);

		IVRUserSession userSession = new IVRUserSession();
		userSession.setSessionId(sessionId);
		userSession.setEmpID("45634");
		userSession.setCanBePagedMobile(true);

		IVRCnfEntity mockCnfSession = new IVRCnfEntity();
		mockCnfSession.setSessionId(sessionId);
		mockCnfSession.setCable("1");
		mockCnfSession.setPair("3");
		mockCnfSession.setSegmentRead("F1");

		String jsonRequestString = "mock-request-string";
		String mockLJsonResponse = "mock-lfacs-response";

		// Mocking
		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(userSession.getLosDbResponse()))
				.thenReturn(losDbResponse);
		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockIvrCnfCacheService.getBySessionId(sessionId)).thenReturn(mockCnfSession);

		when(mockIvrCnfHelper.buildChangeLoopAssignmentRequest(anyString(), anyString(), anyString(), anyString(),
				anyString(), any(), any(), any(), any())).thenReturn(mockRequest);
		when(mockObjectMapper.writeValueAsString(mockRequest)).thenReturn(jsonRequestString);
		when(mockIvrHttpClient.httpPostCall(any(), any(), any(), anyString())).thenReturn(mockLJsonResponse);

		when(mockIVRLfacsServiceHelper.cleanResponseString(mockLJsonResponse)).thenReturn(mockLJsonResponse);
		when(mockObjectMapper.readValue(mockLJsonResponse, ChangeLoopAssignmentResponseDto.class))
				.thenReturn(mockResponse);
		when(ivrCnfPagerTextFormation.getPageChangeLoopAssignment(any(), any(), any(),
				any(), anyString(), anyString(), anyString(), anyString())).thenReturn(mockResponse);		
		when(mockIvrCnfHelper.processResponse(any(), anyString(), any())).thenReturn(response);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND741(sessionId, currentState, userInputs);

		assertEquals(HOOK_RETURN_3, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFND741Code_HookCode_6() throws JsonMappingException, JsonProcessingException, HttpTimeoutException, InterruptedException, ExecutionException {
		// test when pager is FALSE and isSwitchAvailable is FALSE
		String sessionId = "session123";
		String currentState = "FID741";

		List<String> userInputs = new ArrayList<>();
		userInputs.add("12");
		userInputs.add("247");
		
		so.setORD("True");
		soList.add(so);
		loop.setCKID("763 757-4229");
		loop.setSO(soList);
		loop.setTID("DPAAB");
		loopList.add(loop);
		returnDataSet.setLoop(loopList);
		returnDataSet.setPort1("test");
		currentAssignmentResponseDto.setReturnDataSet(returnDataSet);

		TNInfoResponse losDbResponse = new TNInfoResponse();
		losDbResponse.setTn("7637574229");
		losDbResponse.setPrimaryNPA("763");
		losDbResponse.setPrimaryNXX("757");

		ChangeLoopAssignmentRequestDto mockRequest = new ChangeLoopAssignmentRequestDto();
		ChangeLoopAssignmentRequestInputData inputData = new ChangeLoopAssignmentRequestInputData();
		mockRequest.setInputData(inputData);

		ChangeLoopAssignmentResponseDto mockResponse = new ChangeLoopAssignmentResponseDto();
		MessageStatus messageStatus = new MessageStatus();
		messageStatus.setErrorStatus("S");
		mockResponse.setMessageStatus(messageStatus);

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		response.setHookReturnCode(HOOK_RETURN_6);

		IVRUserSession userSession = new IVRUserSession();
		userSession.setSessionId(sessionId);
		userSession.setEmpID("9876");
		userSession.setCanBePagedMobile(true);

		IVRCnfEntity mockCnfSession = new IVRCnfEntity();
		mockCnfSession.setSessionId(sessionId);
		mockCnfSession.setCable("1");
		mockCnfSession.setPair("3");
		mockCnfSession.setSegmentRead("F1"); // Segment

		String jsonRequestString = "mock-request-string";
		String mockLJsonResponse = "mock-lfacs-response";

		// Mocking
		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(userSession.getLosDbResponse()))
				.thenReturn(losDbResponse);
		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockIvrCnfCacheService.getBySessionId(sessionId)).thenReturn(mockCnfSession);

		when(mockIvrCnfHelper.buildChangeLoopAssignmentRequest(anyString(), anyString(), anyString(), anyString(),
				anyString(), any(), any(), any(), any())).thenReturn(mockRequest);
		when(mockObjectMapper.writeValueAsString(mockRequest)).thenReturn(jsonRequestString);
		when(mockIvrHttpClient.httpPostCall(any(), any(), any(), anyString())).thenReturn(mockLJsonResponse);

		when(mockIVRLfacsServiceHelper.cleanResponseString(mockLJsonResponse)).thenReturn(mockLJsonResponse);
		when(mockObjectMapper.readValue(mockLJsonResponse, ChangeLoopAssignmentResponseDto.class))
				.thenReturn(mockResponse);
		when(ivrCnfPagerTextFormation.getPageChangeLoopAssignment(any(), any(), any(),
				any(), anyString(), anyString(), anyString(), anyString())).thenReturn(mockResponse);		
		when(mockIvrCnfHelper.processResponse(any(), anyString(), any())).thenReturn(response);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND741(sessionId, currentState, userInputs);

		assertEquals(HOOK_RETURN_6, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFND741Code_HookCode_4() throws JsonMappingException, JsonProcessingException, HttpTimeoutException, InterruptedException, ExecutionException {
		// test when pager is TRUE and segmentRead is F2 or F3
		String sessionId = "session123";
		String currentState = "FID741";

		List<String> userInputs = new ArrayList<>();
		userInputs.add("12");
		userInputs.add("247");
		
		so.setORD("True");
		soList.add(so);
		loop.setCKID("763 757-4229");
		loop.setSO(soList);
		loop.setTID("DPAAB");
		loopList.add(loop);
		returnDataSet.setLoop(loopList);
		returnDataSet.setPort1("test");
		currentAssignmentResponseDto.setReturnDataSet(returnDataSet);

		TNInfoResponse losDbResponse = new TNInfoResponse();
		losDbResponse.setTn("7637574229");
		losDbResponse.setPrimaryNPA("763");
		losDbResponse.setPrimaryNXX("757");

		ChangeLoopAssignmentRequestDto mockRequest = new ChangeLoopAssignmentRequestDto();
		ChangeLoopAssignmentRequestInputData inputData = new ChangeLoopAssignmentRequestInputData();
		mockRequest.setInputData(inputData);

		ChangeLoopAssignmentResponseDto mockResponse = new ChangeLoopAssignmentResponseDto();
		MessageStatus messageStatus = new MessageStatus();
		messageStatus.setErrorStatus("S");
		mockResponse.setMessageStatus(messageStatus);

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		response.setHookReturnCode(HOOK_RETURN_4);

		IVRUserSession userSession = new IVRUserSession();
		userSession.setSessionId(sessionId);
		userSession.setEmpID("9876");
		userSession.setCanBePagedMobile(true); // Pager

		IVRCnfEntity mockCnfSession = new IVRCnfEntity();
		mockCnfSession.setSessionId(sessionId);
		mockCnfSession.setCable("1");
		mockCnfSession.setPair("3");
		mockCnfSession.setSegmentRead("F2"); // Segment

		String jsonRequestString = "mock-request-string";
		String mockLJsonResponse = "mock-lfacs-response";

		// Mocking
		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(userSession.getLosDbResponse()))
				.thenReturn(losDbResponse);
		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockIvrCnfCacheService.getBySessionId(sessionId)).thenReturn(mockCnfSession);

		when(mockIvrCnfHelper.buildChangeLoopAssignmentRequest(anyString(), anyString(), anyString(), anyString(),
				anyString(), any(), any(), any(), any())).thenReturn(mockRequest);
		when(mockObjectMapper.writeValueAsString(mockRequest)).thenReturn(jsonRequestString);
		when(mockIvrHttpClient.httpPostCall(any(), any(), any(), anyString())).thenReturn(mockLJsonResponse);

		when(mockIVRLfacsServiceHelper.cleanResponseString(mockLJsonResponse)).thenReturn(mockLJsonResponse);
		when(mockObjectMapper.readValue(mockLJsonResponse, ChangeLoopAssignmentResponseDto.class))
				.thenReturn(mockResponse);
		when(ivrCnfPagerTextFormation.getPageChangeLoopAssignment(any(), any(), any(),
				any(), anyString(), anyString(), anyString(), anyString())).thenReturn(mockResponse);				
		when(mockIvrCnfHelper.processResponse(any(), anyString(), any())).thenReturn(response);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND741(sessionId, currentState, userInputs);

		assertEquals(HOOK_RETURN_4, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFND741Code_HookCode_5() throws JsonMappingException, JsonProcessingException, HttpTimeoutException, InterruptedException, ExecutionException {
		// test when pager is TRUE and segmentRead is F3 or F2
		String sessionId = "session123";
		String currentState = "FID741";

		List<String> userInputs = new ArrayList<>();
		userInputs.add("12");
		userInputs.add("247");
		
		so.setORD("True");
		soList.add(so);
		loop.setCKID("763 757-4229");
		loop.setSO(soList);
		loop.setTID("DPAAB");
		loopList.add(loop);
		returnDataSet.setLoop(loopList);
		returnDataSet.setPort1("test");
		currentAssignmentResponseDto.setReturnDataSet(returnDataSet);

		TNInfoResponse losDbResponse = new TNInfoResponse();
		losDbResponse.setTn("7637574229");
		losDbResponse.setPrimaryNPA("763");
		losDbResponse.setPrimaryNXX("757");

		ChangeLoopAssignmentRequestDto mockRequest = new ChangeLoopAssignmentRequestDto();
		ChangeLoopAssignmentRequestInputData inputData = new ChangeLoopAssignmentRequestInputData();
		mockRequest.setInputData(inputData);

		ChangeLoopAssignmentResponseDto mockResponse = new ChangeLoopAssignmentResponseDto();
		MessageStatus messageStatus = new MessageStatus();
		messageStatus.setErrorStatus("S");
		mockResponse.setMessageStatus(messageStatus);

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		response.setHookReturnCode(HOOK_RETURN_5);

		IVRUserSession userSession = new IVRUserSession();
		userSession.setSessionId(sessionId);
		userSession.setEmpID("9876");
		userSession.setCanBePagedMobile(false); // Pager

		IVRCnfEntity mockCnfSession = new IVRCnfEntity();
		mockCnfSession.setSessionId(sessionId);
		mockCnfSession.setCable("1");
		mockCnfSession.setPair("3");
		mockCnfSession.setSegmentRead("F3"); // Segment

		String jsonRequestString = "mock-request-string";
		String mockLJsonResponse = "mock-lfacs-response";

		// Mocking
		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(userSession.getLosDbResponse()))
				.thenReturn(losDbResponse);
		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockIvrCnfCacheService.getBySessionId(sessionId)).thenReturn(mockCnfSession);

		when(mockIvrCnfHelper.buildChangeLoopAssignmentRequest(anyString(), anyString(), anyString(), anyString(),
				anyString(), any(), any(), any(), any())).thenReturn(mockRequest);
		when(mockObjectMapper.writeValueAsString(mockRequest)).thenReturn(jsonRequestString);
		when(mockIvrHttpClient.httpPostCall(any(), any(), any(), anyString())).thenReturn(mockLJsonResponse);

		when(mockIVRLfacsServiceHelper.cleanResponseString(mockLJsonResponse)).thenReturn(mockLJsonResponse);
		when(mockObjectMapper.readValue(mockLJsonResponse, ChangeLoopAssignmentResponseDto.class))
				.thenReturn(mockResponse);
		when(ivrCnfPagerTextFormation.getPageChangeLoopAssignment(any(), any(), any(),
				any(), anyString(), anyString(), anyString(), anyString())).thenReturn(mockResponse);		
		when(mockIvrCnfHelper.processResponse(any(), anyString(), any())).thenReturn(response);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND741(sessionId, currentState, userInputs);

		assertEquals(HOOK_RETURN_5, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFND742_1() {

		String sessionId = "session123";

		String currentState = "STATE_FND742";
		
		List<String> userInputs = new ArrayList<>();
		userInputs.add("12");
		userInputs.add("247");

		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId(sessionId);

		when(mockCacheService.getBySessionId(Mockito.any(String.class))).thenReturn(null);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND742(sessionId, currentState, userInputs);

		assertEquals(null, actualResponse);

	}

	@Test
	void testProcessFND742_HOOK_RETURN() throws JsonMappingException, JsonProcessingException, InterruptedException,
			ExecutionException, HttpTimeoutException {

		String sessionId = "session123";

		String currentState = "STATE_FND742";
		
		List<String> userInputs = new ArrayList<>();
		userInputs.add("12");
		userInputs.add("247");

		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId(sessionId);
		IVRCnfEntity mockCnfSession = new IVRCnfEntity();

		mockCnfSession.setSessionId(sessionId);

		TNInfoResponse mockTnInfo = new TNInfoResponse();
		mockTnInfo.setTn("1234567890");
		mockTnInfo.setPrimaryNPA("123");
		mockTnInfo.setPrimaryNXX("456");

		String mockLosDbJsonString = "mock-losdb-response-string";

		mockSession.setLosDbResponse(mockLosDbJsonString);

		when(mockCacheService.getBySessionId(Mockito.any(String.class))).thenReturn(mockSession);
		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(mockLosDbJsonString)).thenReturn(mockTnInfo);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND742(sessionId, currentState, userInputs);

		assertEquals(HOOK_RETURN, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFND742_5_F2() throws JsonMappingException, JsonProcessingException, InterruptedException,
			ExecutionException, HttpTimeoutException {

		String sessionId = "session123";
		String currentState = "STATE_FND742";
		
		List<String> userInputs = new ArrayList<>();
		userInputs.add("12");
		userInputs.add("247");

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		response.setSessionId(sessionId);

		response.setCurrentState(currentState);
		response.setHookReturnCode(HOOK_RETURN_5);

		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId(sessionId);
		mockSession.setCanBePagedMobile(false);
		mockSession.setEmpID("1");

		IVRCnfEntity mockCnfSession = new IVRCnfEntity();
		mockCnfSession.setSessionId(sessionId);
		mockCnfSession.setSegmentRead("F2");

		TNInfoResponse mockTnInfo = new TNInfoResponse();
		mockTnInfo.setTn("1234567890");
		mockTnInfo.setPrimaryNPA("123");
		mockTnInfo.setPrimaryNXX("456");

		String jsonRequestString = "mock-request-string";
		String mockLfacsResponse = "mock-lfacs-response";
		String mockLosDbJsonString = "mock-losdb-response-string";

		mockSession.setLosDbResponse(mockLosDbJsonString);

		MainChangeTicketLoopRequest mockRequest = new MainChangeTicketLoopRequest();

		MainChangeTicketLoopResponse mockResponse = new MainChangeTicketLoopResponse();

		MessageStatus messageStatus = new MessageStatus();
		messageStatus.setErrorStatus("S");
		mockResponse.setMessageStatus(messageStatus);
		
		ChangeLoopAssignmentResponseDto mockChangeLoopAssignmentResponse = new ChangeLoopAssignmentResponseDto();
		mockChangeLoopAssignmentResponse.setMessageStatus(messageStatus);		

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(mockSession);
		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(mockLosDbJsonString)).thenReturn(mockTnInfo);
		when(mockIvrCnfCacheService.getBySessionId(sessionId)).thenReturn(mockCnfSession);

		when(mockIvrCnfHelper.buildMainChangeTicketLoopRequest(anyString(), anyString(), anyString(), anyString(),
				any(), any(), any(), any())).thenReturn(mockRequest);
		when(mockObjectMapper.writeValueAsString(mockRequest)).thenReturn(jsonRequestString);
		when(mockIvrHttpClient.httpPostCall(any(), any(), any(), anyString())).thenReturn(mockLfacsResponse);
		when(mockIVRLfacsServiceHelper.cleanResponseString(mockLfacsResponse)).thenReturn(mockLfacsResponse);
		when(mockObjectMapper.readValue(mockLfacsResponse, MainChangeTicketLoopResponse.class))
				.thenReturn(mockResponse);
		when(ivrCnfPagerTextFormation.getPageChangeLoopAssignment(any(), any(), any(),
				any(), anyString(), anyString(), anyString(), anyString())).thenReturn(mockChangeLoopAssignmentResponse);			
		when(mockIvrCnfHelper.processResponse(any(), anyString(), any())).thenReturn(response);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND742(sessionId, currentState, userInputs);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_5, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFND742_4_F2() throws JsonMappingException, JsonProcessingException, InterruptedException,
			ExecutionException, HttpTimeoutException {

		String sessionId = "session123";
		String currentState = "STATE_FND742";
		
		List<String> userInputs = new ArrayList<>();
		userInputs.add("12");
		userInputs.add("247");

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		response.setSessionId(sessionId);

		response.setCurrentState(currentState);
		response.setHookReturnCode(HOOK_RETURN_4);

		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId(sessionId);
		mockSession.setCanBePagedMobile(true);
		mockSession.setEmpID("1");

		IVRCnfEntity mockCnfSession = new IVRCnfEntity();
		mockCnfSession.setSessionId(sessionId);
		mockCnfSession.setSegmentRead("F2");

		TNInfoResponse mockTnInfo = new TNInfoResponse();
		mockTnInfo.setTn("1234567890");
		mockTnInfo.setPrimaryNPA("123");
		mockTnInfo.setPrimaryNXX("456");

		String jsonRequestString = "mock-request-string";
		String mockLfacsResponse = "mock-lfacs-response";
		String mockLosDbJsonString = "mock-losdb-response-string";

		mockSession.setLosDbResponse(mockLosDbJsonString);

		MainChangeTicketLoopRequest mockRequest = new MainChangeTicketLoopRequest();

		MainChangeTicketLoopResponse mockResponse = new MainChangeTicketLoopResponse();

		MessageStatus messageStatus = new MessageStatus();
		messageStatus.setErrorStatus("S");
		mockResponse.setMessageStatus(messageStatus);
		
		ChangeLoopAssignmentResponseDto mockChangeLoopAssignmentResponse = new ChangeLoopAssignmentResponseDto();
		mockChangeLoopAssignmentResponse.setMessageStatus(messageStatus);		

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(mockSession);
		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(mockLosDbJsonString)).thenReturn(mockTnInfo);
		when(mockIvrCnfCacheService.getBySessionId(sessionId)).thenReturn(mockCnfSession);

		when(mockIvrCnfHelper.buildMainChangeTicketLoopRequest(anyString(), anyString(), anyString(), anyString(),
				any(), any(), any(), any())).thenReturn(mockRequest);
		when(mockObjectMapper.writeValueAsString(mockRequest)).thenReturn(jsonRequestString);
		when(mockIvrHttpClient.httpPostCall(any(), any(), any(), anyString())).thenReturn(mockLfacsResponse);
		when(mockIVRLfacsServiceHelper.cleanResponseString(mockLfacsResponse)).thenReturn(mockLfacsResponse);
		when(mockObjectMapper.readValue(mockLfacsResponse, MainChangeTicketLoopResponse.class))
				.thenReturn(mockResponse);
		when(ivrCnfPagerTextFormation.getPageChangeLoopAssignment(any(), any(), any(),
				any(), anyString(), anyString(), anyString(), anyString())).thenReturn(mockChangeLoopAssignmentResponse);			
		when(mockIvrCnfHelper.processResponse(any(), anyString(), any())).thenReturn(response);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND742(sessionId, currentState, userInputs);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_4, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFND742_5_F3() throws JsonMappingException, JsonProcessingException, InterruptedException,
			ExecutionException, HttpTimeoutException {

		String sessionId = "session123";
		String currentState = "STATE_FND742";
		
		List<String> userInputs = new ArrayList<>();
		userInputs.add("12");
		userInputs.add("247");

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		response.setSessionId(sessionId);

		response.setCurrentState(currentState);
		response.setHookReturnCode(HOOK_RETURN_5);

		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId(sessionId);
		mockSession.setCanBePagedMobile(false);
		mockSession.setEmpID("1");

		IVRCnfEntity mockCnfSession = new IVRCnfEntity();
		mockCnfSession.setSessionId(sessionId);
		mockCnfSession.setSegmentRead("F3");

		TNInfoResponse mockTnInfo = new TNInfoResponse();
		mockTnInfo.setTn("1234567890");
		mockTnInfo.setPrimaryNPA("123");
		mockTnInfo.setPrimaryNXX("456");

		String jsonRequestString = "mock-request-string";
		String mockLfacsResponse = "mock-lfacs-response";
		String mockLosDbJsonString = "mock-losdb-response-string";

		mockSession.setLosDbResponse(mockLosDbJsonString);

		MainChangeTicketLoopRequest mockRequest = new MainChangeTicketLoopRequest();

		MainChangeTicketLoopResponse mockResponse = new MainChangeTicketLoopResponse();

		MessageStatus messageStatus = new MessageStatus();
		messageStatus.setErrorStatus("S");
		mockResponse.setMessageStatus(messageStatus);
		
		ChangeLoopAssignmentResponseDto mockChangeLoopAssignmentResponse = new ChangeLoopAssignmentResponseDto();
		mockChangeLoopAssignmentResponse.setMessageStatus(messageStatus);		

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(mockSession);
		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(mockLosDbJsonString)).thenReturn(mockTnInfo);
		when(mockIvrCnfCacheService.getBySessionId(sessionId)).thenReturn(mockCnfSession);

		when(mockIvrCnfHelper.buildMainChangeTicketLoopRequest(anyString(), anyString(), anyString(), anyString(),
				any(), any(), any(), any())).thenReturn(mockRequest);
		when(mockObjectMapper.writeValueAsString(mockRequest)).thenReturn(jsonRequestString);
		when(mockIvrHttpClient.httpPostCall(any(), any(), any(), anyString())).thenReturn(mockLfacsResponse);
		when(mockIVRLfacsServiceHelper.cleanResponseString(mockLfacsResponse)).thenReturn(mockLfacsResponse);
		when(mockObjectMapper.readValue(mockLfacsResponse, MainChangeTicketLoopResponse.class))
				.thenReturn(mockResponse);
		when(ivrCnfPagerTextFormation.getPageChangeLoopAssignment(any(), any(), any(),
				any(), anyString(), anyString(), anyString(), anyString())).thenReturn(mockChangeLoopAssignmentResponse);			
		when(mockIvrCnfHelper.processResponse(any(), anyString(), any())).thenReturn(response);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND742(sessionId, currentState, userInputs);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_5, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFND742_4_F3() throws JsonMappingException, JsonProcessingException, InterruptedException,
			ExecutionException, HttpTimeoutException {

		String sessionId = "session123";
		String currentState = "STATE_FND742";
		
		List<String> userInputs = new ArrayList<>();
		userInputs.add("12");
		userInputs.add("247");

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		response.setSessionId(sessionId);

		response.setCurrentState(currentState);
		response.setHookReturnCode(HOOK_RETURN_4);

		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId(sessionId);
		mockSession.setCanBePagedMobile(true);
		mockSession.setEmpID("1");

		IVRCnfEntity mockCnfSession = new IVRCnfEntity();
		mockCnfSession.setSessionId(sessionId);
		mockCnfSession.setSegmentRead("F3");

		TNInfoResponse mockTnInfo = new TNInfoResponse();
		mockTnInfo.setTn("1234567890");
		mockTnInfo.setPrimaryNPA("123");
		mockTnInfo.setPrimaryNXX("456");

		String jsonRequestString = "mock-request-string";
		String mockLfacsResponse = "mock-lfacs-response";
		String mockLosDbJsonString = "mock-losdb-response-string";

		mockSession.setLosDbResponse(mockLosDbJsonString);

		MainChangeTicketLoopRequest mockRequest = new MainChangeTicketLoopRequest();

		MainChangeTicketLoopResponse mockResponse = new MainChangeTicketLoopResponse();

		MessageStatus messageStatus = new MessageStatus();
		messageStatus.setErrorStatus("S");
		mockResponse.setMessageStatus(messageStatus);
		
		ChangeLoopAssignmentResponseDto mockChangeLoopAssignmentResponse = new ChangeLoopAssignmentResponseDto();
		mockChangeLoopAssignmentResponse.setMessageStatus(messageStatus);			

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(mockSession);
		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(mockLosDbJsonString)).thenReturn(mockTnInfo);
		when(mockIvrCnfCacheService.getBySessionId(sessionId)).thenReturn(mockCnfSession);

		when(mockIvrCnfHelper.buildMainChangeTicketLoopRequest(anyString(), anyString(), anyString(), anyString(),
				any(), any(), any(), any())).thenReturn(mockRequest);
		when(mockObjectMapper.writeValueAsString(mockRequest)).thenReturn(jsonRequestString);
		when(mockIvrHttpClient.httpPostCall(any(), any(), any(), anyString())).thenReturn(mockLfacsResponse);
		when(mockIVRLfacsServiceHelper.cleanResponseString(mockLfacsResponse)).thenReturn(mockLfacsResponse);
		when(mockObjectMapper.readValue(mockLfacsResponse, MainChangeTicketLoopResponse.class))
				.thenReturn(mockResponse);
		when(ivrCnfPagerTextFormation.getPageChangeLoopAssignment(any(), any(), any(),
				any(), anyString(), anyString(), anyString(), anyString())).thenReturn(mockChangeLoopAssignmentResponse);		
		when(mockIvrCnfHelper.processResponse(any(), anyString(), any())).thenReturn(response);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND742(sessionId, currentState, userInputs);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_4, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFND742_3_F1() throws JsonMappingException, JsonProcessingException, InterruptedException,
			ExecutionException, HttpTimeoutException {

		String sessionId = "session123";
		String currentState = "STATE_FND742";
		
		List<String> userInputs = new ArrayList<>();
		userInputs.add("12");
		userInputs.add("247");

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		response.setSessionId(sessionId);

		response.setCurrentState(currentState);
		response.setHookReturnCode(HOOK_RETURN_3);

		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId(sessionId);
		mockSession.setCanBePagedMobile(true);
		mockSession.setEmpID("1");

		IVRCnfEntity mockCnfSession = new IVRCnfEntity();
		mockCnfSession.setSessionId(sessionId);
		mockCnfSession.setSegmentRead("F1");

		TNInfoResponse mockTnInfo = new TNInfoResponse();
		mockTnInfo.setTn("1234567890");
		mockTnInfo.setPrimaryNPA("123");
		mockTnInfo.setPrimaryNXX("456");

		String jsonRequestString = "mock-request-string";
		String mockLfacsResponse = "mock-lfacs-response";
		String mockLosDbJsonString = "mock-losdb-response-string";

		mockSession.setLosDbResponse(mockLosDbJsonString);

		MainChangeTicketLoopRequest mockRequest = new MainChangeTicketLoopRequest();

		MainChangeTicketLoopResponse mockResponse = new MainChangeTicketLoopResponse();

		MessageStatus messageStatus = new MessageStatus();
		messageStatus.setErrorStatus("S");
		mockResponse.setMessageStatus(messageStatus);
		
		ChangeLoopAssignmentResponseDto mockChangeLoopAssignmentResponse = new ChangeLoopAssignmentResponseDto();
		mockChangeLoopAssignmentResponse.setMessageStatus(messageStatus);			

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(mockSession);
		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(mockLosDbJsonString)).thenReturn(mockTnInfo);
		when(mockIvrCnfCacheService.getBySessionId(sessionId)).thenReturn(mockCnfSession);

		when(mockIvrCnfHelper.buildMainChangeTicketLoopRequest(anyString(), anyString(), anyString(), anyString(),
				any(), any(), any(), any())).thenReturn(mockRequest);
		when(mockObjectMapper.writeValueAsString(mockRequest)).thenReturn(jsonRequestString);
		when(mockIvrHttpClient.httpPostCall(any(), any(), any(), anyString())).thenReturn(mockLfacsResponse);
		when(mockIVRLfacsServiceHelper.cleanResponseString(mockLfacsResponse)).thenReturn(mockLfacsResponse);
		when(mockObjectMapper.readValue(mockLfacsResponse, MainChangeTicketLoopResponse.class))
				.thenReturn(mockResponse);
		when(ivrCnfPagerTextFormation.getPageChangeLoopAssignment(any(), any(), any(),
				any(), anyString(), anyString(), anyString(), anyString())).thenReturn(mockChangeLoopAssignmentResponse);		
		when(mockIvrCnfHelper.processResponse(any(), anyString(), any())).thenReturn(response);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND742(sessionId, currentState, userInputs);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_3, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFND742_6_F1() throws JsonMappingException, JsonProcessingException, InterruptedException,
			ExecutionException, HttpTimeoutException {

		String sessionId = "session123";
		String currentState = "STATE_FND742";
		
		List<String> userInputs = new ArrayList<>();
		userInputs.add("12");
		userInputs.add("247");

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		response.setSessionId(sessionId);

		response.setCurrentState(currentState);
		response.setHookReturnCode(HOOK_RETURN_6);

		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId(sessionId);
		mockSession.setCanBePagedMobile(false);
		mockSession.setEmpID("1");

		IVRCnfEntity mockCnfSession = new IVRCnfEntity();
		mockCnfSession.setSessionId(sessionId);
		mockCnfSession.setSegmentRead("F1");

		TNInfoResponse mockTnInfo = new TNInfoResponse();
		mockTnInfo.setTn("1234567890");
		mockTnInfo.setPrimaryNPA("123");
		mockTnInfo.setPrimaryNXX("456");

		String jsonRequestString = "mock-request-string";
		String mockLfacsResponse = "mock-lfacs-response";
		String mockLosDbJsonString = "mock-losdb-response-string";

		mockSession.setLosDbResponse(mockLosDbJsonString);

		MainChangeTicketLoopRequest mockRequest = new MainChangeTicketLoopRequest();

		MainChangeTicketLoopResponse mockResponse = new MainChangeTicketLoopResponse();

		MessageStatus messageStatus = new MessageStatus();
		messageStatus.setErrorStatus("S");
		mockResponse.setMessageStatus(messageStatus);
		
		ChangeLoopAssignmentResponseDto mockChangeLoopAssignmentResponse = new ChangeLoopAssignmentResponseDto();
		mockChangeLoopAssignmentResponse.setMessageStatus(messageStatus);			

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(mockSession);
		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(mockLosDbJsonString)).thenReturn(mockTnInfo);
		when(mockIvrCnfCacheService.getBySessionId(sessionId)).thenReturn(mockCnfSession);

		when(mockIvrCnfHelper.buildMainChangeTicketLoopRequest(anyString(), anyString(), anyString(), anyString(),
				any(), any(), any(), any())).thenReturn(mockRequest);
		when(mockObjectMapper.writeValueAsString(mockRequest)).thenReturn(jsonRequestString);
		when(mockIvrHttpClient.httpPostCall(any(), any(), any(), anyString())).thenReturn(mockLfacsResponse);
		when(mockIVRLfacsServiceHelper.cleanResponseString(mockLfacsResponse)).thenReturn(mockLfacsResponse);
		when(mockObjectMapper.readValue(mockLfacsResponse, MainChangeTicketLoopResponse.class))
				.thenReturn(mockResponse);
		when(ivrCnfPagerTextFormation.getPageChangeLoopAssignment(any(), any(), any(),
				any(), anyString(), anyString(), anyString(), anyString())).thenReturn(mockChangeLoopAssignmentResponse);		
		// when(mockIvrCnfHelper.processResponse(response,mockCnfSession.getSegmentRead(),mockSession)).thenReturn(response);
		when(mockIvrCnfHelper.processResponse(any(), anyString(), any())).thenReturn(response);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND742(sessionId, currentState, userInputs);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_6, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFND746CodeRetLoopAssign() throws JsonMappingException, JsonProcessingException,
			InterruptedException, ExecutionException, HttpTimeoutException {

		String sessionId = "session123";
		String currentState = "FND746";

		so.setORD("True");
		soList.add(so);
		loop.setCKID("763 757-4229");
		loop.setSO(null);
		loop.setTID("DPAAB");
		loopList.add(loop);
		returnDataSet.setLoop(loopList);
		returnDataSet.setPort1("test");
		currentAssignmentResponseDto.setReturnDataSet(returnDataSet);

		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId(sessionId);
		mockSession.setCanBePagedMobile(true);
		mockSession.setCurrentAssignmentResponse("xyz");
		List<String> userInputDTMFList = new ArrayList<>();
		userInputDTMFList.add("1");

		TNInfoResponse mockTnInfo = new TNInfoResponse();
		mockTnInfo.setTn("1234567890");
		mockTnInfo.setPrimaryNPA("123");
		mockTnInfo.setPrimaryNXX("456");
		String jsonRequestString = "mock-request-string";
		String mockLfacsResponse = "mock-lfacs-response";
		String mockLosDbJsonString = "mock-losdb-response-string";
		RetrieveMaintenanceChangeTicketRequest mockRequest = new RetrieveMaintenanceChangeTicketRequest();
		RetrieveMaintenanceChangeTicketResponse mockResponse = new RetrieveMaintenanceChangeTicketResponse();
		mockSession.setLosDbResponse(mockLosDbJsonString);
		mockSession.setRtrvMaintChngeMsgName(mockLosDbJsonString);

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(mockSession);
		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(mockLosDbJsonString)).thenReturn(mockTnInfo);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND746(sessionId, currentState,
				userInputDTMFList);

		// assertEquals(IVRHookReturnCodes.HOOK_RETURN_2,
		// actualResponse.getHookReturnCode());
	}

	@Mock
	SparePairPageBuilder mockSparePairPageBuilder;

	@Test
	void testProcessFND746CodeRetLoopAssign_3() throws JsonMappingException, JsonProcessingException,
			InterruptedException, ExecutionException, HttpTimeoutException {

		String sessionId = "session123";
		String currentState = "FND746";

		so.setORD("True");
		soList.add(so);
		loop.setCKID("763 757-4229");
		loop.setSO(soList);
		loop.setTID("DPAAB");
		loopList.add(loop);
		returnDataSet.setLoop(loopList);
		returnDataSet.setPort1("test");
		currentAssignmentResponseDto.setReturnDataSet(returnDataSet);

		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId(sessionId);
		mockSession.setCanBePagedMobile(true);
		mockSession.setCanBePagedEmail(true);
		mockSession.setCurrentAssignmentResponse("xyz");
		List<String> userInputDTMFList = new ArrayList<>();
		userInputDTMFList.add("1");

		TNInfoResponse mockTnInfo = new TNInfoResponse();
		mockTnInfo.setTn("1234567890");
		mockTnInfo.setPrimaryNPA("123");
		mockTnInfo.setPrimaryNXX("456");
		String jsonRequestString = "mock-request-string";
		String mockLfacsResponse = "mock-lfacs-response";
		String mockLosDbJsonString = "mock-losdb-response-string";
		RetrieveLoopAssignmentRequest mockRequest = new RetrieveLoopAssignmentRequest();
		RetrieveLoopAssignmentResponse mockResponse = new RetrieveLoopAssignmentResponse();
		mockSession.setLosDbResponse(mockLosDbJsonString);
		mockSession.setRtrvLoopAssgMsgName(mockLosDbJsonString);
		mockSession.setCanBePagedMobile(true);
		mockSession.setCutPageSent(false);

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(mockSession);
		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(mockLosDbJsonString)).thenReturn(mockTnInfo);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND746(sessionId, currentState,
				userInputDTMFList);

		// assertEquals(IVRHookReturnCodes.HOOK_RETURN_2,
		// actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFND746CodeRetLoopAssign_2()
			throws JsonMappingException, JsonProcessingException, InterruptedException, ExecutionException {

		String sessionId = "session123";
		String currentState = "FND746";

		so.setORD("True");
		soList.add(so);
		loop.setCKID("763 757-4229");
		loop.setSO(soList);
		loop.setTID("DPAAB");
		loopList.add(loop);
		returnDataSet.setLoop(loopList);
		returnDataSet.setPort1("test");
		currentAssignmentResponseDto.setReturnDataSet(returnDataSet);

		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId(sessionId);
		mockSession.setCanBePagedMobile(false);
		mockSession.setCanBePagedEmail(true);
		mockSession.setCurrentAssignmentResponse("xyz");
		List<String> userInputDTMFList = new ArrayList<>();
		userInputDTMFList.add("1");

		TNInfoResponse mockTnInfo = new TNInfoResponse();
		mockTnInfo.setTn("1234567890");
		mockTnInfo.setPrimaryNPA("123");
		mockTnInfo.setPrimaryNXX("456");

		String mockLosDbJsonString = "mock-losdb-response-string";

		mockSession.setLosDbResponse(mockLosDbJsonString);
		mockSession.setRtrvLoopAssgMsgName(mockLosDbJsonString);

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(mockSession);
		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(mockLosDbJsonString)).thenReturn(mockTnInfo);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND746(sessionId, currentState,
				userInputDTMFList);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_3, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID141Code_HookCode_WithoutCNFSession() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID141";

		TNInfoResponse losDbResponse = new TNInfoResponse();
		losDbResponse.setTn("7637574229");
		losDbResponse.setPrimaryNPA("763");
		losDbResponse.setPrimaryNXX("757");

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		IVRUserSession userSession = new IVRUserSession();

		userSession.setSessionId(sessionId);

		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(userSession.getLosDbResponse()))
				.thenReturn(losDbResponse);

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockIvrCnfCacheService.getBySessionId(sessionId)).thenReturn(null);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND141(sessionId, currentState);

		assertEquals(HOOK_RETURN, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFND141Code_HookCode_WithoutSession() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session321";
		String currentState = "FND141";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(null);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND141(sessionId, currentState);

		assertEquals(HOOK_RETURN, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID141Code_HookCode_0() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID141";

		TNInfoResponse losDbResponse = new TNInfoResponse();
		losDbResponse.setTn("7637574229");
		losDbResponse.setPrimaryNPA("763");
		losDbResponse.setPrimaryNXX("757");

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		IVRUserSession userSession = new IVRUserSession();

		userSession.setSessionId(sessionId);

		IVRCnfEntity cnfSession = new IVRCnfEntity();

		cnfSession.setSessionId(sessionId);

		String jsonRequestString = "mock-request-string";
		String mockLfacsResponse = "mock-lfacs-response";
		String mockLJsonResponse = "mock-lfacs-response";

		LoopQualNIIServiceRequest request = new LoopQualNIIServiceRequest();
		NIIServiceResponse responseObject = new NIIServiceResponse();

		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(userSession.getLosDbResponse()))
				.thenReturn(losDbResponse);
		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockIvrCnfCacheService.getBySessionId(sessionId)).thenReturn(cnfSession);
		when(mockIvrCnfHelper.buildLoopQualNIIServiceRequest("4229", losDbResponse.getPrimaryNPA(),
				losDbResponse.getPrimaryNXX(), sessionId, cnfSession)).thenReturn(request);
		when(mockObjectMapper.writeValueAsString(request)).thenReturn(jsonRequestString);
		when(mockIvrHttpClient.httpPostCall(any(), any(), any(), anyString())).thenReturn(mockLfacsResponse);
		when(mockIVRLfacsServiceHelper.cleanResponseString(mockLfacsResponse)).thenReturn(mockLJsonResponse);

		when(mockObjectMapper.readValue(mockLJsonResponse, NIIServiceResponse.class)).thenReturn(responseObject);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND141(sessionId, currentState);

		assertEquals(HOOK_RETURN_0, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFND145Code_HookCode0_WithoutCurrentAssg() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FND145";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		IVRUserSession userSession = new IVRUserSession();

		currentAssignmentResponseDto.setMessageStatus(messageStatus);
		currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG().get(0).setCA("1");
		currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG().get(0).setPR("10");
		userSession.setCurrentAssignmentResponse(currentAssignmentResponseDto.toString());

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(null);
		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND145(sessionId, currentState, "8");

		assertEquals(HOOK_RETURN, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFND746_HookCode_WithoutSession() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session321";
		String currentState = "FND746";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId(sessionId);
		mockSession.setCanBePagedMobile(false);
		mockSession.setCanBePagedEmail(true);
		mockSession.setCurrentAssignmentResponse("xyz");
		List<String> userInputDTMFList = new ArrayList<>();
		userInputDTMFList.add("1");

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(null);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND746(sessionId, currentState,
				userInputDTMFList);
		assertNotNull(actualResponse);
	}

	@Test
	void testProcessFID143Code_HookCode_WithoutCNFSession() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FND143";

		TNInfoResponse losDbResponse = new TNInfoResponse();
		losDbResponse.setTn("7637574229");
		losDbResponse.setPrimaryNPA("763");
		losDbResponse.setPrimaryNXX("757");

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		IVRUserSession userSession = new IVRUserSession();

		userSession.setSessionId(sessionId);

		when(mockIvrCnfCacheService.getBySessionId(sessionId)).thenReturn(null);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND143(sessionId, currentState);

		assertEquals(HOOK_RETURN, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID143Code_HookCode_8() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FND143";

		TNInfoResponse losDbResponse = new TNInfoResponse();
		losDbResponse.setTn("7637574229");
		losDbResponse.setPrimaryNPA("763");
		losDbResponse.setPrimaryNXX("757");

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		IVRCnfEntity userSession = new IVRCnfEntity();

		userSession.setSessionId(sessionId);

		userSession.setGetLoopQualNIIResponse("loop_qual_Result");

		String loopQualNIIResultJson = "loop_qual_Result";

		NIIServiceResponse responseObject = new NIIServiceResponse();

		responseObject.setErrorMessage("SUCCESS");

		List<NetworkInfrastructure> list = new ArrayList<NetworkInfrastructure>();

		NetworkInfrastructure networkInfrastructure = new NetworkInfrastructure();
		networkInfrastructure.setVoiceActivationFlag(true);

		list.add(networkInfrastructure);
		responseObject.setNetworkInfraStructure(list);

		when(mockIvrCnfCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockObjectMapper.readValue(loopQualNIIResultJson, NIIServiceResponse.class)).thenReturn(responseObject);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND143(sessionId, currentState);

		assertEquals(HOOK_RETURN_8, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID143Code_HookCode_7() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FND143";

		TNInfoResponse losDbResponse = new TNInfoResponse();
		losDbResponse.setTn("7637574229");
		losDbResponse.setPrimaryNPA("763");
		losDbResponse.setPrimaryNXX("757");

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		IVRCnfEntity userSession = new IVRCnfEntity();

		userSession.setSessionId(sessionId);

		userSession.setGetLoopQualNIIResponse("loop_qual_Result");

		String loopQualNIIResultJson = "loop_qual_Result";

		NIIServiceResponse responseObject = new NIIServiceResponse();

		responseObject.setErrorMessage("SUCCESS");

		List<NetworkInfrastructure> list = new ArrayList<NetworkInfrastructure>();

		NetworkInfrastructure networkInfrastructure = new NetworkInfrastructure();
		networkInfrastructure.setVoiceActivationFlag(false);

		list.add(networkInfrastructure);
		responseObject.setNetworkInfraStructure(list);

		when(mockIvrCnfCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockObjectMapper.readValue(loopQualNIIResultJson, NIIServiceResponse.class)).thenReturn(responseObject);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND143(sessionId, currentState);

		assertEquals(HOOK_RETURN_7, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFND216Code_HookCode_WithoutCNFSession() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FND143";

		TNInfoResponse losDbResponse = new TNInfoResponse();
		losDbResponse.setTn("7637574229");
		losDbResponse.setPrimaryNPA("763");
		losDbResponse.setPrimaryNXX("757");

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		IVRUserSession userSession = new IVRUserSession();

		userSession.setSessionId(sessionId);

		when(mockIvrCnfCacheService.getBySessionId(sessionId)).thenReturn(null);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND216(sessionId, currentState);

		assertEquals(HOOK_RETURN, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFND216Code_HookCode_0() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FND143";

		TNInfoResponse losDbResponse = new TNInfoResponse();
		losDbResponse.setTn("7637574229");
		losDbResponse.setPrimaryNPA("763");
		losDbResponse.setPrimaryNXX("757");

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		IVRCnfEntity userSession = new IVRCnfEntity();

		userSession.setSessionId(sessionId);

		userSession.setGetLoopQualNIIResponse("loop_qual_Result");

		String loopQualNIIResultJson = "loop_qual_Result";

		NIIServiceResponse responseObject = new NIIServiceResponse();

		List<NetworkInfrastructure> list = new ArrayList<NetworkInfrastructure>();

		NetworkInfrastructure networkInfrastructure = new NetworkInfrastructure();
		networkInfrastructure.setVoiceActivationFlag(true);

		list.add(networkInfrastructure);
		responseObject.setNetworkInfraStructure(list);

		when(mockIvrCnfCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		// when(userSession.getGetLoopQualNIIResponse()).thenReturn(loopQualNIIResultJson);
		when(mockObjectMapper.readValue(loopQualNIIResultJson, NIIServiceResponse.class)).thenReturn(responseObject);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND216(sessionId, currentState);

		assertEquals(HOOK_RETURN_0, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFND216Code_HookCode_1() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FND143";

		TNInfoResponse losDbResponse = new TNInfoResponse();
		losDbResponse.setTn("7637574229");
		losDbResponse.setPrimaryNPA("763");
		losDbResponse.setPrimaryNXX("757");

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		IVRCnfEntity userSession = new IVRCnfEntity();

		userSession.setSessionId(sessionId);

		userSession.setGetLoopQualNIIResponse("loop_qual_Result");

		String loopQualNIIResultJson = "loop_qual_Result";

		NIIServiceResponse responseObject = new NIIServiceResponse();

		List<NetworkInfrastructure> list = new ArrayList<NetworkInfrastructure>();

		NetworkInfrastructure networkInfrastructure = new NetworkInfrastructure();
		networkInfrastructure.setVoiceActivationFlag(false);
		
		NetworkInfrastructure networkInfrastructure1 = new NetworkInfrastructure();
		networkInfrastructure1.setVoiceActivationFlag(true);

		list.add(networkInfrastructure);
		list.add(networkInfrastructure1);
		responseObject.setNetworkInfraStructure(list);

		when(mockIvrCnfCacheService.getBySessionId(sessionId)).thenReturn(userSession);

		when(mockObjectMapper.readValue(loopQualNIIResultJson, NIIServiceResponse.class)).thenReturn(responseObject);

		IVRWebHookResponseDto actualResponse = ivrCnfServiceImpl.processFND216(sessionId, currentState);

		assertEquals(HOOK_RETURN_1, actualResponse.getHookReturnCode());
	}
}
