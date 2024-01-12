package com.lumen.fastivr.IVRLFACS;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.net.http.HttpTimeoutException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumen.fastivr.IVRCacheManagement.IVRCacheService;
import com.lumen.fastivr.IVRDto.IVRWebHookResponseDto;
import com.lumen.fastivr.IVRDto.LOSDB.TNInfoResponse;
import com.lumen.fastivr.IVRDto.coe.CentralOfficeEquipmentResponseDto;
import com.lumen.fastivr.IVRDto.common.IVRHttpResponseDto;
import com.lumen.fastivr.IVRSessionManagement.IVRUserSession;
import com.lumen.fastivr.IVRUtils.IVRConstants;
import com.lumen.fastivr.IVRUtils.IVRHookReturnCodes;
import com.lumen.fastivr.httpclient.IVRHttpClient;

@ExtendWith(MockitoExtension.class)
public class LFACSProcessFID090Test {

	@Mock
	private IVRCacheService mockCacheService;

	@Mock
	private IVRLfacsServiceHelper mockIVRLfacsServiceHelper;

	@InjectMocks
	private IVRLfacsServiceImpl ivrLfacsServiceImpl;
	
	@Mock
	private IVRLfacsPagerTextFormation mockIVRLfacsPagerText;

	@InjectMocks
	private IVRCacheService cacheService;

	@Mock
	private IVRHttpClient ivrHttpClient;

	@Mock
	private ObjectMapper objectMapper;

	private IVRUserSession mockSession;

	private String mockLfacsResponse = "{\"ReturnDataSet\":{\"SWITCHNetworkUnitId\":\"1013-008-09-16E\"},\"MessageStatus\":{\"ErrorCode\":\"\",\"ErrorMessage\":\"\",\"SeverityLevel\":\"\",\"ErrorStatus\":\"S\"}}";
	private CentralOfficeEquipmentResponseDto centralOfficeEquipmentResponseDto;

	@BeforeEach
	public void initDetails() throws JsonMappingException, JsonProcessingException {
		mockSession = new IVRUserSession();
		mockSession.setSessionId("session123");
		mockSession.setEc("999");

		mockSession.setLosDbResponse("mock-losdb-response-string");

		centralOfficeEquipmentResponseDto = new ObjectMapper().readValue(mockLfacsResponse,
				CentralOfficeEquipmentResponseDto.class);
	}
	
	@Test
	void shouldReturnHookReturnCodeThreeAndCurrentStateFID090() throws JsonMappingException, JsonProcessingException, HttpTimeoutException {

		String sessionId = "session123";
		String currentState = "FID090";

		mockSession.setCanBePagedEmail(true);
		mockSession.setCanBePagedMobile(true);
		
		TNInfoResponse mockTnInfo = new TNInfoResponse();
		mockTnInfo.setTn("1234567890");
		mockTnInfo.setPrimaryNPA("123");
		mockTnInfo.setPrimaryNXX("456");

		IVRHttpResponseDto responseDto = new IVRHttpResponseDto(HttpStatus.OK.value(), mockLfacsResponse);
		// mocking
		when(mockCacheService.getBySessionId(sessionId)).thenReturn(mockSession);

		when(ivrHttpClient.cleanResponseString(responseDto.getResponseBody())).thenReturn(mockLfacsResponse);
		when(objectMapper.readValue(eq(mockLfacsResponse), eq(CentralOfficeEquipmentResponseDto.class)))
				.thenReturn(centralOfficeEquipmentResponseDto);

		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(mockSession.getLosDbResponse()))
				.thenReturn(mockTnInfo);
		
		when(mockIVRLfacsServiceHelper.sendTestResultToTech(any(),any(),any(),any())).thenReturn(true);

		when(ivrHttpClient.httpPostApiCall(any(), any(), any(), anyString())).thenReturn(responseDto);
		
