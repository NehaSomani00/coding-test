package com.lumen.fastivr.IVRService;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.lumen.fastivr.IVRAdmin.IVRAdminInterface;
import com.lumen.fastivr.IVRBusinessException.BadUserInputException;
import com.lumen.fastivr.IVRCANST.service.IVRCanstService;
import com.lumen.fastivr.IVRCNF.service.IVRCnfService;
import com.lumen.fastivr.IVRCacheManagement.IVRCacheService;
import com.lumen.fastivr.IVRChangeStatusCablePair.IVRChangeStatusCablePairServiceImpl;
import com.lumen.fastivr.IVRConstruction.service.IVRConstructionService;
import com.lumen.fastivr.IVRDto.IVRWebHookResponseDto;
import com.lumen.fastivr.IVRLFACS.IVRLfacsServiceImpl;
import com.lumen.fastivr.IVRMLT.caching.IvrMltCacheService;
import com.lumen.fastivr.IVRMLT.entity.IvrMltSession;
import com.lumen.fastivr.IVRMLT.service.IvrMltService;
import com.lumen.fastivr.IVRSessionManagement.IVRUserSession;
import com.lumen.fastivr.IVRSignon.IVRSignOnServiceHelper;
import com.lumen.fastivr.IVRSignon.IVRSignonServiceImpl;
import com.lumen.fastivr.IVRStateManagement.IVRState;
import com.lumen.fastivr.IVRStateManagement.IVRStateSystem;
import com.lumen.fastivr.IVRStateManagement.IVRStateTransition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.http.HttpTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.lumen.fastivr.IVRUtils.IVRConstants.*;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class IVRServiceImplTest {

	@InjectMocks
	private IVRServiceImpl ivrService;

	@Mock
	private IVRSignonServiceImpl mockSignOnService;

	@Mock
	private IVRLfacsServiceImpl mockIvrLfacsService;
	
	@Mock
	private IVRChangeStatusCablePairServiceImpl cablePairServiceImpl;

	@Mock
	private IVRStateSystem mockIvrStateSystem;


	@Mock
	private IVRConstructionService ivrConstructionService;

	@Mock
	private IVRAdminInterface ivrAdminInterface;

	@Mock
	private Map<String, IVRUserSession> mockUserSessionMap;

	@Mock
	private IVRSignOnServiceHelper mockServiceHelper;

	@Mock
	private IVRCnfService mockIvrCnfService;

	@Mock
	private IvrMltService ivrMltService;

	@Mock
	private IVRCacheService mockIvrCacheService;

	@Mock
	private IvrMltCacheService mockMltCacheService;

	@Mock
	private IVRCanstService ivrCanstService;


	private IvrMltCacheService mltCacheService;


	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testProcessMLT() {
		// fail("Not yet implemented");
	}

	@Test
	void testProcessSignOn_MultipleInputs_ValidTech() {
		String sessionId = "session123";
		String currentStateStr = "SS0110";
		String nextStateStr = "SSD110";
		List<String> userInputs = new ArrayList<>();
		userInputs.add("45634");
		userInputs.add("123456");
		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, DIRECT_STATE_TRANSFER);
		genesysState.addTransitions(mockIvrStateStransition);

		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_1);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(mockSignOnService.issueUserLogin(userInputs, sessionId)).thenReturn(mockHookResponse);
		when(mockSignOnService.issueValidateTechnicianProperties(sessionId)).thenReturn(mockHookResponse);
		mockHookResponse.setCurrentState(STATE_SSD111);
		when(mockSignOnService.checkTechAreaCode(sessionId)).thenReturn(mockHookResponse);
		mockHookResponse.setCurrentState(STATE_SSD180);

		IVRWebHookResponseDto responseHookObject = ivrService.processSignOn(sessionId, currentStateStr, userInputs);

		assertEquals(HOOK_RETURN_1, responseHookObject.getHookReturnCode());

	}

	@Test
	void testProcessSignOn_SingleInput_SSD180() {
		String sessionId = "session123";
		String currentStateStr = "SS0180";
		String nextStateStr = "SSD180";
		String userInput = "1";
		List<String> userInputs = new ArrayList<>();
		userInputs.add(userInput);
		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, userInput);
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);
		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_2);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(mockSignOnService.checkTechAreaCode(sessionId)).thenReturn(mockHookResponse);

		IVRWebHookResponseDto responseHookObject = ivrService.processSignOn(sessionId, currentStateStr, userInputs);
		assertEquals(HOOK_RETURN_2, responseHookObject.getHookReturnCode());

	}

	@Test
	void test_SSD120() {
		String sessionId = "session123";
		String currentStateStr = "SS0120";
		String nextStateStr = "SSD120";
		String userInput = "newpassword";
		List<String> userInputs = new ArrayList<>();
		userInputs.add(userInput);
		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, DIRECT_STATE_TRANSFER);
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);
		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_3);
		mockHookResponse.setHookReturnMessage("Password validated");

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(mockSignOnService.validatePasswordBusinessRules(userInputs.get(0), sessionId))
				.thenReturn(mockHookResponse);

		IVRWebHookResponseDto response = ivrService.processSignOn(sessionId, currentStateStr, userInputs);

		assertEquals(HOOK_RETURN_3, response.getHookReturnCode());
	}

	@Test
	void test_SSD135_SSD180() {
		String sessionId = "session123";
		String currentStateStr = "SS0135";
		String nextStateStr = "SSD135";
		String userInput = "1";
		List<String> userInputs = new ArrayList<>();
		userInputs.add(userInput);
		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, userInput);
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);
		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_2);
		mockHookResponse.setHookReturnMessage("Valid Area code");

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(mockSignOnService.issueUserLoginWithNewPassword(userInputs.get(0), sessionId))
				.thenReturn(mockHookResponse);
		when(mockSignOnService.checkTechAreaCode(sessionId)).thenReturn(mockHookResponse);

		IVRWebHookResponseDto response = ivrService.processSignOn(sessionId, currentStateStr, userInputs);

		assertEquals(HOOK_RETURN_2, response.getHookReturnCode());

	}

	@Test
	void test_SSD150() {
		String sessionId = "session123";
		String currentStateStr = "SS0150";
		String nextStateStr = "SSD150";
		String userInput = "1";
		List<String> userInputs = new ArrayList<>();
		userInputs.add(userInput);
		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, userInput);
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);
		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_1);
		mockHookResponse.setHookReturnMessage("Valid");

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(mockSignOnService.validateTechPagerConfiguredPasswordReset(sessionId))
				.thenReturn(mockHookResponse);

		IVRWebHookResponseDto response = ivrService.processSignOn(sessionId, currentStateStr, userInputs);

		assertEquals(HOOK_RETURN_1, response.getHookReturnCode());

	}

	@Test
	void test_SSD210_SSD220() {
		String sessionId = "session123";
		String currentStateStr = "SS0210";
		String nextStateStr = "SSD210";
		String userInput = "123456";
		List<String> userInputs = new ArrayList<>();
		userInputs.add(userInput);
		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, DIRECT_STATE_TRANSFER);
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);
		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_1);
		mockHookResponse.setHookReturnMessage("Valid");

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(mockSignOnService.validateTechSecurityCodeFromSession(userInputs.get(0), sessionId))
				.thenReturn(mockHookResponse);
		when(mockSignOnService.generatePagerOTP(sessionId)).thenReturn(mockHookResponse);

		IVRWebHookResponseDto response = ivrService.processSignOn(sessionId, currentStateStr, userInputs);

		assertEquals(HOOK_RETURN_1, response.getHookReturnCode());

	}

	@Test
	void test_SSD300() {
		String sessionId = "session123";
		String currentStateStr = "SS0300";
		String nextStateStr = "SSD300";
		String userInput = "123456";
		List<String> userInputs = new ArrayList<>();
		userInputs.add(userInput);
		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, DIRECT_STATE_TRANSFER);
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);
		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_1);
		mockHookResponse.setHookReturnMessage("Valid");

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(mockSignOnService.validatePagerOtp(userInputs.get(0), sessionId)).thenReturn(mockHookResponse);

		IVRWebHookResponseDto response = ivrService.processSignOn(sessionId, currentStateStr, userInputs);

		assertEquals(HOOK_RETURN_1, response.getHookReturnCode());

	}

	@Test
	void test_SSD160() {
		String sessionId = "session123";
		String currentStateStr = "SS0160";
		String nextStateStr = "SSD160";
		String userInput = "birthdate";
		List<String> userInputs = new ArrayList<>();
		userInputs.add(userInput);
		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, DIRECT_STATE_TRANSFER);
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);
		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_1);
		mockHookResponse.setHookReturnMessage("Valid");

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(mockSignOnService.validateBirthDate(userInputs.get(0), sessionId)).thenReturn(mockHookResponse);

		IVRWebHookResponseDto response = ivrService.processSignOn(sessionId, currentStateStr, userInputs);

		assertEquals(HOOK_RETURN_1, response.getHookReturnCode());

	}

	@Test
	void test_SSD165_SSD170() {
		String sessionId = "session123";
		String currentStateStr = "SS0165";
		String nextStateStr = "SSD165";
		String userInput = "1";
		List<String> userInputs = new ArrayList<>();
		userInputs.add(userInput);
		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, userInput);
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);
		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_1);
		mockHookResponse.setHookReturnMessage("Valid");

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(mockSignOnService.persistNewBirthdate(sessionId)).thenReturn(mockHookResponse);
		when(mockSignOnService.validateTechPagerConfiguredBirthdate(sessionId)).thenReturn(mockHookResponse);
		when(mockSignOnService.checkTechAreaCode(sessionId)).thenReturn(mockHookResponse);

		IVRWebHookResponseDto response = ivrService.processSignOn(sessionId, currentStateStr, userInputs);

		assertEquals(HOOK_RETURN_1, response.getHookReturnCode());
	}

	@Test
	void test_SSD170() {
		String sessionId = "session123";
		String currentStateStr = "SS0170";
		String nextStateStr = "SSD170";
		String userInput = "";
		List<String> userInputs = new ArrayList<>();
		userInputs.add(userInput);
		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, DIRECT_STATE_TRANSFER);
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);
		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_1);
		mockHookResponse.setHookReturnMessage("Valid");

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(mockSignOnService.validateTechPagerConfiguredBirthdate(sessionId)).thenReturn(mockHookResponse);
		when(mockSignOnService.checkTechAreaCode(sessionId)).thenReturn(mockHookResponse);

		IVRWebHookResponseDto response = ivrService.processSignOn(sessionId, currentStateStr, userInputs);

		assertEquals(HOOK_RETURN_1, response.getHookReturnCode());

	}

	@Test
	void test_SSD190() {
		String sessionId = "session123";
		String currentStateStr = "SS0190";
		String nextStateStr = "SSD190";
		String userInput = "area-code";
		List<String> userInputs = new ArrayList<>();
		userInputs.add(userInput);
		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, DIRECT_STATE_TRANSFER);
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);
		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_2);
		mockHookResponse.setHookReturnMessage("Valid");

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(mockSignOnService.validateNPA(userInputs.get(0), sessionId)).thenReturn(mockHookResponse);

		IVRWebHookResponseDto response = ivrService.processSignOn(sessionId, currentStateStr, userInputs);

		assertEquals(HOOK_RETURN_2, response.getHookReturnCode());

	}

	@Test
	void testProcessSignOn_SessionID_NullOrEmpty() {
		String sessionId = "";
		String currentStateStr = "SS0180";
		String userInput = "1";
		List<String> userInputs = new ArrayList<>();
		userInputs.add(userInput);

		assertThrows(BadUserInputException.class,
				() -> ivrService.processSignOn(sessionId, currentStateStr, userInputs));

	}

	@Test
	void testProcessSignOn_State_Not_Configured() {
		String sessionId = "session123";
		String currentStateStr = "SS0180";
		String userInput = "1";
		List<String> userInputs = new ArrayList<>();
		userInputs.add(userInput);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);

		assertThrows(BadUserInputException.class,
				() -> ivrService.processSignOn(sessionId, currentStateStr, userInputs));

	}

	@Test
	void testProcessSignOn_Invalid_GenesysState() {
		String sessionId = "session123";
		String currentStateStr = "SS0900";
		String nextStateStr = "SSD900";
		String userInput = "1";
		List<String> userInputs = new ArrayList<>();
		userInputs.add(userInput);
		IVRState ivrState = new IVRState(currentStateStr);
		IVRState nextIvrState = new IVRState(nextStateStr);
		IVRStateTransition transition = new IVRStateTransition(nextIvrState, userInput);
		ivrState.addTransitions(transition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, ivrState);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);

		assertThrows(BadUserInputException.class,
				() -> ivrService.processSignOn(sessionId, currentStateStr, userInputs));

	}

	@Test
	void testProcessLFACS_FDI035_Success() throws JsonMappingException, JsonProcessingException {
		String sessionId = "session123";
		String currentStateStr = "FI0030";
		String nextStateStr = "FID035";
		String userInput = "1";
		List<String> userInputs = new ArrayList<>();
		userInputs.add(userInput);
		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, userInput);
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);
		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_3);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(mockIvrLfacsService.processFID035Code(sessionId, nextStateStr)).thenReturn(mockHookResponse);

		IVRWebHookResponseDto responseHookObject = ivrService.processLFACS(sessionId, currentStateStr, userInputs);
		assertEquals(HOOK_RETURN_3, responseHookObject.getHookReturnCode());
	}

	@Test
	void testProcessLFACS_FID400_Success() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentStateStr = "FI0030";
		String nextStateStr = "FID035";
		String nextStateStr400 = "FID400";
		String userInput = "1";
		List<String> userInputs = new ArrayList<>();
		userInputs.add(userInput);
		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, userInput);
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);
		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_2);

		IVRWebHookResponseDto mockHookResponse400 = new IVRWebHookResponseDto();
		mockHookResponse400.setCurrentState(nextStateStr400);
		mockHookResponse400.setHookReturnCode(HOOK_RETURN_1);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(mockIvrLfacsService.processFID035Code(sessionId, nextStateStr)).thenReturn(mockHookResponse);
		when(mockIvrLfacsService.processFID400Code(sessionId, nextStateStr400, "2")).thenReturn(mockHookResponse400);

		IVRWebHookResponseDto responseHookObject = ivrService.processLFACS(sessionId, currentStateStr, userInputs);
		assertEquals(HOOK_RETURN_1, responseHookObject.getHookReturnCode());
	}

	@Test
	void testProcessLFACS_FID400_DIRECT_STATE_TRANSFER() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentStateStr = "FI0030";
		String nextStateStr = "FID035";
		String nextStateStr400 = "FID400";
		List<String> userInputs = new ArrayList<String>();
		userInputs.add("");
		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, DIRECT_STATE_TRANSFER);
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);
		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_2);

		IVRWebHookResponseDto mockHookResponse400 = new IVRWebHookResponseDto();
		mockHookResponse400.setCurrentState(nextStateStr400);
		mockHookResponse400.setHookReturnCode(HOOK_RETURN_1);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(mockIvrLfacsService.processFID035Code(sessionId, nextStateStr)).thenReturn(mockHookResponse);
		when(mockIvrLfacsService.processFID400Code(sessionId, nextStateStr400, "2")).thenReturn(mockHookResponse400);

		IVRWebHookResponseDto responseHookObject = ivrService.processLFACS(sessionId, currentStateStr, userInputs);
		assertEquals(HOOK_RETURN_1, responseHookObject.getHookReturnCode());
	}

	@Test
	void testProcessLFACS_FDI035_LFACS_Down() throws JsonMappingException, JsonProcessingException {
		String sessionId = "session123";
		String currentStateStr = "FI0030";
		String nextStateStr = "FID035";
		String userInput = "1";
		List<String> userInputs = new ArrayList<>();
		userInputs.add(userInput);
		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, userInput);
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);
		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_1);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(mockIvrLfacsService.processFID035Code(sessionId, nextStateStr)).thenReturn(mockHookResponse);

		IVRWebHookResponseDto responseHookObject = ivrService.processLFACS(sessionId, currentStateStr, userInputs);
		assertEquals(HOOK_RETURN_1, responseHookObject.getHookReturnCode());
	}

	@Test
	void testProcessLFACS_FID400_From_FI0040() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentStateStr = "FI0040";
		String nextStateStr = "FID400";
		String userInput = "1";
		List<String> userInputs = new ArrayList<>();
		userInputs.add(userInput);
		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, userInput);
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);


		IVRWebHookResponseDto mockHookResponse400 = new IVRWebHookResponseDto();
		mockHookResponse400.setCurrentState(nextStateStr);
		mockHookResponse400.setHookReturnCode(HOOK_RETURN_1);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(mockIvrLfacsService.processFID400Code(sessionId, nextStateStr, "1")).thenReturn(mockHookResponse400);

		IVRWebHookResponseDto responseHookObject = ivrService.processLFACS(sessionId, currentStateStr, userInputs);
		assertEquals(HOOK_RETURN_1, responseHookObject.getHookReturnCode());
	}

	@Test
	void testProcessLFACS_FDI035_LFACS_WithoutSession() {
		String sessionId = "";
		String currentStateStr = "FID035";
		String userInput = "1";
		List<String> userInputs = new ArrayList<>();
		userInputs.add(userInput);

		assertThrows(BadUserInputException.class,
				() -> ivrService.processLFACS(sessionId, currentStateStr, userInputs));

	}

	@Test
	void testProcessLFACS_FDI035_LFACS_WithoutDMInput() {
		String sessionId = "session123";
		String currentStateStr = "FID035";
		String userInput = "";
		List<String> userInputs = new ArrayList<>();
		userInputs.add(userInput);

		assertThrows(BadUserInputException.class,
				() -> ivrService.processLFACS(sessionId, currentStateStr, userInputs));

	}

	@Test
	void testProcessLFACS_FDI035_LFACS_WithDefaultState() {
		String sessionId = "session123";
		String currentStateStr = "FID000";
		String userInput = "1";
		List<String> userInputs = new ArrayList<>();
		userInputs.add(userInput);

		assertThrows(BadUserInputException.class,
				() -> ivrService.processLFACS(sessionId, currentStateStr, userInputs));

	}

	@Test
	void testProcessLFACS_FDI420_LFACS() throws JsonMappingException, JsonProcessingException {
		String sessionId = "session123";
		String currentStateStr = "FI0410";
		String nextStateStr = "FID420";
		String userInput = "1";
		List<String> userInputs = new ArrayList<>();
		userInputs.add(userInput);
		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, userInput);
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);
		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_1);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(mockIvrLfacsService.processFID420Code(sessionId, nextStateStr)).thenReturn(mockHookResponse);

		IVRWebHookResponseDto responseHookObject = ivrService.processLFACS(sessionId, currentStateStr, userInputs);
		assertEquals(HOOK_RETURN_1, responseHookObject.getHookReturnCode());
	}

	@Test
	void testProcessLFACS_FDI429_LFACS() throws JsonMappingException, JsonProcessingException {
		String sessionId = "session123";
		String currentStateStr = "FI0421";
		String nextStateStr = "FID429";
		String userInput = "1";
		List<String> userInputs = new ArrayList<>();
		userInputs.add(userInput);
		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, userInput);
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);
		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_1);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(mockIvrLfacsService.processFID429Code(sessionId, nextStateStr)).thenReturn(mockHookResponse);

		IVRWebHookResponseDto responseHookObject = ivrService.processLFACS(sessionId, currentStateStr, userInputs);
		assertEquals(HOOK_RETURN_1, responseHookObject.getHookReturnCode());
	}

	@Test
	void testProcessLFACS_FDI445_LFACS() throws JsonMappingException, JsonProcessingException {
		String sessionId = "session123";
		String currentStateStr = "FI0450";
		String nextStateStr = "FID445";
		String userInput = "1";
		List<String> userInputs = new ArrayList<>();
		userInputs.add(userInput);
		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, userInput);
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);
		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_1);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(mockIvrLfacsService.processFID445Code(sessionId, currentStateStr, nextStateStr, 1)).thenReturn(mockHookResponse);

		IVRWebHookResponseDto responseHookObject = ivrService.processLFACS(sessionId, currentStateStr, userInputs);
		assertEquals(HOOK_RETURN_1, responseHookObject.getHookReturnCode());
	}

	@Test
	void testProcessLFACS_FID455_LFACS() throws JsonMappingException, JsonProcessingException {
		String sessionId = "session123";
		String currentStateStr = "FI0441";
		String nextStateStr = "FID455";
		String userInput = "1";
		List<String> userInputs = new ArrayList<>();
		userInputs.add(userInput);
		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, userInput);
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);
		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_1);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(mockIvrLfacsService.processFID455Code(sessionId, nextStateStr)).thenReturn(mockHookResponse);

		IVRWebHookResponseDto responseHookObject = ivrService.processLFACS(sessionId, currentStateStr, userInputs);
		assertEquals(HOOK_RETURN_1, responseHookObject.getHookReturnCode());
	}

	@Test
	void testProcessLFACS_FID211() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentStateStr = "FI0210";
		String nextStateStr = "FID211";
		String userInput = "123";
		List<String> userInputs = new ArrayList<>();
		userInputs.add(userInput);
		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, DIRECT_STATE_TRANSFER);
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);
		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_1);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(mockIvrLfacsService.processFID211Code(sessionId, nextStateStr, userInputs.get(0))).thenReturn(mockHookResponse);

		IVRWebHookResponseDto responseHookObject = ivrService.processLFACS(sessionId, currentStateStr, userInputs);
		assertEquals(HOOK_RETURN_1, responseHookObject.getHookReturnCode());
	}

	@Test
	void testProcessLFACS_FID011() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentStateStr = "FI0010";
		String nextStateStr = "FID011";
		String userInput = "123";
		List<String> userInputs = new ArrayList<>();
		userInputs.add(userInput);
		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, DIRECT_STATE_TRANSFER);
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);
		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_1);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(mockIvrLfacsService.processFID011(sessionId, nextStateStr, userInputs.get(0))).thenReturn(mockHookResponse);

		IVRWebHookResponseDto responseHookObject = ivrService.processLFACS(sessionId, currentStateStr, userInputs);
		assertEquals(HOOK_RETURN_1, responseHookObject.getHookReturnCode());
	}

	@Test
	void testProcessLFACS_FID020() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentStateStr = "FI0015";
		String nextStateStr = "FID020";
		List<String> userInputs = new ArrayList<>();
		userInputs.add("");
		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, DIRECT_STATE_TRANSFER);
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);
		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_0);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(mockIvrLfacsService.processFID020Code(sessionId, nextStateStr)).thenReturn(mockHookResponse);

		IVRWebHookResponseDto responseHookObject = ivrService.processLFACS(sessionId, currentStateStr, userInputs);
		assertEquals(HOOK_RETURN_0, responseHookObject.getHookReturnCode());
	}

	@Test
	void testProcessLFACS_FID237() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentStateStr = "FI0230";
		String nextStateStr = "FID237";
		List<String> userInputs = new ArrayList<>();
		userInputs.add("123");
		userInputs.add("234");
		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, DIRECT_STATE_TRANSFER);
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);
		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_0);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(mockIvrLfacsService.processFID237Code(sessionId, nextStateStr, userInputs)).thenReturn(mockHookResponse);

		IVRWebHookResponseDto responseHookObject = ivrService.processLFACS(sessionId, currentStateStr, userInputs);
		assertEquals(HOOK_RETURN_0, responseHookObject.getHookReturnCode());
	}

	@Test
	void testProcessLFACS_FID250() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentStateStr = "FI0240";
		String nextStateStr = "FID250";
		List<String> userInputs = new ArrayList<>();
		userInputs.add("1");
		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, "1");
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);
		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_1);

		String nextStateStr271 = "FID271";
		IVRWebHookResponseDto mockHookResponse271 = new IVRWebHookResponseDto();
		mockHookResponse271.setCurrentState(nextStateStr271);
		mockHookResponse271.setHookReturnCode(HOOK_RETURN_3);

		when(mockIvrLfacsService.processFID271Code(sessionId, nextStateStr271)).thenReturn(mockHookResponse271);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(mockIvrLfacsService.processFID250Code(sessionId, nextStateStr, 1)).thenReturn(mockHookResponse);

		IVRWebHookResponseDto responseHookObject = ivrService.processLFACS(sessionId, currentStateStr, userInputs);
		assertEquals(HOOK_RETURN_3, responseHookObject.getHookReturnCode());
	}

	@Test
	void testProcessLFACS_FID250_400() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentStateStr = "FI0240";
		String nextStateStr = "FID250";
		List<String> userInputs = new ArrayList<>();
		userInputs.add("1");
		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, "1");
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);
		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_1);

		String nextStateStr271 = "FID271";
		IVRWebHookResponseDto mockHookResponse271 = new IVRWebHookResponseDto();
		mockHookResponse271.setCurrentState(nextStateStr271);
		mockHookResponse271.setHookReturnCode(HOOK_RETURN_2);

		String nextStateStr400 = "FID400";
		IVRWebHookResponseDto mockHookResponse400 = new IVRWebHookResponseDto();
		mockHookResponse400.setCurrentState(nextStateStr400);
		mockHookResponse400.setHookReturnCode(HOOK_RETURN_2);

		when(mockIvrLfacsService.processFID271Code(sessionId, nextStateStr271)).thenReturn(mockHookResponse271);
		when(mockIvrLfacsService.processFID400Code(sessionId, nextStateStr400, "2")).thenReturn(mockHookResponse400);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(mockIvrLfacsService.processFID250Code(sessionId, nextStateStr, 1)).thenReturn(mockHookResponse);

		IVRWebHookResponseDto responseHookObject = ivrService.processLFACS(sessionId, currentStateStr, userInputs);
		assertEquals(HOOK_RETURN_2, responseHookObject.getHookReturnCode());
	}

	@Test
	void testProcessLFACS_FID250_500() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentStateStr = "FI0240";
		String nextStateStr = "FID250";
		List<String> userInputs = new ArrayList<>();
		userInputs.add("1");
		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, "1");
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);
		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_2);

		String nextStateStr500 = "FID500";
		IVRWebHookResponseDto mockHookResponse500 = new IVRWebHookResponseDto();
		mockHookResponse500.setCurrentState(nextStateStr500);
		mockHookResponse500.setHookReturnCode(HOOK_RETURN_2);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(mockIvrLfacsService.processFID250Code(sessionId, nextStateStr, 1)).thenReturn(mockHookResponse);
		when(mockIvrLfacsService.processFID500Code(sessionId, nextStateStr500, userInputs)).thenReturn(mockHookResponse500);

		IVRWebHookResponseDto responseHookObject = ivrService.processLFACS(sessionId, currentStateStr, userInputs);
		assertEquals(HOOK_RETURN_2, responseHookObject.getHookReturnCode());
	}

	@Test
	void testProcessLFACS_FID250_600() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentStateStr = "FI0240";
		String nextStateStr = "FID250";
		List<String> userInputs = new ArrayList<>();
		userInputs.add("1");
		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, "1");
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);
		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_4);

		String nextStateStr600 = "FID600";
		IVRWebHookResponseDto mockHookResponse600 = new IVRWebHookResponseDto();
		mockHookResponse600.setCurrentState(nextStateStr600);
		mockHookResponse600.setHookReturnCode(HOOK_RETURN_1);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(mockIvrLfacsService.processFID250Code(sessionId, nextStateStr, 1)).thenReturn(mockHookResponse);
		when(mockIvrLfacsService.processFID600Code(sessionId, nextStateStr600)).thenReturn(mockHookResponse600);

		IVRWebHookResponseDto responseHookObject = ivrService.processLFACS(sessionId, currentStateStr, userInputs);
		assertEquals(HOOK_RETURN_1, responseHookObject.getHookReturnCode());
	}

	@Test
	void testProcessLFACS_FID250_300() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentStateStr = "FI0240";
		String nextStateStr = "FID250";
		List<String> userInputs = new ArrayList<>();
		userInputs.add("1");
		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, "1");
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);
		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_5);

		String nextStateStr600 = "FID300";
		IVRWebHookResponseDto mockHookResponse300 = new IVRWebHookResponseDto();
		mockHookResponse300.setCurrentState(nextStateStr600);
		mockHookResponse300.setHookReturnCode(HOOK_RETURN_3);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(mockIvrLfacsService.processFID250Code(sessionId, nextStateStr, 1)).thenReturn(mockHookResponse);
		when(mockIvrLfacsService.processFID300Code(sessionId, nextStateStr600)).thenReturn(mockHookResponse300);

		IVRWebHookResponseDto responseHookObject = ivrService.processLFACS(sessionId, currentStateStr, userInputs);
		assertEquals(HOOK_RETURN_3, responseHookObject.getHookReturnCode());
	}

	@Test
	void testGetSanityResponse() {
		IVRWebHookResponseDto response = ivrService.getSanityResponse();
		assertNotNull(response);
	}


	@Test
	void testProcessLFACS_FID025() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentStateStr = "FI0025";
		String nextStateStr = "FID025";
		List<String> userInputs = new ArrayList<>();
		userInputs.add("");
		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, DIRECT_STATE_TRANSFER);
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);
		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_0);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(mockIvrLfacsService.processFID025Code(sessionId, nextStateStr, "")).thenReturn(mockHookResponse);

		IVRWebHookResponseDto responseHookObject = ivrService.processLFACS(sessionId, currentStateStr, userInputs);
		assertEquals(HOOK_RETURN_0, responseHookObject.getHookReturnCode());
	}

	@Test
	void testProcessLFACS_FID045() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentStateStr = "FI0030";
		String nextStateStr = "FID045";
		List<String> userInputs = new ArrayList<>();
		userInputs.add("2");
		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, "2");
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);
		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_0);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(mockIvrLfacsService.processFID045Code(sessionId, nextStateStr)).thenReturn(mockHookResponse);

		IVRWebHookResponseDto responseHookObject = ivrService.processLFACS(sessionId, currentStateStr, userInputs);
		assertEquals(HOOK_RETURN_0, responseHookObject.getHookReturnCode());
	}

	@Test
	void testProcessLFACS_FID515() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentStateStr = "FI0510";
		String nextStateStr = "FID515";
		List<String> userInputs = new ArrayList<>();
		userInputs.add("");
		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, DIRECT_STATE_TRANSFER);
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);
		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_0);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(mockIvrLfacsService.processFID515Code(sessionId, nextStateStr)).thenReturn(mockHookResponse);

		IVRWebHookResponseDto responseHookObject = ivrService.processLFACS(sessionId, currentStateStr, userInputs);
		assertEquals(HOOK_RETURN_0, responseHookObject.getHookReturnCode());
	}

	@Test
	void testProcessLFACS_FID525() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentStateStr = "FI0521";
		String nextStateStr = "FID525";
		List<String> userInputs = new ArrayList<>();
		userInputs.add("");
		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, DIRECT_STATE_TRANSFER);
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);
		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_0);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(mockIvrLfacsService.processFID525Code(sessionId, nextStateStr, userInputs)).thenReturn(mockHookResponse);

		IVRWebHookResponseDto responseHookObject = ivrService.processLFACS(sessionId, currentStateStr, userInputs);
		assertEquals(HOOK_RETURN_0, responseHookObject.getHookReturnCode());
	}

	@Test
	void testProcessLFACS_FID532() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentStateStr = "FI0530";
		String nextStateStr = "FID532";
		List<String> userInputs = new ArrayList<>();
		userInputs.add("0");
		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, "0");
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);
		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_0);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(mockIvrLfacsService.processFID532Code(sessionId, nextStateStr)).thenReturn(mockHookResponse);

		IVRWebHookResponseDto responseHookObject = ivrService.processLFACS(sessionId, currentStateStr, userInputs);
		assertEquals(HOOK_RETURN_0, responseHookObject.getHookReturnCode());
	}

	@Test
	void testProcessLFACS_FID535() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentStateStr = "FI0530";
		String nextStateStr = "FID535";
		List<String> userInputs = new ArrayList<>();
		userInputs.add("1");
		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, "1");
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);
		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_0);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(mockIvrLfacsService.processFID535Code(sessionId, nextStateStr)).thenReturn(mockHookResponse);

		IVRWebHookResponseDto responseHookObject = ivrService.processLFACS(sessionId, currentStateStr, userInputs);
		assertEquals(HOOK_RETURN_0, responseHookObject.getHookReturnCode());
	}

	@Test
	void testProcessLFACS_FID560() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentStateStr = "FI0441";
		String nextStateStr = "FID560";
		List<String> userInputs = new ArrayList<>();
		userInputs.add("6");
		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, "6");
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);
		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_0);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(mockIvrLfacsService.processFID560Code(sessionId, nextStateStr)).thenReturn(mockHookResponse);

		IVRWebHookResponseDto responseHookObject = ivrService.processLFACS(sessionId, currentStateStr, userInputs);
		assertEquals(HOOK_RETURN_0, responseHookObject.getHookReturnCode());
	}

	@Test
	void testProcessLFACS_FID500() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentStateStr = "FI0050";
		String nextStateStr = "FID500";
		List<String> userInputs = new ArrayList<>();
		userInputs.add("1");
		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, "1");
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);
		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_0);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(mockIvrLfacsService.processFID500Code(sessionId, nextStateStr, userInputs)).thenReturn(mockHookResponse);


		IVRWebHookResponseDto responseHookObject = ivrService.processLFACS(sessionId, currentStateStr, userInputs);
		assertEquals(HOOK_RETURN_0, responseHookObject.getHookReturnCode());
	}

	@Test
	void testProcessLFACS_FID055() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentStateStr = "FI0050";
		String nextStateStr = "FID500";
		String nextStateStr1 = "FID055";
		List<String> userInputs = new ArrayList<>();
		userInputs.add("2");
		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, "2");
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);
		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_6);

		IVRWebHookResponseDto mockHookResponseNew = new IVRWebHookResponseDto();
		mockHookResponseNew.setCurrentState(nextStateStr);
		mockHookResponseNew.setHookReturnCode(HOOK_RETURN_0);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(mockIvrLfacsService.processFID500Code(sessionId, nextStateStr, userInputs)).thenReturn(mockHookResponse);
		when(mockIvrLfacsService.processFID055Code(sessionId, nextStateStr1, userInputs.get(0))).thenReturn(mockHookResponseNew);

		IVRWebHookResponseDto responseHookObject = ivrService.processLFACS(sessionId, currentStateStr, userInputs);
		assertEquals(HOOK_RETURN_0, responseHookObject.getHookReturnCode());
	}


	@Test
	void testProcessLFACS_FI0030_FID600_FID080_TN_FLOW() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentStateStr = "FI0030";
		String nextStateStr = "FID600";
		String nextStateStr1 = "FID080";
		List<String> userInputs = new ArrayList<>();
		userInputs.add("4");
		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, "4");
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);
		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_1);

		IVRWebHookResponseDto mockHookResponseNew = new IVRWebHookResponseDto();
		mockHookResponseNew.setCurrentState(nextStateStr);
		mockHookResponseNew.setHookReturnCode(HOOK_RETURN_0);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(mockIvrLfacsService.processFID600Code(sessionId, nextStateStr)).thenReturn(mockHookResponse);
		when(mockIvrLfacsService.processFID080Code(sessionId, nextStateStr1)).thenReturn(mockHookResponseNew);

		IVRWebHookResponseDto responseHookObject = ivrService.processLFACS(sessionId, currentStateStr, userInputs);
		assertEquals(HOOK_RETURN_0, responseHookObject.getHookReturnCode());
	}

	@Test
	void testProcessLFACS_FI0240_FID250_FID600_FID285_CP_FLOW() throws JsonMappingException, JsonProcessingException {
		String sessionId = "session123";
		String currentStateStr = "FI0240";
		String nextStateStr = "FID250";
		String nextStateStr1 = "FID600";
		String nextStateStr2 = "FID285";
		List<String> userInputs = new ArrayList<>();
		userInputs.add("4");
		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, "4");
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);
		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_4);
		IVRWebHookResponseDto mockHookResponse2 = new IVRWebHookResponseDto();
		mockHookResponse2.setHookReturnCode(HOOK_RETURN_2);

		IVRWebHookResponseDto mockHookResponseNew = new IVRWebHookResponseDto();
		mockHookResponseNew.setCurrentState(nextStateStr);
		mockHookResponseNew.setHookReturnCode(HOOK_RETURN_0);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(mockIvrLfacsService.processFID250Code(sessionId, nextStateStr, 4)).thenReturn(mockHookResponse);
		when(mockIvrLfacsService.processFID600Code(sessionId, nextStateStr1)).thenReturn(mockHookResponse2);
		when(mockIvrLfacsService.processFID285Code(sessionId, nextStateStr2)).thenReturn(mockHookResponseNew);

		IVRWebHookResponseDto responseHookObject = ivrService.processLFACS(sessionId, currentStateStr, userInputs);
		assertEquals(HOOK_RETURN_0, responseHookObject.getHookReturnCode());
	}

	@Test
	void testProcessLFACS_FID274() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentStateStr = "FI0240";
		String nextStateStr = "FID274";

		List<String> userInputs = new ArrayList<>();
		userInputs.add("6");
		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, "6");
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);
		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_0);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(mockIvrLfacsService.processFID274Code(sessionId, nextStateStr)).thenReturn(mockHookResponse);

		IVRWebHookResponseDto responseHookObject = ivrService.processLFACS(sessionId, currentStateStr, userInputs);
		assertEquals(HOOK_RETURN_0, responseHookObject.getHookReturnCode());
	}

	@Test
	void testProcessLFACS_FID291() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentStateStr = "FI0279";
		String nextStateStr = "FID291";

		List<String> userInputs = new ArrayList<>();
		userInputs.add("3");
		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, "3");
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);
		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_2);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(mockIvrLfacsService.processFID291Code(sessionId, nextStateStr)).thenReturn(mockHookResponse);

		IVRWebHookResponseDto responseHookObject = ivrService.processLFACS(sessionId, currentStateStr, userInputs);
		assertEquals(HOOK_RETURN_2, responseHookObject.getHookReturnCode());
	}

	@Test
	void testProcessLFACS_FID630() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentStateStr = "FI0627";
		String nextStateStr = "FID630";
		List<String> userInputs = new ArrayList<>();
		userInputs.add("");
		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, DIRECT_STATE_TRANSFER);
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);
		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_1);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(mockIvrLfacsService.processFID630Code(sessionId, nextStateStr, 0)).thenReturn(mockHookResponse);

		IVRWebHookResponseDto responseHookObject = ivrService.processLFACS(sessionId, currentStateStr, userInputs);
		assertEquals(HOOK_RETURN_1, responseHookObject.getHookReturnCode());
	}

	@Test
	void testProcessCNF_FND035_Success() throws JsonMappingException, JsonProcessingException {
		String sessionId = "session123";
		String currentStateStr = "FN0030";
		String nextStateStr = "FND035";
		List<String> userInputs = new ArrayList<>();
		userInputs.add("*2134*1234");
		userInputs.add("1");
		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, DIRECT_STATE_TRANSFER);
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);
		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_2);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(mockIvrCnfService.processFND035(sessionId, nextStateStr, userInputs)).thenReturn(mockHookResponse);

		IVRWebHookResponseDto responseHookObject = ivrService.processCNF(sessionId, currentStateStr, userInputs);
		assertEquals(HOOK_RETURN_2, responseHookObject.getHookReturnCode());
	}

	@Test
	void testProcessCNF_FND055_Success() throws JsonMappingException, JsonProcessingException {
		String sessionId = "session123";
		String currentStateStr = "FN0055";
		String nextStateStr = "FND055";
		List<String> userInputs = new ArrayList<>();
		userInputs.add("12");
		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, DIRECT_STATE_TRANSFER);
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);
		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_1);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(mockIvrCnfService.processFND055(sessionId, nextStateStr, userInputs.get(0))).thenReturn(mockHookResponse);

		IVRWebHookResponseDto responseHookObject = ivrService.processCNF(sessionId, currentStateStr, userInputs);
		assertEquals(HOOK_RETURN_1, responseHookObject.getHookReturnCode());
	}

	@Test
	void testProcessCNF_FND059_Success() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentStateStr = "FN0056";
		String nextStateStr = "FND059";
		String nextState060 = "FND060";
		List<String> userInputs = new ArrayList<>();
		userInputs.add("12");
		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, DIRECT_STATE_TRANSFER);
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);
		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_0);

		IVRWebHookResponseDto mockHookResponse060 = new IVRWebHookResponseDto();
		mockHookResponse060.setCurrentState(nextStateStr);
		mockHookResponse060.setHookReturnCode(HOOK_RETURN_8);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(mockIvrCnfService.processFND059(sessionId, nextStateStr)).thenReturn(mockHookResponse);
		when(mockIvrCnfService.processFND060(sessionId, nextState060)).thenReturn(mockHookResponse060);

		IVRWebHookResponseDto responseHookObject = ivrService.processCNF(sessionId, currentStateStr, userInputs);
		assertEquals(HOOK_RETURN_8, responseHookObject.getHookReturnCode());
	}

	@Test
	void testProcessCNF_FND075_Success() throws JsonMappingException, JsonProcessingException {
		String sessionId = "session123";
		String currentStateStr = "FN0070";
		String nextStateStr = "FND075";
		List<String> userInputs = new ArrayList<>();
		userInputs.add("12");
		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, DIRECT_STATE_TRANSFER);
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);
		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_1);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(mockIvrCnfService.processFND075(sessionId, nextStateStr, userInputs.get(0))).thenReturn(mockHookResponse);

		IVRWebHookResponseDto responseHookObject = ivrService.processCNF(sessionId, currentStateStr, userInputs);
		assertEquals(HOOK_RETURN_1, responseHookObject.getHookReturnCode());
	}

	@Test
	void testProcessCNF_FND085_Success() throws JsonMappingException, JsonProcessingException {
		String sessionId = "session123";
		String currentStateStr = "FN0080";
		String nextStateStr = "FND085";
		String nextState090 = "FND090";
		String nextState135 = "FND135";
		List<String> userInputs = new ArrayList<>();
		userInputs.add("1");
		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, "1");
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);
		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_0);

		IVRWebHookResponseDto mockHookResponse090 = new IVRWebHookResponseDto();
		mockHookResponse090.setCurrentState(nextStateStr);
		mockHookResponse090.setHookReturnCode(HOOK_RETURN_8);

		IVRWebHookResponseDto mockHookResponse135 = new IVRWebHookResponseDto();
		mockHookResponse135.setCurrentState(nextStateStr);
		mockHookResponse135.setHookReturnCode(HOOK_RETURN_0);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(mockIvrCnfService.processFND085(sessionId, nextStateStr)).thenReturn(mockHookResponse);
		when(mockIvrCnfService.processFND090(sessionId, nextState090)).thenReturn(mockHookResponse090);
		when(mockIvrCnfService.processFND135(sessionId, nextState135)).thenReturn(mockHookResponse135);

		IVRWebHookResponseDto responseHookObject = ivrService.processCNF(sessionId, currentStateStr, userInputs);
		assertEquals(HOOK_RETURN_0, responseHookObject.getHookReturnCode());
	}

	@Test
	void testProcessCNF_FND145_Success() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentStateStr = "FN0140";
		String nextStateStr = "FND145";
		List<String> userInputs = new ArrayList<>();
		userInputs.add("2");
		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, DTMF_INPUT_2);
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);
		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_3);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(mockIvrCnfService.processFND145(sessionId, nextStateStr, userInputs.get(0))).thenReturn(mockHookResponse);

		IVRWebHookResponseDto responseHookObject = ivrService.processCNF(sessionId, currentStateStr, userInputs);
		assertEquals(HOOK_RETURN_3, responseHookObject.getHookReturnCode());
	}

	@Test
	void testProcessCNF_FND155_Success() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentStateStr = "FN0150";
		String nextStateStr = "FND155";
		List<String> userInputs = new ArrayList<>();
		userInputs.add("2");
		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, DTMF_INPUT_2);
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);
		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_1);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(mockIvrCnfService.processFND155(sessionId, nextStateStr, userInputs.get(0))).thenReturn(mockHookResponse);

		IVRWebHookResponseDto responseHookObject = ivrService.processCNF(sessionId, currentStateStr, userInputs);
		assertEquals(HOOK_RETURN_1, responseHookObject.getHookReturnCode());
	}

	@Test
	void testProcessCNF_FND170_Success() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentStateStr = "FN0160";
		String nextStateStr = "FND170";
		List<String> userInputs = new ArrayList<>();
		userInputs.add("1");
		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, DTMF_INPUT_1);
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);
		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_0);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(mockIvrCnfService.processFND170(sessionId, nextStateStr, userInputs.get(0))).thenReturn(mockHookResponse);

		IVRWebHookResponseDto responseHookObject = ivrService.processCNF(sessionId, currentStateStr, userInputs);
		assertEquals(HOOK_RETURN_0, responseHookObject.getHookReturnCode());
	}

	@Test
	void testProcessMLT_MLD021() throws JsonMappingException, JsonProcessingException {
		String sessionId = "session123";
		String currentStateStr = "ML0020";
		String nextStateStr = "MLD021";
		List<String> userInputs = new ArrayList<>();
		userInputs.add("1");
		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, "1");
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);
		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_0);
		IVRUserSession session = new IVRUserSession();
		session.setSessionId(sessionId);
		IvrMltSession mltSession = new IvrMltSession();
		session.setSessionId(sessionId);
		mltSession.setSessionId(sessionId);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(mockIvrCacheService.getBySessionId(sessionId)).thenReturn(session);
		when(mockMltCacheService.getBySessionId(sessionId)).thenReturn(null);
		when(ivrMltService.processMLD021(sessionId, nextStateStr, userInputs)).thenReturn(mockHookResponse);

		IVRWebHookResponseDto responseHookObject = ivrService.processMLT(sessionId, currentStateStr, userInputs);
		assertEquals(HOOK_RETURN_0, responseHookObject.getHookReturnCode());
	}

	@Test
	void testProcessCNF_FND700_Success() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentStateStr = "FN0700";
		String nextStateStr = "FND700";
		List<String> userInputs = new ArrayList<>();
		userInputs.add("");
		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, DIRECT_STATE_TRANSFER);
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);
		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_3);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(mockIvrCnfService.processFND700(sessionId, nextStateStr, userInputs.get(0))).thenReturn(mockHookResponse);

		IVRWebHookResponseDto responseHookObject = ivrService.processCNF(sessionId, currentStateStr, userInputs);
		assertEquals(HOOK_RETURN_3, responseHookObject.getHookReturnCode());
	}

	@Test
	void testProcessCNF_FND740_Success() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentStateStr = "FN0730";
		String nextStateStr = "FND740";
		String nextState741 = "FND741";
		List<String> userInputs = new ArrayList<>();
		userInputs.add("12");
		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, DIRECT_STATE_TRANSFER);
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);
		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_1);

		IVRWebHookResponseDto mockHookResponse741 = new IVRWebHookResponseDto();
		mockHookResponse741.setCurrentState(nextState741);
		mockHookResponse741.setHookReturnCode(HOOK_RETURN_2);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(mockIvrCnfService.processFND740(sessionId, nextStateStr, userInputs)).thenReturn(mockHookResponse);
		when(mockIvrCnfService.processFND741(sessionId, nextState741, userInputs)).thenReturn(mockHookResponse741);

		IVRWebHookResponseDto responseHookObject = ivrService.processCNF(sessionId, currentStateStr, userInputs);
		assertEquals(HOOK_RETURN_2, responseHookObject.getHookReturnCode());
	}
	
	
	
	@Test
	void testProcessCNF_FND141_Success() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentStateStr = "FN0140";
		String nextStateStr = "FND141";
		String nextState741 = "FND143";
		String nextState145 = "FND145";
		List<String> userInputs = new ArrayList<>();
		userInputs.add("12");
		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, DIRECT_STATE_TRANSFER);
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);
		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_0);

		IVRWebHookResponseDto mockHookResponse741 = new IVRWebHookResponseDto();
		mockHookResponse741.setCurrentState(nextState741);
		mockHookResponse741.setHookReturnCode(HOOK_RETURN_8);
		
		IVRWebHookResponseDto mockHookResponse145 = new IVRWebHookResponseDto();
		mockHookResponse145.setCurrentState(nextState145);
		mockHookResponse145.setHookReturnCode(HOOK_RETURN_1);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(mockIvrCnfService.processFND141(sessionId, nextStateStr)).thenReturn(mockHookResponse);
		when(mockIvrCnfService.processFND143(sessionId, nextState741)).thenReturn(mockHookResponse741);
		when(mockIvrCnfService.processFND145(sessionId, nextState145,DTMF_INPUT_8)).thenReturn(mockHookResponse145);
		

		IVRWebHookResponseDto responseHookObject = ivrService.processCNF(sessionId, currentStateStr, userInputs);
		
		assertEquals(HOOK_RETURN_1, responseHookObject.getHookReturnCode());
	}

	
	

	@Test
	void testProcessCNF_FND742_Success() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentStateStr = "FN0730";
		String nextStateStr = "FND740";
		String nextState742 = "FND742";
		List<String> userInputs = new ArrayList<>();
		userInputs.add("12");
		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, DIRECT_STATE_TRANSFER);
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);
		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_2);

		IVRWebHookResponseDto mockHookResponse742 = new IVRWebHookResponseDto();
		mockHookResponse742.setCurrentState(nextState742);
		mockHookResponse742.setHookReturnCode(HOOK_RETURN_2);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(mockIvrCnfService.processFND740(sessionId, nextStateStr, userInputs)).thenReturn(mockHookResponse);
		when(mockIvrCnfService.processFND742(sessionId, nextState742, userInputs)).thenReturn(mockHookResponse742);

		IVRWebHookResponseDto responseHookObject = ivrService.processCNF(sessionId, currentStateStr, userInputs);
		assertEquals(HOOK_RETURN_2, responseHookObject.getHookReturnCode());
	}

	@Test
	void testProcessCNF_FND215_Success() throws JsonMappingException, JsonProcessingException {

		String sessionId = "session123";
		String currentStateStr = "FN0200";
		String nextStateStr = "FND215";
		List<String> userInputs = new ArrayList<>();
		userInputs.add("1");
		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, DTMF_INPUT_1);
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);
		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_1);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(mockIvrCnfService.processFND215(sessionId, currentStateStr, nextStateStr, userInputs.get(0))).thenReturn(mockHookResponse);
		when(mockIvrCnfService.processFND216(sessionId, "FND216")).thenReturn(mockHookResponse);

		IVRWebHookResponseDto responseHookObject = ivrService.processCNF(sessionId, currentStateStr, userInputs);
		assertEquals(HOOK_RETURN_1, responseHookObject.getHookReturnCode());
	}

	
	@Test
	void testProcessAdministrationForGetAreaCode() {
		String sessionId = "session123";
		String currentStateStr = "AD0020";
		String nextStateStr = "ADD100";
		List<String> userInputs = new ArrayList<>();
		userInputs.add("1");

		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, userInputs.get(0));
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);

		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_0);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(ivrAdminInterface.getCurrentAreaCode(sessionId)).thenReturn(mockHookResponse);

		IVRWebHookResponseDto responseHookObject = ivrService.processAdministration(sessionId, currentStateStr, userInputs);

		assertEquals(HOOK_RETURN_0, responseHookObject.getHookReturnCode());
		verify(ivrAdminInterface, times(1)).getCurrentAreaCode(anyString());
	}

	@Test
	void testProcessAdministrationForVoiceResults() {
		String sessionId = "session123";
		String currentStateStr = "AD0020";
		String nextStateStr = "ADD555";
		List<String> userInputs = new ArrayList<>();
		userInputs.add("1");

		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, userInputs.get(0));
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);

		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_0);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(ivrAdminInterface.voiceResults(sessionId)).thenReturn(mockHookResponse);

		IVRWebHookResponseDto responseHookObject = ivrService.processAdministration(sessionId, currentStateStr, userInputs);

		assertEquals(HOOK_RETURN_0, responseHookObject.getHookReturnCode());
		verify(ivrAdminInterface, times(1)).voiceResults(anyString());
	}

	@Test
	void testProcessAdministrationForValidateAreaCode() {
		String sessionId = "session123";
		String currentStateStr = "AD0020";
		String nextStateStr = "ADD110";
		List<String> userInputs = new ArrayList<>();
		userInputs.add("1");

		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, userInputs.get(0));
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);

		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_2);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(ivrAdminInterface.validateAreaCode(userInputs.get(0), sessionId)).thenReturn(mockHookResponse);

		IVRWebHookResponseDto responseHookObject = ivrService.processAdministration(sessionId, currentStateStr, userInputs);

		assertEquals(HOOK_RETURN_2, responseHookObject.getHookReturnCode());
		verify(ivrAdminInterface, times(1)).validateAreaCode(anyString(), anyString());

	}

	@Test
	void testProcessAdministrationForUpdateAreaCode() {
		String sessionId = "session123";
		String currentStateStr = "AD0020";
		String nextStateStr = "ADD500";
		List<String> userInputs = new ArrayList<>();
		userInputs.add("1");

		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, userInputs.get(0));
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);

		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_0);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(ivrAdminInterface.setTechNPARequest(userInputs.get(0), sessionId)).thenReturn(mockHookResponse);

		IVRWebHookResponseDto responseHookObject = ivrService.processAdministration(sessionId, currentStateStr, userInputs);

		assertEquals(HOOK_RETURN_0, responseHookObject.getHookReturnCode());
		verify(ivrAdminInterface, times(1)).setTechNPARequest(anyString(), anyString());

	}

	@Test
	void testProcessAdministrationForVariableAreaCode() {
		String sessionId = "session123";
		String currentStateStr = "MM00011";
		String nextStateStr = "ADD010";
		List<String> userInputs = new ArrayList<>();
		userInputs.add("1");

		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, userInputs.get(0));
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);

		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_0);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(ivrAdminInterface.validateVariableAreaCode(userInputs.get(0), sessionId)).thenReturn(mockHookResponse);

		IVRWebHookResponseDto responseHookObject = ivrService.processAdministration(sessionId, currentStateStr, userInputs);

		assertEquals(HOOK_RETURN_0, responseHookObject.getHookReturnCode());
		verify(ivrAdminInterface, times(1)).validateVariableAreaCode(anyString(), anyString());

	}
	
	

	@Test
	void testProcessAdministrationForUpdateVariableAreaCode() {
		String sessionId = "session123";
		String currentStateStr = "AD0010";
		String nextStateStr = "ADD550";
		List<String> userInputs = new ArrayList<>();
		userInputs.add("1");

		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, userInputs.get(0));
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);

		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_0);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(ivrAdminInterface.issueSetVarNpaRequest(userInputs.get(0), sessionId)).thenReturn(mockHookResponse);

		IVRWebHookResponseDto responseHookObject = ivrService.processAdministration(sessionId, currentStateStr, userInputs);

		assertEquals(HOOK_RETURN_0, responseHookObject.getHookReturnCode());
		verify(ivrAdminInterface, times(1)).issueSetVarNpaRequest(anyString(), anyString());

	}


	@Test
	void testProcessAdministrationForValidatePassword() {
		String sessionId = "session123";
		String currentStateStr = "AD0020";
		String nextStateStr = "ADD020";
		List<String> userInputs = new ArrayList<>();
		userInputs.add("1");

		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, userInputs.get(0));
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);

		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_2);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(ivrAdminInterface.validatePassword(userInputs.get(0), sessionId)).thenReturn(mockHookResponse);

		IVRWebHookResponseDto responseHookObject = ivrService.processAdministration(sessionId, currentStateStr, userInputs);

		assertEquals(HOOK_RETURN_2, responseHookObject.getHookReturnCode());
		verify(ivrAdminInterface, times(1)).validatePassword(anyString(), anyString());

	}

	@Test
	void testValidatePasswordBusinessRules_sameAsCurrentPassword() {

		String sessionId = "session123";
		String currentStateStr = "AD0020";
		String nextStateStr = "ADD020";
		List<String> userInputs = new ArrayList<>();
		userInputs.add("1");

		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, userInputs.get(0));
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);

		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_0);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(ivrAdminInterface.validatePassword(userInputs.get(0), sessionId)).thenReturn(mockHookResponse);

		IVRWebHookResponseDto responseHookObject = ivrService.processAdministration(sessionId, currentStateStr, userInputs);

		assertEquals(HOOK_RETURN_0, responseHookObject.getHookReturnCode());
		verify(ivrAdminInterface, times(1)).validatePassword(anyString(), anyString());
		
	}

	@Test
	void testValidatePasswordBusinessRules_lengthConstraint_MIN() {

		String sessionId = "session123";
		String currentStateStr = "AD0020";
		String nextStateStr = "ADD020";
		List<String> userInputs = new ArrayList<>();
		userInputs.add("1");

		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, userInputs.get(0));
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);

	}
	
	@Test
	void testProcessChangeStatusCablePair_FPD011() throws JsonMappingException, JsonProcessingException {
		
		String sessionId = "session123";
		String currentStateStr = "FP0010";
		String nextStateStr = "FPD011";
		String userInput = "2300625";
		List<String> userInputs = new ArrayList<>();
		userInputs.add(userInput);
		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, DIRECT_STATE_TRANSFER);
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);
		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_1);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(cablePairServiceImpl.processFPD011StateCode(sessionId, nextStateStr, userInputs.get(0))).thenReturn(mockHookResponse);

		IVRWebHookResponseDto responseHookObject = ivrService.processChangeStatusOfCablePair(sessionId, currentStateStr, userInputs);
		assertEquals(HOOK_RETURN_1, responseHookObject.getHookReturnCode());
	}
	
	@Test
	void testProcessChangeStatusCablePair_FPD005() throws JsonMappingException, JsonProcessingException {
		
		String sessionId = "session123";
		String currentStateStr = "MM0001";
		String nextStateStr = "FPD005";
		String userInput = "";
		List<String> userInputs = new ArrayList<>();
		userInputs.add(userInput);
		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, DIRECT_STATE_TRANSFER);
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);
		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_0);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(cablePairServiceImpl.processFPD005StateCode(sessionId,currentStateStr, nextStateStr)).thenReturn(mockHookResponse);

		IVRWebHookResponseDto responseHookObject = ivrService.processChangeStatusOfCablePair(sessionId, currentStateStr, userInputs);
		assertEquals(HOOK_RETURN_0, responseHookObject.getHookReturnCode());
	}
	
	@Test
	void testProcessChangeStatusCablePair_FPD005_FromAnotherFlow() throws JsonMappingException, JsonProcessingException {
		
		String sessionId = "session123";
		String currentStateStr = "FND741";
		String nextStateStr = "FPD005";
		String userInput = "";
		List<String> userInputs = new ArrayList<>();
		userInputs.add(userInput);
		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, DIRECT_STATE_TRANSFER);
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);
		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_1);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(cablePairServiceImpl.processFPD005StateCode(sessionId,currentStateStr, nextStateStr)).thenReturn(mockHookResponse);

		IVRWebHookResponseDto responseHookObject = ivrService.processChangeStatusOfCablePair(sessionId, currentStateStr, userInputs);
		assertEquals(HOOK_RETURN_1, responseHookObject.getHookReturnCode());
	}
	
	@Test
	void testProcessChangeStatusCablePair_FPD060() throws JsonMappingException, JsonProcessingException {
		
		String sessionId = "session123";
		String currentStateStr = "FP0040";
		String nextStateStr = "FPD060";
		
		List<String> userInputs = Stream.of("10","605","5").collect(Collectors.toList());
		
		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, DIRECT_STATE_TRANSFER);
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);
		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_1);
		
		IVRWebHookResponseDto mockHookResponse1 = new IVRWebHookResponseDto();
		mockHookResponse1.setCurrentState(nextStateStr);
		mockHookResponse1.setHookReturnCode(HOOK_RETURN_3);
		
		IVRWebHookResponseDto mockHookResponse2 = new IVRWebHookResponseDto();
		mockHookResponse2.setCurrentState(nextStateStr);
		mockHookResponse2.setHookReturnCode(HOOK_RETURN_8);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(cablePairServiceImpl.processFPD060DefectiveCode(sessionId, currentStateStr, nextStateStr,userInputs.get(2))).thenReturn(mockHookResponse);
		when(cablePairServiceImpl.processFPD060StateCode(any(), any(), any(), any())).thenReturn(mockHookResponse1);
		when(cablePairServiceImpl.processFPD100StateCode(any())).thenReturn(mockHookResponse2);

		IVRWebHookResponseDto responseHookObject = ivrService.processChangeStatusOfCablePair(sessionId, currentStateStr, userInputs);
		assertEquals(HOOK_RETURN_8, responseHookObject.getHookReturnCode());
	}
	
	
	@Test
	void testValidatePasswordBusinessRules_lengthConstraint_MAX() {

		String sessionId = "session123";
		String currentStateStr = "AD0020";
		String nextStateStr = "ADD020";
		List<String> userInputs = new ArrayList<>();
		userInputs.add("1");

		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, userInputs.get(0));
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);

		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_1);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(ivrAdminInterface.validatePassword(userInputs.get(0), sessionId)).thenReturn(mockHookResponse);

		IVRWebHookResponseDto responseHookObject = ivrService.processAdministration(sessionId, currentStateStr, userInputs);

		assertEquals(HOOK_RETURN_1, responseHookObject.getHookReturnCode());
		verify(ivrAdminInterface, times(1)).validatePassword(anyString(), anyString());
	}
	
	@Test
	void testValidatePasswordBusinessRules_validPassword() {
		String sessionId = "session123";
		String currentStateStr = "AD0020";
		String nextStateStr = "ADD020";
		List<String> userInputs = new ArrayList<>();
		userInputs.add("1");

		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, userInputs.get(0));
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);

		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_3);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(ivrAdminInterface.validatePassword(userInputs.get(0), sessionId)).thenReturn(mockHookResponse);

		IVRWebHookResponseDto responseHookObject = ivrService.processAdministration(sessionId, currentStateStr, userInputs);

		assertEquals(HOOK_RETURN_3, responseHookObject.getHookReturnCode());
		verify(ivrAdminInterface, times(1)).validatePassword(anyString(), anyString());
	}
	@Test
	void testIssueUserLoginWithNewPassword_Success() {

		String sessionId = "session123";
		String currentStateStr = "AD0020";
		String nextStateStr = "ADD035";
		List<String> userInputs = new ArrayList<>();
		userInputs.add("1");

		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, userInputs.get(0));
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);

		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_2);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(ivrAdminInterface.issueUserLoginWithNewPassword(userInputs.get(0), sessionId)).thenReturn(mockHookResponse);

		IVRWebHookResponseDto responseHookObject = ivrService.processAdministration(sessionId, currentStateStr, userInputs);

		assertEquals(HOOK_RETURN_2, responseHookObject.getHookReturnCode());
		verify(ivrAdminInterface, times(1)).issueUserLoginWithNewPassword(anyString(), anyString());
	}
	


	@Test
	void testIssueUserLoginWithNewPassword_GPDOWN() {

		String sessionId = "session123";
		String currentStateStr = "AD0020";
		String nextStateStr = "ADD035";
		List<String> userInputs = new ArrayList<>();
		userInputs.add("1");

		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, userInputs.get(0));
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);

		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(GPDOWN_ERR_MSG_CODE);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(ivrAdminInterface.issueUserLoginWithNewPassword(userInputs.get(0), sessionId)).thenReturn(mockHookResponse);

		IVRWebHookResponseDto responseHookObject = ivrService.processAdministration(sessionId, currentStateStr, userInputs);

		assertEquals(GPDOWN_ERR_MSG_CODE, responseHookObject.getHookReturnCode());
		verify(ivrAdminInterface, times(1)).issueUserLoginWithNewPassword(anyString(), anyString());
	}


	@Test
	void testProcessAdministrationForBadUserException() {
		String sessionId = "session123";
		String currentStateStr = "AD0020";
		String nextStateStr = "ADD110";
		List<String> userInputs = new ArrayList<>();
		userInputs.add("1");

		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, "");
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);

		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_2);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);

		Throwable exception = assertThrows(BadUserInputException.class ,
				() -> ivrService.processAdministration(sessionId, currentStateStr, userInputs));

		assertEquals("Cannot Obtain next State based on User Inputs", exception.getMessage());
		verify(ivrAdminInterface, times(0)).validateAreaCode(anyString(), anyString());

	}

	@Test
	void testProcessConstructionForOpeningNumber() throws JsonProcessingException {
		String sessionId = "session123";
		String currentStateStr = "CT0049";
		String nextStateStr = "CTD400";
		List<String> userInputs = new ArrayList<>();
		userInputs.add("1");

		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, userInputs.get(0));
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);

		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_9);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(ivrConstructionService.checkOpeningNumberExists(sessionId)).thenReturn(mockHookResponse);

		IVRWebHookResponseDto responseHookObject = ivrService.processConstructionActivity(sessionId, currentStateStr, userInputs);

		assertEquals(HOOK_RETURN_9, responseHookObject.getHookReturnCode());
		verify(ivrConstructionService, times(1)).checkOpeningNumberExists(anyString());

	}

	@Test
	void testProcessConstructionForParseTelephone() throws JsonProcessingException {
		String sessionId = "session123";
		String currentStateStr = "CT0049";
		String nextStateStr = "CTD403";
		List<String> userInputs = new ArrayList<>();
		userInputs.add("1");

		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, userInputs.get(0));
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);

		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_1);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(ivrConstructionService.parseTelephone(userInputs.get(0), sessionId)).thenReturn(mockHookResponse);

		IVRWebHookResponseDto responseHookObject = ivrService.processConstructionActivity(sessionId, currentStateStr, userInputs);

		assertEquals(HOOK_RETURN_1, responseHookObject.getHookReturnCode());
		verify(ivrConstructionService, times(1)).parseTelephone(anyString(), anyString());

	}

	@Test
	void testProcessConstructionForIssueReferTroubleReport() throws JsonProcessingException {
		String sessionId = "session123";
		String currentStateStr = "CT0049";
		String nextStateStr = "CTD410";
		List<String> userInputs = new ArrayList<>();
		userInputs.add("1");

		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, userInputs.get(0));
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);

		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_9);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(ivrConstructionService.issueReferTroubleReport(userInputs.get(0), sessionId)).thenReturn(mockHookResponse);

		IVRWebHookResponseDto responseHookObject = ivrService.processConstructionActivity(sessionId, currentStateStr, userInputs);

		assertEquals(HOOK_RETURN_9, responseHookObject.getHookReturnCode());
		verify(ivrConstructionService, times(1)).issueReferTroubleReport(anyString(), anyString());

	}


	@Test
	void testProcessCANSTStateFTD240IfAlphaPager() throws JsonProcessingException, HttpTimeoutException, ExecutionException, InterruptedException {
		String sessionId = "session123";
		String currentStateStr = "FT0238";
		String nextStateStr = "FTD240";
		List<String> userInputs = new ArrayList<>();
		userInputs.add("1");

		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, userInputs.get(0));
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);

		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_2);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(ivrCanstService.processFTD240(sessionId)).thenReturn(mockHookResponse);

		IVRWebHookResponseDto responseHookObject = ivrService.processChangeorNewAssignServingTerminal(sessionId, currentStateStr, userInputs);

		assertEquals(HOOK_RETURN_2, responseHookObject.getHookReturnCode());
		verify(ivrCanstService, times(1)).processFTD240(anyString());

	}

	@Test
	void testProcessCANSTStateFTD240IfNotAlphaPager() throws JsonProcessingException, HttpTimeoutException, ExecutionException, InterruptedException {
		String sessionId = "session123";
		String currentStateStr = "FT0238";
		String nextStateStr = "FTD240";
		List<String> userInputs = new ArrayList<>();
		userInputs.add("1");

		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, userInputs.get(0));
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);

		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_1);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(ivrCanstService.processFTD240(sessionId)).thenReturn(mockHookResponse);

		IVRWebHookResponseDto responseHookObject = ivrService.processChangeorNewAssignServingTerminal(sessionId, currentStateStr, userInputs);

		assertEquals(HOOK_RETURN_1, responseHookObject.getHookReturnCode());
		verify(ivrCanstService, times(1)).processFTD240(anyString());

	}

	@Test
	void testProcessCANSTStateFTD231() throws JsonProcessingException, HttpTimeoutException, ExecutionException, InterruptedException {
		String sessionId = "session123";
		String currentStateStr = "FT0238";
		String nextStateStr = "FTD231";
		List<String> userInputs = new ArrayList<>();
		userInputs.add("1");

		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, userInputs.get(0));
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);

		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_1);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(ivrCanstService.processFTD231(sessionId)).thenReturn(mockHookResponse);

		IVRWebHookResponseDto responseHookObject = ivrService.processChangeorNewAssignServingTerminal(sessionId, currentStateStr, userInputs);

		assertEquals(HOOK_RETURN_1, responseHookObject.getHookReturnCode());
		verify(ivrCanstService, times(1)).processFTD231(anyString());

	}


	@Test
	void testProcessCANSTStateFTD400() throws JsonProcessingException, HttpTimeoutException, ExecutionException, InterruptedException {
		String sessionId = "session123";
		String currentStateStr = "FT0238";
		String nextStateStr = "FTD400";
		List<String> userInputs = new ArrayList<>();
		userInputs.add("1");

		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, userInputs.get(0));
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);

		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_1);
		mockHookResponse.setSessionId("sessionId");

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(ivrCanstService.processFTD400(sessionId)).thenReturn(mockHookResponse);

		IVRWebHookResponseDto responseHookObject = ivrService.processChangeorNewAssignServingTerminal(sessionId, currentStateStr, userInputs);

		assertEquals(HOOK_RETURN_1, responseHookObject.getHookReturnCode());
		verify(ivrCanstService, times(1)).processFTD400(anyString());

	}




	@Test
	void testProcessChangeStatusCablePair_FPD020() throws JsonMappingException, JsonProcessingException {
		
		String sessionId = "session123";
		String currentStateStr = "FP0020";
		String nextStateStr = "FPD020";
		String userInput = "*7141*57#";
		List<String> userInputs = new ArrayList<>();
		userInputs.add(userInput);
		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, DIRECT_STATE_TRANSFER);
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);
		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_3);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(cablePairServiceImpl.processFPD020StateCode(sessionId, nextStateStr, userInputs.get(0))).thenReturn(mockHookResponse);

		IVRWebHookResponseDto responseHookObject = ivrService.processChangeStatusOfCablePair(sessionId, currentStateStr, userInputs);
		assertEquals(HOOK_RETURN_3, responseHookObject.getHookReturnCode());
	}
	
	@Test
	void testProcessChangeStatusCablePair_FPD020_Error() throws JsonMappingException, JsonProcessingException {
		
		String sessionId = "session123";
		String currentStateStr = "FP0020";
		String nextStateStr = "FPD020";
		String userInput = "*714157#";
		List<String> userInputs = new ArrayList<>();
		userInputs.add(userInput);
		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, DIRECT_STATE_TRANSFER);
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);
		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_0);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(cablePairServiceImpl.processFPD020StateCode(sessionId, nextStateStr, userInputs.get(0))).thenReturn(mockHookResponse);

		IVRWebHookResponseDto responseHookObject = ivrService.processChangeStatusOfCablePair(sessionId, currentStateStr, userInputs);
		assertEquals(HOOK_RETURN_0, responseHookObject.getHookReturnCode());
	}

	@Test
	void testProcess_FTD370() throws JsonProcessingException, HttpTimeoutException, ExecutionException, InterruptedException {
		String sessionId = "session123";
		String currentStateStr = "FT0365";
		String nextStateStr = "FTD370";
		List<String> userInputs = new ArrayList<>();
		userInputs.add("1");

		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, userInputs.get(0));
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);
		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_0);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(ivrCanstService.processFTD370(sessionId, nextStateStr, userInputs)).thenReturn(mockHookResponse);

		IVRWebHookResponseDto responseHookObject = ivrService.processChangeorNewAssignServingTerminal(sessionId, currentStateStr, userInputs);
		assertEquals(HOOK_RETURN_0, responseHookObject.getHookReturnCode());

	}
	
	@Test
	void testProcess_FTD371() throws JsonMappingException, JsonProcessingException, HttpTimeoutException, ExecutionException, InterruptedException {

		String sessionId = "session123";
		String currentStateStr = "FTD370";
		String nextStateStr = "FTD371";
		List<String> userInputs = new ArrayList<>();
		userInputs.add("0");
		IVRState genesysState = new IVRState(currentStateStr);
		IVRState nextIVRState = new IVRState(nextStateStr);
		IVRStateTransition mockIvrStateStransition = new IVRStateTransition(nextIVRState, userInputs.get(0));
		genesysState.addTransitions(mockIvrStateStransition);
		Map<String, IVRState> mockStateMap = new HashMap<>();
		mockStateMap.put(currentStateStr, genesysState);
		IVRWebHookResponseDto mockHookResponse = new IVRWebHookResponseDto();
		mockHookResponse.setCurrentState(nextStateStr);
		mockHookResponse.setHookReturnCode(HOOK_RETURN_0);

		when(mockIvrStateSystem.getStateMap()).thenReturn(mockStateMap);
		when(ivrCanstService.processFTD371(sessionId, nextStateStr, userInputs)).thenReturn(mockHookResponse);

		IVRWebHookResponseDto responseHookObject = ivrService.processChangeorNewAssignServingTerminal(sessionId, currentStateStr, userInputs);
		assertEquals(HOOK_RETURN_0, responseHookObject.getHookReturnCode());
	}


	}
