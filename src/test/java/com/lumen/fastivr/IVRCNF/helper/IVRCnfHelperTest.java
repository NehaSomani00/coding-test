package com.lumen.fastivr.IVRCNF.helper;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

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
import com.lumen.fastivr.IVRCNF.Dto.ChangeLoopAssignmentReqInputDataCurrentLoopDetails;
import com.lumen.fastivr.IVRCNF.Dto.ChangeLoopAssignmentReqInputDataReplacementLoopDetails;
import com.lumen.fastivr.IVRCNF.Dto.ChangeLoopAssignmentRequestDto;
import com.lumen.fastivr.IVRCNF.Dto.ChangeLoopAssignmentRequestInputData;
import com.lumen.fastivr.IVRCNF.Dto.CurrentLoopDetails;
import com.lumen.fastivr.IVRCNF.Dto.InputData;
import com.lumen.fastivr.IVRCNF.Dto.MainChangeTicketLoopInputRequest;
import com.lumen.fastivr.IVRCNF.Dto.MainChangeTicketLoopRequest;
import com.lumen.fastivr.IVRCNF.Dto.ReplacementLoopDetails;
import com.lumen.fastivr.IVRCNF.Dto.VerifyCutInformationRequest;
import com.lumen.fastivr.IVRCNF.Dto.VerifyCutInformationResponse;
import com.lumen.fastivr.IVRCNF.entity.IVRCnfEntity;
import com.lumen.fastivr.IVRCNF.repository.IVRCnfCacheService;
import com.lumen.fastivr.IVRCacheManagement.IVRCacheService;
import com.lumen.fastivr.IVRDto.AuthorizationInfo;
import com.lumen.fastivr.IVRDto.CurrentAssignmentInfo;
import com.lumen.fastivr.IVRDto.CurrentAssignmentRequestTnDto;
import com.lumen.fastivr.IVRDto.CurrentAssignmentResponseDto;
import com.lumen.fastivr.IVRDto.IVRWebHookResponseDto;
import com.lumen.fastivr.IVRDto.LOOP;
import com.lumen.fastivr.IVRDto.MessageStatus;
import com.lumen.fastivr.IVRDto.ReturnDataSet;
import com.lumen.fastivr.IVRDto.SEG;
import com.lumen.fastivr.IVRDto.SO;
import com.lumen.fastivr.IVRDto.TN;
import com.lumen.fastivr.IVRDto.TargetSchemaVersionUsed;
import com.lumen.fastivr.IVRDto.WireCtrPrimaryNPANXX;
import com.lumen.fastivr.IVRDto.LOSDB.TNInfoResponse;
import com.lumen.fastivr.IVRDto.RetrieveMaintenanceChangeTicket.RetMaintChangeTicketInputData;
import com.lumen.fastivr.IVRDto.RetrieveMaintenanceChangeTicket.RetrieveMaintenanceChangeTicketRequest;
import com.lumen.fastivr.IVRDto.retrieveLoopAssignment.RetLoopAssigInputData;
import com.lumen.fastivr.IVRDto.retrieveLoopAssignment.RetrieveLoopAssignmentRequest;
import com.lumen.fastivr.IVRLFACS.IVRLfacsServiceHelper;
import com.lumen.fastivr.IVRSessionManagement.IVRUserSession;
import com.lumen.fastivr.IVRSignon.IVRSignOnServiceHelper;
import com.lumen.fastivr.httpclient.IVRHttpClient;

@ExtendWith(MockitoExtension.class)
public class IVRCnfHelperTest {

	@Mock
	private ObjectMapper mockObjectMapper;

	@Mock
	private IVRCacheService mockCacheService;

	@Mock
	private IVRCnfCacheService mockIvrCnfCacheService;

	@Mock
	private IVRLfacsServiceHelper mockIVRLfacsServiceHelper;

	@Mock
	private IVRSignOnServiceHelper mockIvrSignOnServiceHelper;

	@Mock
	IVRHttpClient mockIvrHttpClient;

	@InjectMocks
	private IVRCnfHelper ivrCnfHelper;

	@Mock
	private InputData inputData;

	CurrentAssignmentResponseDto currentAssignmentResponseDto = null;

	ReturnDataSet returnDataSet = new ReturnDataSet();
	SO so = new SO();
	List<SO> soList = new ArrayList<>();
	LOOP loop = new LOOP();
	List<LOOP> loopList = new ArrayList<>();

