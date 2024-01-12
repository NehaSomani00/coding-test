package com.lumen.fastivr.IVRLFACS;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumen.fastivr.IVRCacheManagement.IVRCacheService;
import com.lumen.fastivr.IVRDto.IVRParameter;
import com.lumen.fastivr.IVRDto.IVRWebHookResponseDto;
import com.lumen.fastivr.IVRDto.LOSDB.TNInfoResponse;
import com.lumen.fastivr.IVRDto.additionalLines.AdditionalLinesReportRequestDto;
import com.lumen.fastivr.IVRDto.additionalLines.AdditionalLinesReportResponseDto;
import com.lumen.fastivr.IVRSessionManagement.IVRUserSession;
import com.lumen.fastivr.IVRUtils.AdditionalLinesReportUtils;
import com.lumen.fastivr.IVRUtils.FormatUtilities;
import com.lumen.fastivr.IVRUtils.IVRConstants;
import com.lumen.fastivr.IVRUtils.IVRHookReturnCodes;
import com.lumen.fastivr.httpclient.IVRHttpClient;

@ExtendWith(MockitoExtension.class)
public class AdditionalLinesTest {
	
	@Mock
	private IVRCacheService mockCacheService;

	@Mock
	private IVRLfacsServiceHelper mockIVRLfacsServiceHelper;

	@Mock
	private ObjectMapper mockObjectMapper;

	@InjectMocks
	private IVRLfacsServiceImpl ivrLfacsServiceImpl;
	
	@Mock
	private AdditionalLinesReportUtils mockAdditionalLinesReportUtils;
	
	@Mock
	private IVRHttpClient mockIvrHttpClient;
	
	@Mock
	private AddlLinesPageBuilder mockAddlLinesPageBuilder;
	
	@Mock
	private FormatUtilities mockFormatUtilities;
	
	@BeforeEach
	void setUp() throws Exception {
		
	}
	
	@Test
	void testprocessAdditonalLinesVoiceFID635() {
		IVRUserSession session = new IVRUserSession();
		session.setAdditionalLinesCounter(1);
		when(mockCacheService.getBySessionId(anyString())).thenReturn(session);
		String hookReturnMessage = ivrLfacsServiceImpl.processAdditonalLinesVoiceFID635("session123").getHookReturnMessage();
		assertEquals(IVRConstants.FID635_HK_1_MSG, hookReturnMessage);
	}
	
	@Test
	void testprocessAdditonalLinesVoiceFID635_HK2() {
		IVRUserSession session = new IVRUserSession();
		session.setAdditionalLinesCounter(2);
		when(mockCacheService.getBySessionId(anyString())).thenReturn(session);
		String hookReturnMessage = ivrLfacsServiceImpl.processAdditonalLinesVoiceFID635("session123").getHookReturnMessage();
		assertEquals(IVRConstants.FID635_HK_2_MSG, hookReturnMessage);
	}
	
	@Test
	void testprocessFID35Code_InvalidSession()  {
		when(mockCacheService.getBySessionId(anyString())).thenReturn(null);
		String hookReturnMessage = ivrLfacsServiceImpl.processAdditonalLinesVoiceFID635("session123").getHookReturnMessage();
		assertEquals(IVRConstants.INVALID_SESSION_ID, hookReturnMessage);
	}
	
	@Test
	void testprocessFID600Code_NoServAddr() throws JsonMappingException, JsonProcessingException {
		IVRUserSession session = new IVRUserSession();
		session.setCanBePagedMobile(true);
		session.setCanBePagedEmail(false);
		session.setCurrentAssignmentResponse("curr-assg-resp");
		String losdb_resp_json = "losdb_resp_json";
		session.setLosDbResponse(losdb_resp_json);
		TNInfoResponse tnInfoResp = new TNInfoResponse();
		tnInfoResp.setTn("TN");

		when(mockCacheService.getBySessionId(anyString())).thenReturn(session);
		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(anyString())).thenReturn(tnInfoResp);
		when(mockIVRLfacsServiceHelper.checkIfServiceAddrExists(any())).thenReturn(IVRHookReturnCodes.HOOK_RETURN_0);
		
