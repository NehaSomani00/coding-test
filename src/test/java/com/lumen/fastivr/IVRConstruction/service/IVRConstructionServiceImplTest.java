package com.lumen.fastivr.IVRConstruction.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumen.fastivr.IVRCacheManagement.IVRCacheService;
import com.lumen.fastivr.IVRConstruction.Dto.ConstructionActivityResponse;
import com.lumen.fastivr.IVRConstruction.entity.IvrConstructionSession;
import com.lumen.fastivr.IVRConstruction.helper.IVRConstructionHelper;
import com.lumen.fastivr.IVRConstruction.repository.IVRConstructionCacheService;
import com.lumen.fastivr.IVRDto.IVRWebHookResponseDto;
import com.lumen.fastivr.IVRDto.LOSDB.TNInfoResponse;
import com.lumen.fastivr.IVRDto.retrieveLoopAssignment.RetrieveLoopAssignmentRequest;
import com.lumen.fastivr.IVRLFACS.IVRLfacsServiceHelper;
import com.lumen.fastivr.IVRLFACS.LfacsValidation;
import com.lumen.fastivr.IVRSessionManagement.IVRUserSession;
import com.lumen.fastivr.httpclient.IVRHttpClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IVRConstructionServiceImplTest {

	@Mock
	private IVRCacheService mockCacheService;

	@Mock
	private IVRConstructionCacheService mockCacheServices;

	@Mock
	private ObjectMapper mockObjectMapper;

	@Mock
	IVRHttpClient mockIvrHttpClient;

	@Mock
	private IVRLfacsServiceHelper ivrLfacsServiceHelper;

	@Mock
	private IVRConstructionHelper ivrConstructionHelper;

	@Mock
	private LfacsValidation tnValidation;

	@InjectMocks
	private IVRConstructionServiceImpl ivrConstructionService;

	@Test
	void testParseTelephone() {

		String sessionId = "session123";

		IVRUserSession session = new IVRUserSession();
		IvrConstructionSession sessionConstr = new IvrConstructionSession();

		IVRWebHookResponseDto responseDto = new IVRWebHookResponseDto();
		responseDto.setHookReturnCode(HOOK_RETURN_1);

		session.setSessionId(sessionId);
		sessionConstr.setSessionId(sessionId);

		List<String> userInputs = new ArrayList<>();
		userInputs.add("32");

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(session);
		when(mockCacheServices.getBySessionId(sessionId)).thenReturn(sessionConstr);
		when(tnValidation.validateFacsTN(any(), any())).thenReturn(responseDto);


		IVRWebHookResponseDto actualResponse = ivrConstructionService.parseTelephone(userInputs.get(0), sessionId);

		assertEquals(HOOK_RETURN_1, actualResponse.getHookReturnCode());
		verify(tnValidation, times(1)).validateFacsTN(anyString(), any());

	}

	@Test
	void testCheckOpeningNumberExistsForTransferOpen() throws JsonProcessingException {

		String sessionId = "session123";

		IvrConstructionSession ivrConstructionSession = new IvrConstructionSession();

		IVRWebHookResponseDto responseDto = new IVRWebHookResponseDto();
		responseDto.setHookReturnCode(HOOK_RETURN_8);

		ConstructionActivityResponse response = new ConstructionActivityResponse();

		ivrConstructionSession.setSessionId(sessionId);
		response.setOpenNbr("10");
		response.setOpenStat("open");
		ivrConstructionSession.setConstructionResponse("hello hi");

		List<String> userInputs = new ArrayList<>();
		userInputs.add("32");

		when(mockCacheServices.getBySessionId(sessionId)).thenReturn(ivrConstructionSession);
		when(ivrConstructionHelper.extractDetailsFromConstructionResponse(anyString())).thenReturn(response);

		IVRWebHookResponseDto actualResponse = ivrConstructionService.checkOpeningNumberExists(sessionId);

		assertEquals(HOOK_RETURN_8, actualResponse.getHookReturnCode());
		verify(tnValidation, times(0)).validateFacsTN(anyString(), any());

	}

	@Test
	void testCheckOpeningNumberExistsForTransferPending() throws JsonProcessingException {

		String sessionId = "session123";

		IvrConstructionSession session = new IvrConstructionSession();

		ConstructionActivityResponse response = new ConstructionActivityResponse();
		IVRWebHookResponseDto responseDto = new IVRWebHookResponseDto();
		responseDto.setHookReturnCode(HOOK_RETURN_8);

		session.setSessionId(sessionId);
		response.setOpenNbr("10");
		response.setOpenStat("pend");
		session.setConstructionResponse("hello hi");

		List<String> userInputs = new ArrayList<>();
		userInputs.add("32");

		when(ivrConstructionHelper.extractDetailsFromConstructionResponse(anyString())).thenReturn(response);
		when(mockCacheServices.getBySessionId(sessionId)).thenReturn(session);


		IVRWebHookResponseDto actualResponse = ivrConstructionService.checkOpeningNumberExists(sessionId);

		assertEquals(HOOK_RETURN_8, actualResponse.getHookReturnCode());
		verify(tnValidation, times(0)).validateFacsTN(anyString(), any());

	}

	@Test
	void testCheckOpeningNumberDoesNotExists() throws JsonProcessingException {

		String sessionId = "session123";

		IvrConstructionSession session = new IvrConstructionSession();
		ConstructionActivityResponse response = new ConstructionActivityResponse();

		IVRWebHookResponseDto responseDto = new IVRWebHookResponseDto();
		responseDto.setHookReturnCode(HOOK_RETURN_9);

		session.setSessionId(sessionId);
		response.setOpenNbr("10");
		response.setOpenStat("pending");
		session.setConstructionResponse("hello hi");

		List<String> userInputs = new ArrayList<>();
		userInputs.add("32");

		when(mockCacheServices.getBySessionId(sessionId)).thenReturn(session);
		when(ivrConstructionHelper.extractDetailsFromConstructionResponse(anyString())).thenReturn(response);

		IVRWebHookResponseDto actualResponse = ivrConstructionService.checkOpeningNumberExists(sessionId);

		assertEquals(HOOK_RETURN_9, actualResponse.getHookReturnCode());
		verify(tnValidation, times(0)).validateFacsTN(anyString(), any());

	}

	@Test
	void testCheckOpeningNumberDoesNotExistsWhenOpenNbrEmpty() throws JsonProcessingException {

		String sessionId = "session123";

		IvrConstructionSession session = new IvrConstructionSession();
		ConstructionActivityResponse response = new ConstructionActivityResponse();

		IVRWebHookResponseDto responseDto = new IVRWebHookResponseDto();
		responseDto.setHookReturnCode(HOOK_RETURN_9);

		session.setSessionId(sessionId);
		response.setOpenNbr("");
		response.setOpenStat("open");
		session.setConstructionResponse("hello hi");

		List<String> userInputs = new ArrayList<>();
		userInputs.add("32");

		when(mockCacheServices.getBySessionId(sessionId)).thenReturn(session);
		when(ivrConstructionHelper.extractDetailsFromConstructionResponse(anyString())).thenReturn(response);

		IVRWebHookResponseDto actualResponse = ivrConstructionService.checkOpeningNumberExists(sessionId);

		assertEquals(HOOK_RETURN_9, actualResponse.getHookReturnCode());
		verify(tnValidation, times(0)).validateFacsTN(anyString(), any());

	}

	@Test
	void testIssueReferTroubleReport() throws JsonProcessingException {

		String sessionId = "session123";

		IVRUserSession session = new IVRUserSession();
		IvrConstructionSession ivrConstructionSession = new IvrConstructionSession();
		ConstructionActivityResponse response = new ConstructionActivityResponse();
		response.setOpenNbr("45");

		IVRWebHookResponseDto responseDto = new IVRWebHookResponseDto();
		responseDto.setHookReturnCode(HOOK_RETURN_9);

		RetrieveLoopAssignmentRequest request = new RetrieveLoopAssignmentRequest();


		TNInfoResponse lostDbResponse = new TNInfoResponse();
		lostDbResponse.setPrimaryNPA("12");
		lostDbResponse.setPrimaryNXX("22");
		lostDbResponse.setTn("1234567890");
		lostDbResponse.setNpaState("44");

		session.setSessionId(sessionId);
		session.setLosDbResponse("hello hi");
		ivrConstructionSession.setConstructionResponse("hello hi");

		List<String> userInputs = new ArrayList<>();
		userInputs.add("32");
		String jsonRequestString = "mock-request-string";
		String jsonResultString = "mock-result-string";

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(session);
		when(mockCacheServices.getBySessionId(sessionId)).thenReturn(ivrConstructionSession);
		when(ivrLfacsServiceHelper.extractTNInfoFromLosDBResponse(anyString())).thenReturn(lostDbResponse);
		when(ivrConstructionHelper.extractDetailsFromConstructionResponse(anyString())).thenReturn(response);
		when(ivrConstructionHelper.buildReferTroubleReportRequest(anyString(), anyString(), anyString(), anyString(), any())).thenReturn(request);
		when(mockObjectMapper.writeValueAsString(request)).thenReturn(jsonRequestString);

		when(mockIvrHttpClient.httpPostCall(any(), any(), any(), anyString()))
				.thenReturn(jsonResultString);

		when(mockIvrHttpClient.cleanResponseString(any())).thenReturn(jsonResultString);


		IVRWebHookResponseDto actualResponse = ivrConstructionService.issueReferTroubleReport(userInputs.get(0), sessionId);

		assertEquals(HOOK_RETURN_9, actualResponse.getHookReturnCode());
		verify(ivrConstructionHelper, times(1)).buildReferTroubleReportRequest(anyString(), anyString(), anyString(), anyString(), any());

	}

}