	@BeforeEach
	void setUp() throws Exception {

		currentAssignmentResponseDto = new CurrentAssignmentResponseDto();
		ReturnDataSet dataSet = new ReturnDataSet();
		List<LOOP> loopList = new ArrayList<LOOP>();
		LOOP loop = new LOOP();

		List<SEG> segList = new ArrayList<SEG>();

		SEG seg = new SEG();
		seg.setCTT("1");
		seg.setCA("ca1");
		seg.setPR("pr1");
		SEG seg2 = new SEG();
		seg.setCTT("2");
		seg.setCA("ca2");
		seg.setPR("pr2");
		SEG seg3 = new SEG();
		seg.setCTT("3");
		seg.setCA("ca3");
		seg.setPR("pr3");
		segList.add(seg);
		segList.add(seg2);
		segList.add(seg3);
		loop.setSEG(segList);
		loopList.add(loop);
		dataSet.setLoop(loopList);
		currentAssignmentResponseDto.setReturnDataSet(dataSet);
	}

	@Test
	void testconvertInputCodesToAlphabets_Null() {

		assertNull(ivrCnfHelper.convertInputCodesToAlphabets("/*21/*"));
	}

	@Test
	void testisSwitchAvailable() throws JsonMappingException, JsonProcessingException {

		IVRUserSession session = new IVRUserSession();

		String mockLosDbJsonString = "mock-losdb-response-string";

		session.setSessionId("123");
		session.setLosDbResponse(mockLosDbJsonString);
		session.setNpaPrefix("123");

		TNInfoResponse mockTnInfo = new TNInfoResponse();
		mockTnInfo.setTn("1234567890");
		mockTnInfo.setPrimaryNPA("123");
		mockTnInfo.setPrimaryNXX("456");

		IVRCnfEntity cnfSession = new IVRCnfEntity();

		cnfSession.setSessionId("123");
		cnfSession.setCable("123");
		cnfSession.setPair("abc");
		cnfSession.setCnfInqType("123");

		String jsonRequestString = "mock-request-string";
		String mockLfacsResponse = "mock-lfacs-response";
		String mockLJsonResponse = "mock-lfacs-response";

		VerifyCutInformationRequest mockRequest = new VerifyCutInformationRequest();
		VerifyCutInformationResponse mockResponse = new VerifyCutInformationResponse();
		cnfSession.setGetVerifyCutResponse(mockLosDbJsonString);

		InputData id = new InputData();

		WireCtrPrimaryNPANXX wireCtrPrimaryNPANXX = new WireCtrPrimaryNPANXX();
		wireCtrPrimaryNPANXX.setNpa("123");
		wireCtrPrimaryNPANXX.setNxx("456");
		id.setWireCtrPrimaryNPANXX(wireCtrPrimaryNPANXX);
		id.setCircuitId("123");
		id.setCableId("1234");
		id.setCableUnitId("abc");
		mockRequest.setInputData(id);

//		mockRequest.setRequestId("FASTFAST");
//		mockRequest.setWebServiceName("SIABusService");
//		mockRequest.setRequestPurpose("PD");

		AuthorizationInfo authorizationInfo = new AuthorizationInfo();
		authorizationInfo.setPassword("9312qrty!");
		authorizationInfo.setUserid("fasfast");
		//mockRequest.setAuthorizationInfo(authorizationInfo);

		TargetSchemaVersionUsed targetSchemaVersionUsed = new TargetSchemaVersionUsed();
		targetSchemaVersionUsed.setMajorVersionNumber(0);
		targetSchemaVersionUsed.setMinorVersionNumber(0);
		//mockRequest.setTargetSchemaVersionUsed(targetSchemaVersionUsed);

		//mockRequest.setTimeOutSecond(300);

		java.util.Date date = new java.util.Date();
		//mockRequest.setSendTimeStamp(new Timestamp(date.getTime()));

		MessageStatus messageStatus = new MessageStatus();
		messageStatus.setErrorStatus("S");
		mockResponse.setMessageStatus(messageStatus);

		when(mockIvrCnfCacheService.getBySessionId("123")).thenReturn(cnfSession);
		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(mockLosDbJsonString)).thenReturn(mockTnInfo);

		when(mockIvrHttpClient.httpPostCall(any(), any(), any(), anyString())).thenReturn(mockLfacsResponse);
		when(mockIVRLfacsServiceHelper.cleanResponseString(mockLfacsResponse)).thenReturn(mockLJsonResponse);
		when(mockObjectMapper.readValue(mockLJsonResponse, VerifyCutInformationResponse.class))
				.thenReturn(mockResponse);