		String hookReturnMessage = ivrLfacsServiceImpl.processFID600Code("session123", IVRConstants.STATE_FID600).getHookReturnMessage();
		assertEquals(IVRConstants.SERVICE_ADDRESS_NOT_FOUND, hookReturnMessage);
		
	}
	
	@Test
	void testprocessFID600Code_ServAddr() throws JsonMappingException, JsonProcessingException {
		IVRUserSession session = new IVRUserSession();
		session.setCanBePagedMobile(true);
		session.setCanBePagedEmail(false);
		session.setCurrentAssignmentResponse("curr-assg-resp");
		String losdb_resp_json = "losdb_resp_json";
		session.setLosDbResponse(losdb_resp_json);
		TNInfoResponse tnInfoResp = new TNInfoResponse();
		tnInfoResp.setTn("TN");

		when(mockCacheService.getBySessionId(anyString())).thenReturn(session);
		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(anyString())).thenReturn(tnInfoResp);
		when(mockIVRLfacsServiceHelper.checkIfServiceAddrExists(any())).thenReturn(IVRHookReturnCodes.HOOK_RETURN_1);
		
		String hookReturnMessage = ivrLfacsServiceImpl.processFID600Code("session123", IVRConstants.STATE_FID600).getHookReturnMessage();
		assertEquals(IVRConstants.SERVICE_ADDRESS_FOUND, hookReturnMessage);
		
	}
	
	@Test
	void testprocessFID600Code_InvalidSession()  {

		when(mockCacheService.getBySessionId(anyString())).thenReturn(null);
		
		String hookReturnMessage = ivrLfacsServiceImpl.processFID600Code("session123", IVRConstants.STATE_FID600).getHookReturnMessage();
		assertEquals(IVRConstants.INVALID_SESSION_ID, hookReturnMessage);
	}
	
	@Test
	void testprocessFID080Code_mobile() throws JsonMappingException, JsonProcessingException {
		IVRUserSession session = new IVRUserSession();
		session.setSessionId("session123");
		session.setCanBePagedMobile(true);
		session.setCanBePagedEmail(false);
		session.setCurrentAssignmentResponse("curr-assg-resp");
		String losdb_resp_json = "losdb_resp_json";
		session.setLosDbResponse(losdb_resp_json);
		TNInfoResponse tnInfoResp = new TNInfoResponse();
		tnInfoResp.setPrimaryNPA("npa");
		tnInfoResp.setPrimaryNXX("nxx");
		tnInfoResp.setTn("TN");
		session.setInquiredTn("TN");
		String serviceAddress = "serviceAddress";
		AdditionalLinesReportRequestDto request = new AdditionalLinesReportRequestDto();
		String requestString = "requestString";

		when(mockCacheService.getBySessionId(anyString())).thenReturn(session);
		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(anyString())).thenReturn(tnInfoResp);
		when(mockAdditionalLinesReportUtils.getFormattedServiceAddress(anyString(), anyString()))
				.thenReturn(serviceAddress);
		when(mockIVRLfacsServiceHelper.buildAdditionalLinesReportRequest(anyString(), anyString(), anyString(),
				anyString())).thenReturn(request);
		when(mockObjectMapper.writeValueAsString(request)).thenReturn(requestString);
		when(mockIvrHttpClient.httpPostCall(anyString(), any(), anyString(), anyString())).thenReturn(requestString);
		when(mockIvrHttpClient.cleanResponseString(anyString())).thenReturn(requestString);
		AdditionalLinesReportResponseDto respDto = new AdditionalLinesReportResponseDto();
		when(mockObjectMapper.readValue(requestString, AdditionalLinesReportResponseDto.class)).thenReturn(respDto );
		doNothing().when(mockAddlLinesPageBuilder).processAddlLinesResponse(any(), any(), any(), any());
		
		String hookReturnCode = ivrLfacsServiceImpl.processFID080Code("session123", IVRConstants.STATE_FID080).getHookReturnCode();
		assertEquals(IVRHookReturnCodes.HOOK_RETURN_3, hookReturnCode);
	}
	
	@Test
	void testprocessFID080Code_mail() throws JsonMappingException, JsonProcessingException {
		IVRUserSession session = new IVRUserSession();
		session.setSessionId("session123");
		session.setCanBePagedMobile(false);
		session.setCanBePagedEmail(true);
		session.setCurrentAssignmentResponse("curr-assg-resp");
		String losdb_resp_json = "losdb_resp_json";
		session.setLosDbResponse(losdb_resp_json);
		TNInfoResponse tnInfoResp = new TNInfoResponse();
		tnInfoResp.setPrimaryNPA("npa");
		tnInfoResp.setPrimaryNXX("nxx");
		tnInfoResp.setTn("TN");
		session.setInquiredTn("TN");
		String serviceAddress = "serviceAddress";
		AdditionalLinesReportRequestDto request = new AdditionalLinesReportRequestDto();
		String requestString = "requestString";

		when(mockCacheService.getBySessionId(anyString())).thenReturn(session);
		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(anyString())).thenReturn(tnInfoResp);
		when(mockAdditionalLinesReportUtils.getFormattedServiceAddress(anyString(), anyString()))
				.thenReturn(serviceAddress);
		when(mockIVRLfacsServiceHelper.buildAdditionalLinesReportRequest(anyString(), anyString(), anyString(),
				anyString())).thenReturn(request);
		when(mockObjectMapper.writeValueAsString(request)).thenReturn(requestString);
		when(mockIvrHttpClient.httpPostCall(anyString(), any(), anyString(), anyString())).thenReturn(requestString);
		when(mockIvrHttpClient.cleanResponseString(anyString())).thenReturn(requestString);
		AdditionalLinesReportResponseDto respDto = new AdditionalLinesReportResponseDto();
		when(mockObjectMapper.readValue(requestString, AdditionalLinesReportResponseDto.class)).thenReturn(respDto );
		//when(mockFormatUtilities.FormatTelephoneNNNXXXX("TN")).thenReturn("");
		doNothing().when(mockAddlLinesPageBuilder).processAddlLinesResponse(any(), any(), any(), any());
		
		String hookReturnCode = ivrLfacsServiceImpl.processFID080Code("session123", IVRConstants.STATE_FID080).getHookReturnCode();
		assertEquals(IVRHookReturnCodes.HOOK_RETURN_2, hookReturnCode);
	}
	
	@Test
	void testprocessFID080Code_InvalidSession()  {

		when(mockCacheService.getBySessionId(anyString())).thenReturn(null);
		
		String hookReturnMessage = ivrLfacsServiceImpl.processFID080Code("session123", IVRConstants.STATE_FID080).getHookReturnMessage();
		assertEquals(IVRConstants.INVALID_SESSION_ID, hookReturnMessage);
	}
	
	@Test
	void testProcessFID285Code_HookCode3() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID285";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		IVRUserSession userSession = new IVRUserSession();
		userSession.setSessionId(sessionId);
		userSession.setCanBePagedMobile(true);
		userSession.setLosDbResponse("los-db-response");
		userSession.setCurrentAssignmentResponse("curr-assg-resp");
		String serviceAddress = "NO=1315, ST 1234, 12 ROOM";
		TNInfoResponse tnResponse = new TNInfoResponse();
		tnResponse.setPrimaryNPA("NPA");
		tnResponse.setPrimaryNXX("NXX");
		tnResponse.setTn("TN");
		userSession.setInquiredTn("TN");
		AdditionalLinesReportRequestDto addlinesRrpt = new AdditionalLinesReportRequestDto();
		AdditionalLinesReportResponseDto addLinesResp = new AdditionalLinesReportResponseDto();

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(userSession.getLosDbResponse()))
				.thenReturn(tnResponse);
		when(mockAdditionalLinesReportUtils.getFormattedServiceAddress(anyString(), anyString()))
				.thenReturn(serviceAddress);
		when(mockIVRLfacsServiceHelper.buildAdditionalLinesReportRequest(anyString(), anyString(), anyString(),
				anyString())).thenReturn(addlinesRrpt);
		String jsonString = "addl-json-str";
		when(mockObjectMapper.writeValueAsString(addlinesRrpt)).thenReturn(jsonString);
		when(mockIvrHttpClient.httpPostCall(anyString(), any(), anyString(), anyString())).thenReturn(jsonString);
		when(mockIvrHttpClient.cleanResponseString(anyString())).thenReturn(jsonString);
		when(mockObjectMapper.readValue(jsonString, AdditionalLinesReportResponseDto.class)).thenReturn(addLinesResp);
		doNothing().when(mockAddlLinesPageBuilder).processAddlLinesResponse(any(), any(), any(), any());
		
		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID285Code(sessionId, currentState);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_3, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID285Code_HookCode2() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID285";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		IVRUserSession userSession = new IVRUserSession();
		userSession.setSessionId(sessionId);
		userSession.setCanBePagedMobile(false);
		userSession.setCanBePagedEmail(true);
		userSession.setLosDbResponse("los-db-response");
		userSession.setCurrentAssignmentResponse("curr-assg-resp");
		userSession.setInquiredTn("TN");
		String serviceAddress = "NO=1315, ST 1234, 12 ROOM";
		TNInfoResponse tnResponse = new TNInfoResponse();
		tnResponse.setPrimaryNPA("NPA");
		tnResponse.setPrimaryNXX("NXX");
		tnResponse.setTn("TN");
		AdditionalLinesReportRequestDto addlinesRrpt = new AdditionalLinesReportRequestDto();
		AdditionalLinesReportResponseDto addLinesResp = new AdditionalLinesReportResponseDto();

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(userSession.getLosDbResponse()))
				.thenReturn(tnResponse);
		when(mockAdditionalLinesReportUtils.getFormattedServiceAddress(anyString(), anyString()))
				.thenReturn(serviceAddress);
		when(mockIVRLfacsServiceHelper.buildAdditionalLinesReportRequest(anyString(), anyString(), anyString(),
				anyString())).thenReturn(addlinesRrpt);
		String jsonString = "addl-json-str";
		when(mockObjectMapper.writeValueAsString(addlinesRrpt)).thenReturn(jsonString);
		when(mockIvrHttpClient.httpPostCall(anyString(), any(), anyString(), anyString())).thenReturn(jsonString);
		when(mockIvrHttpClient.cleanResponseString(anyString())).thenReturn(jsonString);
		when(mockObjectMapper.readValue(jsonString, AdditionalLinesReportResponseDto.class)).thenReturn(addLinesResp);
		doNothing().when(mockAddlLinesPageBuilder).processAddlLinesResponse(any(), any(), any(), any());
		
		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID285Code(sessionId, currentState);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_2, actualResponse.getHookReturnCode());
	}

	@Test
	void testProcessFID285Code_InvalidSession() {

		String sessionId = "session123";
		String currentState = "FID285";

		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(null);
		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID285Code(sessionId, currentState);

		assertEquals(IVRConstants.INVALID_SESSION_ID, actualResponse.getHookReturnMessage());
	}
	
	@Test
	void testprocessAdditonalLinesVoiceFID615_HK8() throws JsonMappingException, JsonProcessingException {
		IVRUserSession session = new IVRUserSession();
		AdditionalLinesReportResponseDto addlRespDto = new AdditionalLinesReportResponseDto();
		String addlRespJson = "addl-lines-json";
		session.setAdditionalLinesResponse(addlRespJson);
		List<String> mockList = new ArrayList<>();
		mockList.add("");
		addlRespDto.setReturnDataSet(mockList);

		when(mockCacheService.getBySessionId(anyString())).thenReturn(session);
		when(mockObjectMapper.readValue(addlRespJson, AdditionalLinesReportResponseDto.class)).thenReturn(addlRespDto);
		when(mockIVRLfacsServiceHelper.getNumberOfAddlLines(addlRespDto)).thenReturn(2);
		//when(mockIVRLfacsServiceHelper.getAdditionalLinesExceptInquiredTN(any(), anyList())).thenReturn(mockList);

		String hookReturnMessage = ivrLfacsServiceImpl
				.processAdditonalLinesVoiceFID615("session123", IVRConstants.STATE_FID615).getHookReturnMessage();
		
		assertEquals(IVRConstants.FID615_MSG_8, hookReturnMessage);
	}
	
	@Test
	void testprocessAdditonalLinesVoiceFID615_HK5() throws JsonMappingException, JsonProcessingException {
		IVRUserSession session = new IVRUserSession();
		AdditionalLinesReportResponseDto addlRespDto = new AdditionalLinesReportResponseDto();
		String addlRespJson = "addl-lines-json";
		session.setAdditionalLinesResponse(addlRespJson);
		List<String> mockList = new ArrayList<>();
		addlRespDto.setReturnDataSet(mockList);

		when(mockCacheService.getBySessionId(anyString())).thenReturn(session);
		when(mockObjectMapper.readValue(addlRespJson, AdditionalLinesReportResponseDto.class)).thenReturn(addlRespDto);
		when(mockIVRLfacsServiceHelper.getNumberOfAddlLines(addlRespDto)).thenReturn(0);
		//when(mockIVRLfacsServiceHelper.getAdditionalLinesExceptInquiredTN(any(), anyList())).thenReturn(mockList);

		String hookReturnMessage = ivrLfacsServiceImpl
				.processAdditonalLinesVoiceFID615("session123", IVRConstants.STATE_FID615).getHookReturnMessage();
		
		assertEquals(IVRConstants.FID615_MSG_5, hookReturnMessage);
	}
	
	@Test
	void testprocessAdditonalLinesVoiceFID615_InvalidSession() throws JsonMappingException, JsonProcessingException {
		when(mockCacheService.getBySessionId(anyString())).thenReturn(null);

		String hookReturnMessage = ivrLfacsServiceImpl
				.processAdditonalLinesVoiceFID615("session123", IVRConstants.STATE_FID615).getHookReturnMessage();
		
		assertEquals(IVRConstants.INVALID_SESSION_ID, hookReturnMessage);
	}
	
	@Test
	void testprocessAdditonalLinesVoiceFID625_HK1() throws JsonMappingException, JsonProcessingException {
		IVRUserSession session = new IVRUserSession();
		AdditionalLinesReportResponseDto addlRespDto = new AdditionalLinesReportResponseDto();
		
		String addlRespJson = "addl-lines-json";
		session.setAdditionalLinesResponse(addlRespJson);
		
		List<String> list = new ArrayList<>();
		list.add("InquiredTN");
		list.add("Addl Lines1");
		addlRespDto.setReturnDataSet(list);
		
		when(mockCacheService.getBySessionId(anyString())).thenReturn(session);
		when(mockObjectMapper.readValue(addlRespJson, AdditionalLinesReportResponseDto.class)).thenReturn(addlRespDto);
		when(mockIVRLfacsServiceHelper.addParamterData(anyString())).thenReturn(new ArrayList<IVRParameter>());
		when(mockIVRLfacsServiceHelper.getNumberOfAddlLines(any())).thenReturn(list.size()-1);		
		String hookReturnMessage = ivrLfacsServiceImpl
				.processAdditonalLinesVoiceFID625("session123", IVRConstants.STATE_FID625).getHookReturnMessage();
		
		assertEquals(IVRConstants.FID625_MSG_1, hookReturnMessage);
	}
	
	@Test
	void testprocessAdditonalLinesVoiceFID625_HK2() throws JsonMappingException, JsonProcessingException {
		IVRUserSession session = new IVRUserSession();
		AdditionalLinesReportResponseDto addlRespDto = new AdditionalLinesReportResponseDto();
		
		String addlRespJson = "addl-lines-json";
		session.setAdditionalLinesResponse(addlRespJson);
		
		List<String> list = new ArrayList<>();
		list.add("InquiredTN");
		list.add("Addl Lines1");
		list.add("Addl Lines2");
		addlRespDto.setReturnDataSet(list);
		
		when(mockCacheService.getBySessionId(anyString())).thenReturn(session);
		when(mockObjectMapper.readValue(addlRespJson, AdditionalLinesReportResponseDto.class)).thenReturn(addlRespDto);
		when(mockIVRLfacsServiceHelper.getNumberOfAddlLines(any())).thenReturn(list.size()-1);	
		when(mockIVRLfacsServiceHelper.addParamterData(anyString())).thenReturn(new ArrayList<IVRParameter>());
		
		String hookReturnMessage = ivrLfacsServiceImpl
				.processAdditonalLinesVoiceFID625("session123", IVRConstants.STATE_FID625).getHookReturnMessage();
		
		assertEquals(IVRConstants.FID625_MSG_2, hookReturnMessage);
	}
	
	
	
}
