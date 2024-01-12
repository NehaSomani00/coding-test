package com.lumen.fastivr.IVRLFACS;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.Optional;

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
import com.lumen.fastivr.IVRDto.MessageStatus;
import com.lumen.fastivr.IVRDto.LOSDB.TNInfoResponse;
import com.lumen.fastivr.IVRDto.common.IVRHttpResponseDto;
import com.lumen.fastivr.IVRDto.defectivepairs.CablePairRange;
import com.lumen.fastivr.IVRDto.defectivepairs.DefectivePairsInputData;
import com.lumen.fastivr.IVRDto.defectivepairs.DefectivePairsRequestDto;
import com.lumen.fastivr.IVRDto.defectivepairs.DefectivePairsResponseDto;
import com.lumen.fastivr.IVRRepository.FastIvrMnetRepository;
import com.lumen.fastivr.IVRSessionManagement.IVRUserSession;
import com.lumen.fastivr.IVRUtils.IVRConstants;
import com.lumen.fastivr.IVRUtils.IVRHookReturnCodes;
import com.lumen.fastivr.httpclient.IVRHttpClient;

@ExtendWith(MockitoExtension.class)
public class LFACSProcessFID282Test {
	
	@Mock
	private IVRCacheService mockCacheService;

	@Mock
	private IVRLfacsServiceHelper mockIVRLfacsServiceHelper;

	@Mock
	private ObjectMapper mockObjectMapper;


	@Mock
	private IVRLfacsPagerTextFormation mockIVRLfacsPagerText;

	@InjectMocks
	private IVRLfacsServiceImpl ivrLfacsServiceImpl;
	
	@Mock
	IVRHttpClient mockIvrHttpClient;
	
	@Mock
	private FastIvrMnetRepository fastIvrMnetRepository;
	
	private IVRUserSession mockSession;
	
	
	@BeforeEach
	public void initDetails() throws JsonMappingException, JsonProcessingException {
		mockSession = new IVRUserSession();
		mockSession.setSessionId("session123");
		mockSession.setEc("999");
		mockSession.setCanBePagedMobile(true);
		mockSession.setCanBePagedEmail(true);
		mockSession.setCable("LPG278");
		mockSession.setPair("17");	
		
		mockSession.setLosDbResponse("mock-losdb-response-string");
		mockSession.setCurrentAssignmentResponse(
				"{\"ReturnDataSet\":{\"LOOP\":[{\"SEG\":[{\"SEGNO\":\"2\",\"CA\":\"DSL1822\",\"PR\":\"128\"},{\"SEGNO\":\"3\",\"CA\":\"DSL1822\",\"PR\":\"128\"},{\"SEGNO\":\"4\",\"CA\":\"DSL1822\",\"PR\":\"128\"}]}]},\"RequestId\":\"FASTFAST\",\"WebServiceName\":\"SIABusService\",\"CompletedTimeStampSpecified\":true}");
		
	}
	
