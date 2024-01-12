package com.lumen.fastivr.IVRLFACS;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

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
import com.lumen.fastivr.IVRDto.CurrentAssignmentResponseDto;
import com.lumen.fastivr.IVRDto.IVRWebHookResponseDto;
import com.lumen.fastivr.IVRDto.LOSDB.TNInfoResponse;
import com.lumen.fastivr.IVRDto.multipleappearance.MultipleAppearanceResponseDto;
import com.lumen.fastivr.IVRSessionManagement.IVRUserSession;
import com.lumen.fastivr.IVRUtils.IVRHookReturnCodes;
import com.lumen.fastivr.httpclient.IVRHttpClient;

/**
 * 
 */
@ExtendWith(MockitoExtension.class)
public class LFACSProcessFID070Test {

	@Mock
	private IVRCacheService mockCacheService;

	@Mock
	private IVRLfacsServiceHelper mockIVRLfacsServiceHelper;

	@InjectMocks
	private IVRLfacsServiceImpl ivrLfacsServiceImpl;

	@InjectMocks
	private IVRCacheService cacheService;

	@Mock
	private IVRHttpClient ivrHttpClient;

	@Mock
	private ObjectMapper objectMapper;
	
	@Mock
	private IVRLfacsPagerTextFormation ivrLfacsPagerText;
	
	private IVRUserSession mockSession;

	private String mockLfacsResponse = "{\"ReturnDataSet\":{\"TerminalDetail\":[{\"TerminalAddress\":\"X 10000 PALM ST NW\",\"TerminalType\":\"FIXD\",\"CandidatePairStatus\":\"*\",\"BindingPostColorCode\":\"424\"}],\"AdditionalTerminalsFlag\":false}}";
	private CurrentAssignmentResponseDto currentAssignmentResponseDto;
	private MultipleAppearanceResponseDto multipleAppearanceResponseDto;

	@BeforeEach
	public void initDetails() throws JsonMappingException, JsonProcessingException {
		mockSession = new IVRUserSession();
		mockSession.setSessionId("session123");
		mockSession.setEc("999");

		mockSession.setLosDbResponse("mock-losdb-response-string");
		mockSession.setCurrentAssignmentResponse(
				"{\"ReturnDataSet\":{\"LOOP\":[{\"SEG\":[{\"SEGNO\":\"2\",\"CA\":\"DSL1822\",\"PR\":\"128\"},{\"SEGNO\":\"3\",\"CA\":\"DSL1822\",\"PR\":\"128\"},{\"SEGNO\":\"4\",\"CA\":\"DSL1822\",\"PR\":\"128\"}]}]},\"RequestId\":\"FASTFAST\",\"WebServiceName\":\"SIABusService\",\"CompletedTimeStampSpecified\":true}");
		currentAssignmentResponseDto = new ObjectMapper().readValue(mockSession.getCurrentAssignmentResponse(),
				CurrentAssignmentResponseDto.class);

		multipleAppearanceResponseDto = new ObjectMapper().readValue(mockLfacsResponse,
				MultipleAppearanceResponseDto.class);
	}

	
	@Test
	void shouldReturnHookReturnCodeTwo() throws JsonMappingException, JsonProcessingException {
		
		String sessionId = "session123";
		String currentState = "FID070";
		
		TNInfoResponse mockTnInfo = new TNInfoResponse();
		mockTnInfo.setTn("1234567890");
		mockTnInfo.setPrimaryNPA("123");
		mockTnInfo.setPrimaryNXX("456");
		
		// mocking
		when(mockCacheService.getBySessionId(sessionId)).thenReturn(mockSession);
		
		when(objectMapper.readValue(eq(mockSession.getCurrentAssignmentResponse()),
				eq(CurrentAssignmentResponseDto.class))).thenReturn(currentAssignmentResponseDto);
		when(objectMapper.readValue(eq(mockLfacsResponse), eq(MultipleAppearanceResponseDto.class)))
		.thenReturn(multipleAppearanceResponseDto);
		
		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(mockSession.getLosDbResponse()))
		.thenReturn(mockTnInfo);
		
		when(ivrHttpClient.httpPostCall(any(), any(), any(), anyString())).thenReturn(mockLfacsResponse);
		when(ivrHttpClient.cleanResponseString(mockLfacsResponse)).thenReturn(mockLfacsResponse);
		when(mockCacheService.updateSession(mockSession)).thenReturn(mockSession);
		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID070Code(sessionId, currentState, 1);
		