		assertTrue(ivrCnfHelper.isSwitchAvailable(session));
	}

	@Test
	void testisSwitchAvailable_Npa() throws JsonMappingException, JsonProcessingException {

		IVRUserSession session = new IVRUserSession();

		String mockLosDbJsonString = "mock-losdb-response-string";

		session.setSessionId("123");
		session.setLosDbResponse(mockLosDbJsonString);
		session.setNpaPrefix("124");

		TNInfoResponse mockTnInfo = new TNInfoResponse();
		mockTnInfo.setTn("1234567890");
		mockTnInfo.setPrimaryNPA("123");
		mockTnInfo.setPrimaryNXX("456");

		IVRCnfEntity cnfSession = new IVRCnfEntity();

		cnfSession.setSessionId("123");
		cnfSession.setCable("123");
		cnfSession.setPair("abc");
		cnfSession.setCnfInqType("123");

		String jsonRequestString = "mock-request-string";
		String mockLfacsResponse = "mock-lfacs-response";
		String mockLJsonResponse = "mock-lfacs-response";

		VerifyCutInformationRequest mockRequest = new VerifyCutInformationRequest();
		VerifyCutInformationResponse mockResponse = new VerifyCutInformationResponse();
		cnfSession.setGetVerifyCutResponse(mockLosDbJsonString);

		MessageStatus messageStatus = new MessageStatus();
		messageStatus.setErrorStatus("S");
		mockResponse.setMessageStatus(messageStatus);

		when(mockIvrCnfCacheService.getBySessionId("123")).thenReturn(cnfSession);

		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(mockLosDbJsonString)).thenReturn(mockTnInfo);
		when(mockIvrSignOnServiceHelper.isNpaPresentInDB(anyString())).thenReturn(Boolean.TRUE);

		when(mockIvrHttpClient.httpPostCall(any(), any(), any(), anyString())).thenReturn(mockLfacsResponse);
		when(mockIVRLfacsServiceHelper.cleanResponseString(mockLfacsResponse)).thenReturn(mockLJsonResponse);
		when(mockObjectMapper.readValue(mockLJsonResponse, VerifyCutInformationResponse.class))
				.thenReturn(mockResponse);

		assertTrue(ivrCnfHelper.isSwitchAvailable(session));
	}

	@Test
	void testisSwitchAvailable_LosNull() throws JsonMappingException, JsonProcessingException {

		IVRUserSession session = new IVRUserSession();

		String mockLosDbJsonString = "mock-losdb-response-string";

		session.setSessionId("123");
		session.setLosDbResponse(mockLosDbJsonString);
		session.setNpaPrefix("124");

		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(mockLosDbJsonString)).thenReturn(null);

		assertFalse(ivrCnfHelper.isSwitchAvailable(session));
	}

	@Test
	void testCurrentAssignment()
			throws JsonMappingException, JsonProcessingException, InterruptedException, ExecutionException {

		IVRUserSession mockSession = new IVRUserSession();

		CurrentAssignmentRequestTnDto mockRequest = new CurrentAssignmentRequestTnDto();
		CurrentAssignmentResponseDto mockResponse = new CurrentAssignmentResponseDto();

		String mockLosDbJsonString = "mock-losdb-response-string";
		String jsonRequestString = "mock-request-string";
		String mockLfacsResponse = "mock-lfacs-response";

		mockSession.setSessionId("123");
		mockSession.setLosDbResponse(mockLosDbJsonString);

		TNInfoResponse mockTnInfo = new TNInfoResponse();
		mockTnInfo.setTn("1234567890");
		mockTnInfo.setPrimaryNPA("123");
		mockTnInfo.setPrimaryNXX("456");

		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(mockLosDbJsonString)).thenReturn(mockTnInfo);

		when(mockIVRLfacsServiceHelper.buildCurrentAssignmentInqRequest(mockTnInfo.getTn(), mockTnInfo.getPrimaryNPA(),
				mockTnInfo.getPrimaryNXX(), null, mockSession)).thenReturn(mockRequest);
		when(mockObjectMapper.writeValueAsString(mockRequest)).thenReturn(jsonRequestString);
		when(mockIVRLfacsServiceHelper.callCurrentAssignmentInquiryLfacs(jsonRequestString, mockSession))
				.thenReturn(mockLfacsResponse);
		when(mockIVRLfacsServiceHelper.cleanResponseString(mockLfacsResponse)).thenReturn(mockLfacsResponse);
		when(mockObjectMapper.readValue(mockLfacsResponse, CurrentAssignmentResponseDto.class))
				.thenReturn(mockResponse);
		when(mockCacheService.updateSession(Mockito.any(IVRUserSession.class))).thenReturn(mockSession);

		ivrCnfHelper.insertCurrentAssignment(mockSession);
	}

	@Test
	void testBuildMainChangeTicketLoopRequest() throws JsonMappingException, JsonProcessingException {

		String sessionid = "session123";

		IVRUserSession userSession = new IVRUserSession();

		userSession.setSessionId(sessionid);
		userSession.setCurrentAssignmentResponse("Test");

		IVRCnfEntity cnfSession = new IVRCnfEntity();

		cnfSession.setSessionId(sessionid);
		cnfSession.setSegmentRead("F1");

		MainChangeTicketLoopRequest request = new MainChangeTicketLoopRequest();

		CurrentAssignmentInfo currentAssignmentInfo = new CurrentAssignmentInfo();

		TN TN = new TN();

		TN.setCkid("12345678");

		currentAssignmentInfo.setTn(TN);

		MainChangeTicketLoopInputRequest id = new MainChangeTicketLoopInputRequest();

		id.setEmployeeId("123");
		WireCtrPrimaryNPANXX wireCtrPrimaryNPANXX = new WireCtrPrimaryNPANXX();
		wireCtrPrimaryNPANXX.setNpa("123");
		wireCtrPrimaryNPANXX.setNxx("321");
		id.setWireCtrPrimaryNPANXX(wireCtrPrimaryNPANXX);

		id.setSegNumber("1");

		request.setInputData(id);

		CurrentLoopDetails currentLoopDetails = new CurrentLoopDetails();

		currentLoopDetails.setCableId(cnfSession.getCable());
		currentLoopDetails.setCableUnitId(cnfSession.getPair());
		id.setCurrentLoopDetails(currentLoopDetails);
		request.setInputData(id);

		ReplacementLoopDetails replacementLoopDetails = new ReplacementLoopDetails();

		replacementLoopDetails.setCableId(cnfSession.getCable());
		replacementLoopDetails.setCableTroubleTicketNumber("5558533"); // TODO Business clarification
		replacementLoopDetails.setCableUnitId(cnfSession.getPair());
		replacementLoopDetails.setFacilityChangeReason("NDC");
		id.setReplacementLoopDetails(replacementLoopDetails);
		request.setInputData(id);

		request.setRequestId("FASTFAST");
		request.setWebServiceName("SIABusService");
		request.setRequestPurpose("TS");
		AuthorizationInfo authInfo = new AuthorizationInfo();
		authInfo.setUserid("fasfast");
		authInfo.setPassword("9312qrty!");
		request.setAuthorizationInfo(authInfo);
		TargetSchemaVersionUsed schema = new TargetSchemaVersionUsed();
		schema.setMajorVersionNumber(0);
		schema.setMinorVersionNumber(0);
		request.setTargetSchemaVersionUsed(schema);
		request.setTimeOutSecond(180);

		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponseDto);

		MainChangeTicketLoopRequest actualResponse = ivrCnfHelper.buildMainChangeTicketLoopRequest("12345678", "123",
				"321", "123", cnfSession, userSession, "12", "247");

		assertEquals("123", actualResponse.getInputData().getEmployeeId());
	}

	@Test
	void testBuildMainChangeTicketLoopRequest_F2() throws JsonMappingException, JsonProcessingException {

		String sessionid = "session123";

		IVRUserSession userSession = new IVRUserSession();

		userSession.setSessionId(sessionid);
		userSession.setCurrentAssignmentResponse("Test");

		IVRCnfEntity cnfSession = new IVRCnfEntity();

		cnfSession.setSessionId(sessionid);
		cnfSession.setSegmentRead("F2");

		MainChangeTicketLoopRequest request = new MainChangeTicketLoopRequest();

		CurrentAssignmentInfo currentAssignmentInfo = new CurrentAssignmentInfo();

		TN TN = new TN();

		TN.setCkid("12345678");

		currentAssignmentInfo.setTn(TN);

		MainChangeTicketLoopInputRequest id = new MainChangeTicketLoopInputRequest();

		id.setEmployeeId("123");
		WireCtrPrimaryNPANXX wireCtrPrimaryNPANXX = new WireCtrPrimaryNPANXX();
		wireCtrPrimaryNPANXX.setNpa("123");
		wireCtrPrimaryNPANXX.setNxx("321");
		id.setWireCtrPrimaryNPANXX(wireCtrPrimaryNPANXX);

		id.setSegNumber("2");

		request.setInputData(id);

		CurrentLoopDetails currentLoopDetails = new CurrentLoopDetails();

		currentLoopDetails.setCableId(cnfSession.getCable());
		currentLoopDetails.setCableUnitId(cnfSession.getPair());
		id.setCurrentLoopDetails(currentLoopDetails);
		request.setInputData(id);

		ReplacementLoopDetails replacementLoopDetails = new ReplacementLoopDetails();

		replacementLoopDetails.setCableId(cnfSession.getCable());
		replacementLoopDetails.setCableTroubleTicketNumber("5558533");
		replacementLoopDetails.setCableUnitId(cnfSession.getPair());
		replacementLoopDetails.setFacilityChangeReason("NDC");
		id.setReplacementLoopDetails(replacementLoopDetails);
		request.setInputData(id);

		request.setRequestId("FASTFAST");
		request.setWebServiceName("SIABusService");
		request.setRequestPurpose("TS");
		AuthorizationInfo authInfo = new AuthorizationInfo();
		authInfo.setUserid("fasfast");
		authInfo.setPassword("9312qrty!");
		request.setAuthorizationInfo(authInfo);
		TargetSchemaVersionUsed schema = new TargetSchemaVersionUsed();
		schema.setMajorVersionNumber(0);
		schema.setMinorVersionNumber(0);
		request.setTargetSchemaVersionUsed(schema);
		request.setTimeOutSecond(180);

		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponseDto);

		MainChangeTicketLoopRequest actualResponse = ivrCnfHelper.buildMainChangeTicketLoopRequest("12345678", "123",
				"321", "123", cnfSession, userSession, "12", "247");

		assertEquals("123", actualResponse.getInputData().getEmployeeId());
	}

	@Test
	void testBuildMainChangeTicketLoopRequest_F3() throws JsonMappingException, JsonProcessingException {

		String sessionid = "session123";

		IVRUserSession userSession = new IVRUserSession();

		userSession.setSessionId(sessionid);
		userSession.setCurrentAssignmentResponse("Test");

		IVRCnfEntity cnfSession = new IVRCnfEntity();

		cnfSession.setSessionId(sessionid);

		cnfSession.setSegmentRead("F3");

		MainChangeTicketLoopRequest request = new MainChangeTicketLoopRequest();

		CurrentAssignmentInfo currentAssignmentInfo = new CurrentAssignmentInfo();

		TN TN = new TN();

		TN.setCkid("12345678");

		currentAssignmentInfo.setTn(TN);

		MainChangeTicketLoopInputRequest id = new MainChangeTicketLoopInputRequest();

		id.setEmployeeId("123");
		WireCtrPrimaryNPANXX wireCtrPrimaryNPANXX = new WireCtrPrimaryNPANXX();
		wireCtrPrimaryNPANXX.setNpa("123");
		wireCtrPrimaryNPANXX.setNxx("321");
		id.setWireCtrPrimaryNPANXX(wireCtrPrimaryNPANXX);

		id.setSegNumber("3");

		request.setInputData(id);

		CurrentLoopDetails currentLoopDetails = new CurrentLoopDetails();

		currentLoopDetails.setCableId(cnfSession.getCable());
		currentLoopDetails.setCableUnitId(cnfSession.getPair());
		id.setCurrentLoopDetails(currentLoopDetails);
		request.setInputData(id);

		ReplacementLoopDetails replacementLoopDetails = new ReplacementLoopDetails();

		replacementLoopDetails.setCableId(cnfSession.getCable());
		replacementLoopDetails.setCableTroubleTicketNumber("5558533");
		replacementLoopDetails.setCableUnitId(cnfSession.getPair());
		replacementLoopDetails.setFacilityChangeReason("NDC");
		id.setReplacementLoopDetails(replacementLoopDetails);
		request.setInputData(id);

		request.setRequestId("FASTFAST");
		request.setWebServiceName("SIABusService");
		request.setRequestPurpose("TS");
		AuthorizationInfo authInfo = new AuthorizationInfo();
		authInfo.setUserid("fasfast");
		authInfo.setPassword("9312qrty!");
		request.setAuthorizationInfo(authInfo);
		TargetSchemaVersionUsed schema = new TargetSchemaVersionUsed();
		schema.setMajorVersionNumber(0);
		schema.setMinorVersionNumber(0);
		request.setTargetSchemaVersionUsed(schema);
		request.setTimeOutSecond(180);

		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponseDto);

		MainChangeTicketLoopRequest actualResponse = ivrCnfHelper.buildMainChangeTicketLoopRequest("12345678", "123",
				"321", "123", cnfSession, userSession, "12", "247");

		assertEquals("123", actualResponse.getInputData().getEmployeeId());
	}

	@Test
	void testProcessResponse_F1_3() throws JsonMappingException, JsonProcessingException {

		String mockLosDbJsonString = "mock-losdb-response-string";

		TNInfoResponse mockTnInfo = new TNInfoResponse();
		mockTnInfo.setTn("1234567890");
		mockTnInfo.setPrimaryNPA("123");
		mockTnInfo.setPrimaryNXX("456");

		IVRUserSession session = new IVRUserSession();

		session.setCanBePagedMobile(true);

		session.setLosDbResponse(mockLosDbJsonString);

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		session.setSessionId("123");

		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(mockLosDbJsonString)).thenReturn(mockTnInfo);

		IVRWebHookResponseDto actualResponse = ivrCnfHelper.processResponse(response, "F1", session);

		assertEquals("3", actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessResponse_F1_6() throws JsonMappingException, JsonProcessingException {

		String mockLosDbJsonString = "mock-losdb-response-string";

		TNInfoResponse mockTnInfo = new TNInfoResponse();
		mockTnInfo.setTn("1234567890");
		mockTnInfo.setPrimaryNPA("123");
		mockTnInfo.setPrimaryNXX("456");

		IVRUserSession session = new IVRUserSession();

		session.setCanBePagedMobile(false);

		session.setLosDbResponse(mockLosDbJsonString);

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		session.setSessionId("123");

		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(mockLosDbJsonString)).thenReturn(mockTnInfo);

		IVRWebHookResponseDto actualResponse = ivrCnfHelper.processResponse(response, "F1", session);

		assertEquals("6", actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessResponse_F1_5() throws JsonMappingException, JsonProcessingException {

		String mockLosDbJsonString = "mock-losdb-response-string";

		TNInfoResponse mockTnInfo = new TNInfoResponse();
		mockTnInfo.setTn("3034221414");
		mockTnInfo.setPrimaryNPA("303");
		mockTnInfo.setPrimaryNXX("534");

		IVRUserSession session = new IVRUserSession();
		session.setSessionId("123");
		session.setCanBePagedMobile(false);
		session.setLosDbResponse(mockLosDbJsonString);
		session.setNpaPrefix("303");

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId("123");

		IVRCnfEntity cnfSession = new IVRCnfEntity();
		cnfSession.setSessionId("123");

		String jsonRequestString = "mock-request-string";
		String mockLfacsResponse = "mock-lfacs-response";
		String mockLJsonResponse = "mock-lfacs-response";

		VerifyCutInformationRequest mockRequest = new VerifyCutInformationRequest();
		VerifyCutInformationResponse mockResponse = new VerifyCutInformationResponse();

		MessageStatus messageStatus = new MessageStatus();
		messageStatus.setErrorStatus("S");
		mockResponse.setMessageStatus(messageStatus);

		when(mockIvrCnfCacheService.getBySessionId("123")).thenReturn(cnfSession);
		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(mockLosDbJsonString)).thenReturn(mockTnInfo);
		when(mockIvrHttpClient.httpPostCall(any(), any(), any(), anyString())).thenReturn(mockLfacsResponse);
		when(mockIVRLfacsServiceHelper.cleanResponseString(mockLfacsResponse)).thenReturn(mockLJsonResponse);
		when(mockObjectMapper.readValue(mockLJsonResponse, VerifyCutInformationResponse.class))
				.thenReturn(mockResponse);

		IVRWebHookResponseDto actualResponse = ivrCnfHelper.processResponse(response, "F1", session);

		assertEquals("5", actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessResponse_F2_4() throws JsonMappingException, JsonProcessingException {

		String mockLosDbJsonString = "mock-losdb-response-string";

		TNInfoResponse mockTnInfo = new TNInfoResponse();
		mockTnInfo.setTn("1234567890");
		mockTnInfo.setPrimaryNPA("123");
		mockTnInfo.setPrimaryNXX("456");

		IVRUserSession session = new IVRUserSession();

		session.setCanBePagedMobile(true);

		session.setLosDbResponse(mockLosDbJsonString);

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		session.setSessionId("123");

		IVRWebHookResponseDto actualResponse = ivrCnfHelper.processResponse(response, "F2", session);

		assertEquals("4", actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessResponse_F2_5() throws JsonMappingException, JsonProcessingException {

		String mockLosDbJsonString = "mock-losdb-response-string";

		TNInfoResponse mockTnInfo = new TNInfoResponse();
		mockTnInfo.setTn("1234567890");
		mockTnInfo.setPrimaryNPA("123");
		mockTnInfo.setPrimaryNXX("456");

		IVRUserSession session = new IVRUserSession();

		session.setCanBePagedMobile(false);

		session.setLosDbResponse(mockLosDbJsonString);

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();

		session.setSessionId("123");

		IVRWebHookResponseDto actualResponse = ivrCnfHelper.processResponse(response, "F2", session);

		assertEquals("5", actualResponse.getHookReturnCode());
	}

	@Test
	void testBuildChangeLoopAssignmentRequest() throws JsonMappingException, JsonProcessingException {

		String sessionid = "session123";

		so.setORD("True");
		soList.add(so);
		loop.setCKID("763 757-4229");
		loop.setSO(soList);
		loop.setTID("DPAAB");
		loopList.add(loop);
		returnDataSet.setLoop(loopList);
		returnDataSet.setPort1("test");

		List<SEG> segList = new ArrayList<SEG>();
		SEG seg = new SEG();
		seg.setCTT("1");
		seg.setCA("ca1");
		seg.setPR("pr1");
		SEG seg2 = new SEG();
		seg.setCTT("2");
		seg.setCA("ca2");
		seg.setPR("pr2");
		SEG seg3 = new SEG();
		seg.setCTT("3");
		seg.setCA("ca3");
		seg.setPR("pr3");
		segList.add(seg);
		segList.add(seg2);
		segList.add(seg3);
		loop.setSEG(segList);
		loopList.add(loop);
		returnDataSet.setLoop(loopList);
		currentAssignmentResponseDto.setReturnDataSet(returnDataSet);

		IVRUserSession userSession = new IVRUserSession();
		userSession.setSessionId(sessionid);
		userSession.setCurrentAssignmentResponse("Test");

		IVRCnfEntity cnfSession = new IVRCnfEntity();
		cnfSession.setSessionId(sessionid);
		cnfSession.setSegmentRead("F1");
		cnfSession.setCable("1");
		cnfSession.setPair("3");

		ChangeLoopAssignmentRequestDto request = new ChangeLoopAssignmentRequestDto();

		CurrentAssignmentInfo currentAssignmentInfo = new CurrentAssignmentInfo();

		TN TN = new TN();

		TN.setCkid("12345678");

		currentAssignmentInfo.setTn(TN);

		ChangeLoopAssignmentRequestInputData id = new ChangeLoopAssignmentRequestInputData();

		id.setLFACSEmployeeCode("123");
		WireCtrPrimaryNPANXX wireCtrPrimaryNPANXX = new WireCtrPrimaryNPANXX();
		wireCtrPrimaryNPANXX.setNpa("123");
		wireCtrPrimaryNPANXX.setNxx("321");
		id.setWireCtrPrimaryNPANXX(wireCtrPrimaryNPANXX);

		id.setServiceOrderNumber(
				currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().get(0).getORD());
		id.setCircuitId(currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getCKID());

		id.setSegNumber("1");
		request.setInputData(id);

		ChangeLoopAssignmentReqInputDataCurrentLoopDetails currentLoopDetails = new ChangeLoopAssignmentReqInputDataCurrentLoopDetails();
		currentLoopDetails.setCableId(cnfSession.getCable());
		currentLoopDetails.setCableUnitId(cnfSession.getPair());
		id.setChangeLoopAssignmentReqInputDataCurrentLoopDetails(currentLoopDetails);
		request.setInputData(id);

		ChangeLoopAssignmentReqInputDataReplacementLoopDetails replacementLoopDetails = new ChangeLoopAssignmentReqInputDataReplacementLoopDetails();
		replacementLoopDetails.setCableId(cnfSession.getCable());
		replacementLoopDetails.setCableUnitId(cnfSession.getPair());
		id.setChangeLoopAssignmentReqInputDataReplacementLoopDetails(replacementLoopDetails);
		request.setInputData(id);

		id.setFacilityChangeReasonCode("NDC");
		id.setChangeActionCode("CUTFAINSTREP");
		request.setInputData(id);

//		request.setRequestId("FASTFAST");
//		request.setWebServiceName("SIABusService");
//		request.setRequestPurpose("TS");
//		AuthorizationInfo authInfo = new AuthorizationInfo();
//		authInfo.setUserid("fasfast");
//		authInfo.setPassword("9312qrty!");
//		request.setAuthorizationInfo(authInfo);
//		TargetSchemaVersionUsed schema = new TargetSchemaVersionUsed();
//		schema.setMajorVersionNumber(0);
//		schema.setMinorVersionNumber(0);
//		request.setTargetSchemaVersionUsed(schema);
//		request.setTimeOutSecond(180);

		when(mockObjectMapper.readValue(userSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class))
				.thenReturn(currentAssignmentResponseDto);

		ChangeLoopAssignmentRequestDto actualResponse = ivrCnfHelper.buildChangeLoopAssignmentRequest("12345678", "123",
				"321", "123", cnfSession.getSessionId(), cnfSession, userSession, "12", "247");

		assertEquals("123", actualResponse.getInputData().getLFACSEmployeeCode());
	}

	@Test
	void testBuildRetriveMainInqRequest() throws JsonMappingException, JsonProcessingException {

		RetrieveMaintenanceChangeTicketRequest request = new RetrieveMaintenanceChangeTicketRequest();

		String sessionid = "session123";

		IVRUserSession userSession = new IVRUserSession();
		userSession.setSessionId(sessionid);
		userSession.setCurrentAssignmentResponse("Test");

		IVRCnfEntity cnfSession = new IVRCnfEntity();
		cnfSession.setSessionId(sessionid);
		cnfSession.setSegmentRead("F1");
		cnfSession.setCable("1");
		cnfSession.setPair("3");

		WireCtrPrimaryNPANXX wireCtrPrimaryNPANXX = new WireCtrPrimaryNPANXX();
		wireCtrPrimaryNPANXX.setNpa("123");
		wireCtrPrimaryNPANXX.setNxx("456");

		RetMaintChangeTicketInputData inputData = new RetMaintChangeTicketInputData();
		inputData.setWireCtrPrimaryNPANXX(wireCtrPrimaryNPANXX);

		inputData.setCableId(cnfSession.getCable());
		inputData.setCableUnitId(cnfSession.getPair());

		inputData.setLFACSEmployeeCode("SIA");
		inputData.setLFACSEntity("A");

		request.setInputData(inputData);
//		request.setRequestId("FASTFAST");
//		request.setWebServiceName("SIABusService");
//		request.setRequestPurpose("TS");
//		AuthorizationInfo authInfo = new AuthorizationInfo();
//		authInfo.setUserid("fasfast");
//		authInfo.setPassword("9312qrty!");
//		request.setAuthorizationInfo(authInfo);
//		TargetSchemaVersionUsed schema = new TargetSchemaVersionUsed();
//		schema.setMajorVersionNumber(0);
//		schema.setMinorVersionNumber(0);
//		request.setTargetSchemaVersionUsed(schema);
//		request.setTimeOutSecond(180);

		RetrieveMaintenanceChangeTicketRequest actualResponse = ivrCnfHelper.buildRetriveMainInqRequest("123", "456",
				sessionid, cnfSession);

		assertEquals("SIA", actualResponse.getInputData().getLFACSEmployeeCode());
	}

	@Test
	void testRetrieveLoopAssignmentRequest() throws JsonMappingException, JsonProcessingException { // RetrieveLoopAssignmentRequest

		RetrieveLoopAssignmentRequest request = new RetrieveLoopAssignmentRequest();

		String sessionid = "session123";

		IVRUserSession userSession = new IVRUserSession();
		userSession.setSessionId(sessionid);
		userSession.setCurrentAssignmentResponse("Test");

		IVRCnfEntity cnfSession = new IVRCnfEntity();
		cnfSession.setSessionId(sessionid);
		cnfSession.setSegmentRead("F1");
		cnfSession.setCable("1");
		cnfSession.setPair("3");

		WireCtrPrimaryNPANXX wireCtrPrimaryNPANXX = new WireCtrPrimaryNPANXX();
		wireCtrPrimaryNPANXX.setNpa("123");
		wireCtrPrimaryNPANXX.setNxx("456");

		RetLoopAssigInputData inputData = new RetLoopAssigInputData();
		inputData.setWireCtrPrimaryNPANXX(wireCtrPrimaryNPANXX);

		inputData.setCableId(cnfSession.getCable());
		inputData.setCableUnitId(cnfSession.getPair());

		inputData.setLFACSEmployeeCode("SIA");
		inputData.setLFACSEntity("A");

		request.setInputData(inputData);
//		request.setRequestId("FASTFAST");
//		request.setWebServiceName("SIABusService");
//		request.setRequestPurpose("TS");
//		AuthorizationInfo authInfo = new AuthorizationInfo();
//		authInfo.setUserid("fasfast");
//		authInfo.setPassword("9312qrty!");
//		request.setAuthorizationInfo(authInfo);
//		TargetSchemaVersionUsed schema = new TargetSchemaVersionUsed();
//		schema.setMajorVersionNumber(0);
//		schema.setMinorVersionNumber(0);
//		request.setTargetSchemaVersionUsed(schema);
//		request.setTimeOutSecond(180);

		RetrieveLoopAssignmentRequest actualResponse = ivrCnfHelper.buildRetriveLoopAssignInqRequest(null, "123456", "123",
				"456", cnfSession);

		assertEquals("SIA", actualResponse.getInputData().getLFACSEmployeeCode());
	}
}