	@Test
	void testProcessFID282Code() throws Exception {
	    // Arrange
	    String sessionId = "123";
	    String nextState = "nextState";
	    
	    TNInfoResponse mockTnInfo = new TNInfoResponse();
		mockTnInfo.setTn("1234567890");
		mockTnInfo.setPrimaryNPA("123");
		mockTnInfo.setPrimaryNXX("456");
		
		IVRHttpResponseDto dto = new IVRHttpResponseDto();
		dto.setStatusCode(HttpStatus.OK.value());
		dto.setResponseBody("responseBody");
		
		 DefectivePairsRequestDto defectivePairsRequestDto = new DefectivePairsRequestDto();
		    defectivePairsRequestDto.setInputData(new DefectivePairsInputData.Builder().cableId("cableId").cablePairRange(new CablePairRange()).build());
		    defectivePairsRequestDto.getInputData().getCablePairRange().setLowPair(1);
	    
		when(mockIVRLfacsServiceHelper.extractTNInfoFromLosDBResponse(mockSession.getLosDbResponse()))
		.thenReturn(mockTnInfo);
		
		when(mockIVRLfacsServiceHelper.buildDefectivePairsRequest(any(), any(), any(),any(), anyString()))
		.thenReturn(defectivePairsRequestDto);
		
	    when(mockCacheService.getBySessionId(sessionId)).thenReturn(mockSession);
	    Optional<String> lastName = Optional.of("Test");
	    when(fastIvrMnetRepository.findLastNameByCuid(mockSession.getCuid())).thenReturn(lastName);

	    when(mockIvrHttpClient.httpPostApiCall(any(), any(), any(), anyString())).thenReturn(dto);
	    when(mockIvrHttpClient.cleanResponseString(any())).thenReturn("cleanResponseString");
	   
	    DefectivePairsResponseDto defectivePairsResponseDto = new DefectivePairsResponseDto();
	    defectivePairsResponseDto.setMessageStatus(new MessageStatus());
	    defectivePairsResponseDto.getMessageStatus().setErrorStatus(IVRConstants.WRAPPER_API_ERROR_STATUS_SUCCESS);
	    when(mockObjectMapper.readValue(anyString(), eq(DefectivePairsResponseDto.class))).thenReturn(defectivePairsResponseDto);
	    IVRHttpResponseDto ivrHttpResponseDto = new IVRHttpResponseDto();
	    ivrHttpResponseDto.setResponseBody("responseBody");
	   

	    // Act
	    IVRWebHookResponseDto response = ivrLfacsServiceImpl.processFID282Code(sessionId, nextState);

	    // Assert
	    assertEquals(nextState, response.getCurrentState());
	    assertEquals(IVRHookReturnCodes.HOOK_RETURN_0, response.getHookReturnCode());
	    assertEquals("Success", response.getHookReturnMessage());
	}

	@Test
	void testProcessFID282Code_withNullUserSession() throws Exception {
	    // Arrange
	    String sessionId = "123";
	    String nextState = "nextState";
	    when(mockCacheService.getBySessionId(sessionId)).thenReturn(null);

	    // Act
	    IVRWebHookResponseDto response = ivrLfacsServiceImpl.processFID282Code(sessionId, nextState);

	    // Assert
	    assertEquals(sessionId, response.getSessionId());
	    assertEquals(nextState, response.getCurrentState());
	    assertEquals(IVRHookReturnCodes.HOOK_RETURN_1, response.getHookReturnCode());
	    assertEquals(IVRConstants.GPDOWN_ERR_MSG, response.getHookReturnMessage());
	}

	@Test
	void testProcessFID282Code_withErrorResponse() throws Exception {
	    // Arrange
	    String sessionId = "123";
	    String nextState = "nextState";
	    IVRUserSession userSession = new IVRUserSession();
	    userSession.setCanBePagedMobile(true);
	    userSession.setCanBePagedEmail(true);
	    when(mockCacheService.getBySessionId(sessionId)).thenReturn(userSession);
	    DefectivePairsResponseDto defectivePairsResponseDto = new DefectivePairsResponseDto();
	    defectivePairsResponseDto.setMessageStatus(new MessageStatus());
	    defectivePairsResponseDto.getMessageStatus().setErrorStatus("error");
	    IVRHttpResponseDto ivrHttpResponseDto = new IVRHttpResponseDto();
	    ivrHttpResponseDto.setResponseBody("responseBody");
	    DefectivePairsRequestDto defectivePairsRequestDto = new DefectivePairsRequestDto();
	    defectivePairsRequestDto.setInputData(new DefectivePairsInputData.Builder().cableId("cableId").cablePairRange(new CablePairRange()).build());

	    // Act
	    IVRWebHookResponseDto response = ivrLfacsServiceImpl.processFID282Code(sessionId, nextState);

	    // Assert
	    assertEquals(nextState, response.getCurrentState());
	    assertEquals(IVRHookReturnCodes.HOOK_RETURN_1, response.getHookReturnCode());
	    assertEquals(IVRConstants.GPDOWN_ERR_MSG, response.getHookReturnMessage());
	}

}