		assertEquals(IVRHookReturnCodes.HOOK_RETURN_2, actualResponse.getHookReturnCode());
		
	}
	
	@Test
	void shouldReturnHookReturnCodeThree() throws JsonMappingException, JsonProcessingException {
		
		String sessionId = "session123";
		String currentState = "FID070";
		mockSession.setCanBePagedMobile(true);
		
		TNInfoResponse mockTnInfo = new TNInfoResponse();
		mockTnInfo.setTn("1234567890");
		mockTnInfo.setPrimaryNPA("123");
		mockTnInfo.setPrimaryNXX("456");
		
		// mocking
		when(mockCacheService.getBySessionId(sessionId)).thenReturn(mockSession);
		
		when(objectMapper.readValue(eq(mockSession.getCurrentAssignmentResponse()),
				eq(CurrentAssignmentResponseDto.class))).thenReturn(currentAssignmentResponseDto);
		when(objectMapper.readValue(eq(mockLfacsResponse), eq(MultipleAppearanceResponseDto.class)))
		.thenReturn(multipleAppearanceResponseDto);
		
		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(mockSession.getLosDbResponse()))
		.thenReturn(mockTnInfo);
		
		when(ivrHttpClient.httpPostCall(any(), any(), any(), anyString())).thenReturn(mockLfacsResponse);
		when(ivrHttpClient.cleanResponseString(mockLfacsResponse)).thenReturn(mockLfacsResponse);
		when(ivrLfacsPagerText.getPageMultipleAppearences(any(), any(), any(),anyInt())).thenReturn("test");
		when(mockIVRLfacsServiceHelper.sendTestResultToTech(any(), any(), any(),any())).thenReturn(true);
		
		when(mockCacheService.updateSession(mockSession)).thenReturn(mockSession);
		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID070Code(sessionId, currentState, 2);
		
		assertEquals(IVRHookReturnCodes.HOOK_RETURN_3, actualResponse.getHookReturnCode());
		
	}
	
	@Test
	void shouldReturnHookReturnCodeFour() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentState = "FID070";

		TNInfoResponse mockTnInfo = new TNInfoResponse();
		mockTnInfo.setTn("1234567890");
		mockTnInfo.setPrimaryNPA("123");
		mockTnInfo.setPrimaryNXX("456");

		// mocking
		when(mockCacheService.getBySessionId(sessionId)).thenReturn(mockSession);

		when(objectMapper.readValue(eq(mockSession.getCurrentAssignmentResponse()),
				eq(CurrentAssignmentResponseDto.class))).thenReturn(currentAssignmentResponseDto);
		when(objectMapper.readValue(eq(mockLfacsResponse), eq(MultipleAppearanceResponseDto.class)))
				.thenReturn(multipleAppearanceResponseDto);

		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(mockSession.getLosDbResponse()))
				.thenReturn(mockTnInfo);

		when(ivrHttpClient.httpPostCall(any(), any(), any(), anyString())).thenReturn(mockLfacsResponse);
		when(ivrHttpClient.cleanResponseString(mockLfacsResponse)).thenReturn(mockLfacsResponse);
		when(mockCacheService.updateSession(mockSession)).thenReturn(mockSession);
		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID070Code(sessionId, currentState, 3);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_4, actualResponse.getHookReturnCode());

	}

	@Test
	void shouldReturnHookReturnCodeOneAsLOSDbResponseIsNull() throws JsonMappingException, JsonProcessingException {
		String sessionId = "session123";
		String currentState = "FID070";
		
		when(mockCacheService.getBySessionId(sessionId)).thenReturn(mockSession);

		when(objectMapper.readValue(eq(mockSession.getCurrentAssignmentResponse()),
				eq(CurrentAssignmentResponseDto.class))).thenReturn(currentAssignmentResponseDto);
		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(mockSession.getLosDbResponse()))
		.thenReturn(null);
		
		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID070Code(sessionId, currentState, 1);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_1, actualResponse.getHookReturnCode());

		//RuntimeException runTm = Assertions.assertThrows(BusinessException.class, () -> {ivrLfacsServiceImpl.processFID070Code(sessionId, currentState, 1);},"Runtime Exception Expected");
		//assertEquals(IVRConstants.CANNOT_FETCH_MULTI_APPEARANCE_API_LOSDB_NULL,runTm.getMessage());
	}
	@Test
	void shouldReturnHookReturnCodeOneAsUserSessionIsNull() throws JsonMappingException, JsonProcessingException {
		String sessionId = "session123";
		String currentState = "FID070";
		
		when(mockCacheService.getBySessionId(sessionId)).thenReturn(null);
		
		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID070Code(sessionId, currentState, 1);
		
		assertEquals(IVRHookReturnCodes.HOOK_RETURN_1, actualResponse.getHookReturnCode());
		
	}

}
