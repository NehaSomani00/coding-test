/**
 * 
 */
package com.lumen.fastivr.IVRCallerId;

import static com.lumen.fastivr.IVRUtils.IVRConstants.INVALID_SESSION_ID;
import static com.lumen.fastivr.IVRUtils.IVRConstants.VALID_TN_MSG;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_1;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.net.http.HttpTimeoutException;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumen.fastivr.IVRCNF.helper.IVRCnfHelper;
import com.lumen.fastivr.IVRCacheManagement.IVRCacheService;
import com.lumen.fastivr.IVRDto.IVRWebHookResponseDto;
import com.lumen.fastivr.IVRLFACS.IVRLfacsServiceHelper;
import com.lumen.fastivr.IVRLFACS.IVRLfacsServiceImpl;
import com.lumen.fastivr.IVRLFACS.LfacsValidation;
import com.lumen.fastivr.IVRSessionManagement.IVRUserSession;
import com.lumen.fastivr.IVRUtils.IVRConstants;
import com.lumen.fastivr.IVRUtils.IVRHookReturnCodes;
import com.lumen.fastivr.httpclient.IVRHttpClient;

@ExtendWith(MockitoExtension.class)
public class IVRCallerIdImplTest {


	@Mock
	private IVRCacheService mockCacheService;

	@InjectMocks
	private IVRCallerIdSeviceImpl callerIdServiceImpl;

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
	void processIDD011_VALIDSESSION() {
		String sessionid = "session123";
		String userInput = "1234567";
		String currentState = IVRConstants.STATE_ID0010;
		IVRWebHookResponseDto mockResponse = new IVRWebHookResponseDto();
		mockResponse.setHookReturnMessage(VALID_TN_MSG);
		mockResponse.setHookReturnCode(HOOK_RETURN_1);
		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId(sessionid);
		when(mockCacheService.getBySessionId(sessionid)).thenReturn(mockSession);
		when(mockLfacsTNValidation.validateFacsTN(userInput, mockSession)).thenReturn(mockResponse);

		IVRWebHookResponseDto response = callerIdServiceImpl.processIDD011StateCode(sessionid, currentState, userInput);
		assertEquals(HOOK_RETURN_1, response.getHookReturnCode());
		assertEquals(VALID_TN_MSG, response.getHookReturnMessage());
	}
	
	@Test
	void processIDD011_INVALIDSESSION() {
		String sessionid = "session123";
		String userInput = "1234567";
		String currentState = IVRConstants.STATE_IDD011;
		IVRWebHookResponseDto mockResponse = new IVRWebHookResponseDto();
		mockResponse.setHookReturnMessage(INVALID_SESSION_ID);
		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId(sessionid);
		when(mockCacheService.getBySessionId(sessionid)).thenReturn(null);

		IVRWebHookResponseDto response = callerIdServiceImpl.processIDD011StateCode(sessionid, currentState, userInput);
		assertEquals(INVALID_SESSION_ID, response.getHookReturnMessage());
	}
	
	
	
	@Test
	void processIDD020StateCode_Test() throws JsonMappingException, JsonProcessingException,
			InterruptedException, ExecutionException, HttpTimeoutException {

		String sessionId = "1245";
		
		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId(sessionId);
		when(mockCacheService.getBySessionId(sessionId)).thenReturn(mockSession);

		IVRWebHookResponseDto actualResponse = callerIdServiceImpl.processIDD020StateCode(sessionId, null, null);

		assertEquals(IVRHookReturnCodes.HOOK_RETURN_1, actualResponse.getHookReturnCode());
	}
	
}