		when(mockCacheService.updateSession(mockSession)).thenReturn(mockSession);
		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID090Code(sessionId, currentState);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_3, actualResponse.getHookReturnCode());
		//assertEquals(IVRConstants.STATE_FID700, actualResponse.getCurrentState());
		assertEquals(IVRConstants.STATE_FID090, actualResponse.getCurrentState());

	}
	
	@Test
	void shouldReturnHookReturnCodeEightAndCurrentStateFID700() throws JsonMappingException, JsonProcessingException, HttpTimeoutException {
		
		String sessionId = "session123";
		String currentState = "FID090";
		
		mockSession.setCanBePagedEmail(true);
		mockSession.setCanBePagedMobile(false);
		
		TNInfoResponse mockTnInfo = new TNInfoResponse();
		mockTnInfo.setTn("1234567890");
		mockTnInfo.setPrimaryNPA("123");
		mockTnInfo.setPrimaryNXX("456");
		
		IVRHttpResponseDto responseDto = new IVRHttpResponseDto(HttpStatus.OK.value(), mockLfacsResponse);
		// mocking
		when(mockCacheService.getBySessionId(sessionId)).thenReturn(mockSession);

		when(ivrHttpClient.cleanResponseString(responseDto.getResponseBody())).thenReturn(mockLfacsResponse);
		when(objectMapper.readValue(eq(mockLfacsResponse), eq(CentralOfficeEquipmentResponseDto.class)))
				.thenReturn(centralOfficeEquipmentResponseDto);

		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(mockSession.getLosDbResponse()))
				.thenReturn(mockTnInfo);
		
		when(mockIVRLfacsServiceHelper.sendTestResultToTech(any(),any(),any(),any())).thenReturn(true);

		when(ivrHttpClient.httpPostApiCall(any(), any(), any(), anyString())).thenReturn(responseDto);
		
		when(mockCacheService.updateSession(mockSession)).thenReturn(mockSession);
		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID090Code(sessionId, currentState);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_8, actualResponse.getHookReturnCode());
		assertEquals(IVRConstants.STATE_FID700, actualResponse.getCurrentState());
		
	}
	
	@Test
	void shouldReturnHookReturnCodeFiveAndCurrentStateFID700AsSwitchNetworkUnitIdIsNull() throws JsonMappingException, JsonProcessingException, HttpTimeoutException {
		String mockLfacsResponseWithEmptySWITCHNetworkUnitId = "{\"ReturnDataSet\":{\"SWITCHNetworkUnitId\":\"\"},\"MessageStatus\":{\"ErrorCode\":\"\",\"ErrorMessage\":\"\",\"SeverityLevel\":\"\",\"ErrorStatus\":\"S\"}}";
		
		String sessionId = "session123";
		String currentState = "FID700";
		
		mockSession.setCanBePagedEmail(true);
		mockSession.setCanBePagedMobile(false);
		
		TNInfoResponse mockTnInfo = new TNInfoResponse();
		mockTnInfo.setTn("1234567890");
		mockTnInfo.setPrimaryNPA("123");
		mockTnInfo.setPrimaryNXX("456");
		
		IVRHttpResponseDto responseDto = new IVRHttpResponseDto(HttpStatus.OK.value(), mockLfacsResponseWithEmptySWITCHNetworkUnitId);
		// mocking
		when(mockCacheService.getBySessionId(sessionId)).thenReturn(mockSession);

		when(ivrHttpClient.cleanResponseString(responseDto.getResponseBody())).thenReturn(mockLfacsResponseWithEmptySWITCHNetworkUnitId);
		when(objectMapper.readValue(eq(mockLfacsResponseWithEmptySWITCHNetworkUnitId), eq(CentralOfficeEquipmentResponseDto.class)))
				.thenReturn(new ObjectMapper().readValue(mockLfacsResponseWithEmptySWITCHNetworkUnitId,
						CentralOfficeEquipmentResponseDto.class));

		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(mockSession.getLosDbResponse()))
				.thenReturn(mockTnInfo);
		
		when(mockIVRLfacsServiceHelper.sendTestResultToTech(any(),any(),any(),any())).thenReturn(true);

		when(ivrHttpClient.httpPostApiCall(any(), any(), any(), anyString())).thenReturn(responseDto);
		
		when(mockIVRLfacsPagerText.getCentralOfficePageText(any(), any(), any())).thenReturn("");
		
		when(mockCacheService.updateSession(mockSession)).thenReturn(mockSession);
		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID090Code(sessionId, currentState);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_5, actualResponse.getHookReturnCode());
		assertEquals(IVRConstants.STATE_FID700, actualResponse.getCurrentState());
		
	}
	
	@Test
	void shouldReturnHookReturnCodeTwoAndCurrentStateFID700() throws JsonMappingException, JsonProcessingException, HttpTimeoutException {
		
		String sessionId = "session123";
		String currentState = "FID700";
		
		mockSession.setCanBePagedEmail(true);
		mockSession.setCanBePagedMobile(false);
		
		TNInfoResponse mockTnInfo = new TNInfoResponse();
		mockTnInfo.setTn("1234567890");
		mockTnInfo.setPrimaryNPA("123");
		mockTnInfo.setPrimaryNXX("456");
		
		// mocking
		when(mockCacheService.getBySessionId(sessionId)).thenReturn(mockSession);

		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(mockSession.getLosDbResponse()))
				.thenReturn(mockTnInfo);
		

		when(ivrHttpClient.httpPostApiCall(any(), any(), any(), anyString())).thenThrow(HttpTimeoutException.class);
		
		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID090Code(sessionId, currentState);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_2, actualResponse.getHookReturnCode());
		assertEquals(IVRConstants.STATE_FID700, actualResponse.getCurrentState());
		
	}
	
	@Test
	void shouldReturnHookReturnCodeFourAndCurrentStateFID700AsAPICallThrowsIllegalArgsExcp() throws JsonMappingException, JsonProcessingException, HttpTimeoutException {
		
		String sessionId = "session123";
		String currentState = "FID700";
		
		mockSession.setCanBePagedEmail(true);
		mockSession.setCanBePagedMobile(false);
		
		TNInfoResponse mockTnInfo = new TNInfoResponse();
		mockTnInfo.setTn("1234567890");
		mockTnInfo.setPrimaryNPA("123");
		mockTnInfo.setPrimaryNXX("456");
		
		IVRHttpResponseDto responseDto = new IVRHttpResponseDto(HttpStatus.BAD_REQUEST.value(), mockLfacsResponse);
		// mocking
		when(mockCacheService.getBySessionId(sessionId)).thenReturn(mockSession);


		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(mockSession.getLosDbResponse()))
				.thenReturn(mockTnInfo);
		
		when(ivrHttpClient.httpPostApiCall(any(), any(), any(), anyString())).thenReturn(responseDto);
		
		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID090Code(sessionId, currentState);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_4, actualResponse.getHookReturnCode());
		assertEquals(IVRConstants.STATE_FID700, actualResponse.getCurrentState());
		
	}
	
	@Test
	void shouldReturnHookReturnCodeThreeAndCurrentStateFID700AsAPICallThrowsIntrptedExcp() throws JsonMappingException, JsonProcessingException, HttpTimeoutException {
		
		String sessionId = "session123";
		String currentState = "FID700";
		
		mockSession.setCanBePagedEmail(true);
		mockSession.setCanBePagedMobile(false);
		
		TNInfoResponse mockTnInfo = new TNInfoResponse();
		mockTnInfo.setTn("1234567890");
		mockTnInfo.setPrimaryNPA("123");
		mockTnInfo.setPrimaryNXX("456");
		
		IVRHttpResponseDto responseDto = new IVRHttpResponseDto(HttpStatus.BAD_GATEWAY.value(), mockLfacsResponse);
		// mocking
		when(mockCacheService.getBySessionId(sessionId)).thenReturn(mockSession);

		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(mockSession.getLosDbResponse()))
				.thenReturn(mockTnInfo);
		
		when(ivrHttpClient.httpPostApiCall(any(), any(), any(), anyString())).thenReturn(responseDto);
		
		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID090Code(sessionId, currentState);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_3, actualResponse.getHookReturnCode());
		assertEquals(IVRConstants.STATE_FID700, actualResponse.getCurrentState());
		
	}
	
	@Test
	void shouldReturnHookReturnCodeOneAsLOSDbResponseIsNull() throws JsonMappingException, JsonProcessingException {
		String sessionId = "session123";
		String currentState = "FID090";
		
		when(mockCacheService.getBySessionId(sessionId)).thenReturn(mockSession);

		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(mockSession.getLosDbResponse()))
		.thenReturn(null);
		
		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID090Code(sessionId, currentState);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_1, actualResponse.getHookReturnCode());

	}
	@Test
	void shouldReturnHookReturnCodeOneAsUserSessionIsNull() throws JsonMappingException, JsonProcessingException {
		String sessionId = "session123";
		String currentState = "FID090";
		
		when(mockCacheService.getBySessionId(sessionId)).thenReturn(null);
		
		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID090Code(sessionId, currentState);
		
		assertEquals(IVRHookReturnCodes.HOOK_RETURN_1, actualResponse.getHookReturnCode());
		
	}
	

}
