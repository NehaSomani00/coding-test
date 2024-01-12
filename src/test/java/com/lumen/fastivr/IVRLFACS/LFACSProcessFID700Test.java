package com.lumen.fastivr.IVRLFACS;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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
import com.lumen.fastivr.IVRDto.coe.CentralOfficeEquipmentResponseDto;
import com.lumen.fastivr.IVRDto.common.IVRHttpResponseDto;
import com.lumen.fastivr.IVRSessionManagement.IVRUserSession;
import com.lumen.fastivr.IVRUtils.IVRHookReturnCodes;
import com.lumen.fastivr.httpclient.IVRHttpClient;

@ExtendWith(MockitoExtension.class)
public class LFACSProcessFID700Test {
	
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

	@BeforeEach
	public void initDetails() throws JsonMappingException, JsonProcessingException {
		mockSession = new IVRUserSession();
		mockSession.setSessionId("session123");

	}

	@Test
	void shouldReturnHookReturnCodeEightAsSWITCHNetworkUnitIdIsNotEmpty() throws JsonMappingException, JsonProcessingException {
		
		String currentState = "FID700";
		mockSession.setCentralOfficeEquipmentResponse(mockLfacsResponse);
		
		// mocking
		when(mockCacheService.getBySessionId("session123")).thenReturn(mockSession);

		when(ivrHttpClient.cleanResponseString(eq(mockSession.getCentralOfficeEquipmentResponse()))).thenReturn(mockLfacsResponse);

		when(objectMapper.readValue(eq(mockSession.getCentralOfficeEquipmentResponse()), eq(CentralOfficeEquipmentResponseDto.class)))
		.thenReturn(new ObjectMapper().readValue(mockSession.getCentralOfficeEquipmentResponse(),
				CentralOfficeEquipmentResponseDto.class));
		when(mockIVRLfacsServiceHelper.sendTestResultToTech(any(),any(),any(),any())).thenReturn(true);

		
		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID700Code("session123", currentState);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_8, actualResponse.getHookReturnCode());
		
	}
	
	@Test
	void shouldReturnHookReturnCodeFiveAsSWITCHNetworkUnitIdIstEmpty() throws JsonMappingException, JsonProcessingException, HttpTimeoutException {
		String mockLfacsResponseWithEmptySWITCHNetworkUnitId = "{\"ReturnDataSet\":{\"SWITCHNetworkUnitId\":\"\"},\"MessageStatus\":{\"ErrorCode\":\"\",\"ErrorMessage\":\"\",\"SeverityLevel\":\"\",\"ErrorStatus\":\"S\"}}";
		mockSession.setCentralOfficeEquipmentResponse(mockLfacsResponseWithEmptySWITCHNetworkUnitId);
		String currentState = "FID700";
		
		
		IVRHttpResponseDto responseDto = new IVRHttpResponseDto(HttpStatus.OK.value(), mockLfacsResponseWithEmptySWITCHNetworkUnitId);
		// mocking
		when(mockCacheService.getBySessionId("session123")).thenReturn(mockSession);

		when(ivrHttpClient.cleanResponseString(responseDto.getResponseBody())).thenReturn(mockLfacsResponseWithEmptySWITCHNetworkUnitId);
		when(objectMapper.readValue(eq(mockSession.getCentralOfficeEquipmentResponse()), eq(CentralOfficeEquipmentResponseDto.class)))
				.thenReturn(new ObjectMapper().readValue(mockLfacsResponseWithEmptySWITCHNetworkUnitId,
						CentralOfficeEquipmentResponseDto.class));

		
		when(mockIVRLfacsServiceHelper.sendTestResultToTech(any(),any(),any(),any())).thenReturn(true);

		when(mockIVRLfacsPagerText.getCentralOfficePageText(any(), any(), any())).thenReturn("");
		
		IVRWebHookResponseDto actualResponse = ivrLfacsServiceImpl.processFID700Code("session123", currentState);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_5, actualResponse.getHookReturnCode());
	}
}
