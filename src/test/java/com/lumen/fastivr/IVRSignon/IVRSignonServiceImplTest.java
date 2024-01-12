package com.lumen.fastivr.IVRSignon;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_0;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_1;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_2;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_3;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_4;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_5;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_6;

import static com.lumen.fastivr.IVRUtils.IVRConstants.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lumen.fastivr.IVRCacheManagement.IVRCacheService;
import com.lumen.fastivr.IVRDto.IVRPagerConfig;
import com.lumen.fastivr.IVRDto.IVRParameter;
import com.lumen.fastivr.IVRDto.IVRWebHookResponseDto;
import com.lumen.fastivr.IVREntity.FastIvrUser;
import com.lumen.fastivr.IVRService.IVRService;
import com.lumen.fastivr.IVRSessionManagement.IVRUserSession;

@ExtendWith(MockitoExtension.class)
class IVRSignonServiceImplTest {
	
//	@Mock
//	private Map<String, IVRUserSession> mockUserSessionMap;
	
	@Mock
	private IVRCacheService mockCacheService;
	
	@Mock
	private IVRSignOnServiceHelper mockserviceHelper;
	
	@InjectMocks
	private IVRSignonServiceImpl signonService;

	@BeforeEach
	void setUp() throws Exception {
	}
	
	//SSD110
	@Test
	void testIssueUserLogin_newConversation_ValidTech() {
		String securityCode = "45632";
		String password = "123456";
		List<String> inputs = new ArrayList<>();
		inputs.add(securityCode);
		inputs.add(password);
		String sessionId = "session123";
		
		when(mockCacheService.getBySessionId(sessionId)).thenReturn(null);
		IVRUserSession session = new IVRUserSession();
		session.setSessionId(sessionId);
		session.setNewSession(true);
		when(mockCacheService.addSession(Mockito.any(IVRUserSession.class))).thenReturn(session);
		when(mockserviceHelper.validateSecurityCode(anyString(), Mockito.any(IVRUserSession.class))).thenReturn(HOOK_RETURN_1);
		when(mockserviceHelper.validateLoginPassword(anyString(), Mockito.any(IVRUserSession.class))).thenReturn(HOOK_RETURN_1);
		when(mockCacheService.updateSession(Mockito.any(IVRUserSession.class))).thenReturn(session);
		
		IVRWebHookResponseDto actualResponse = signonService.issueUserLogin(inputs, sessionId);
		
		assertEquals(HOOK_RETURN_1, actualResponse.getHookReturnCode());
	}
	
	@Test
	void testIssueUserLogin_newConversation_ValidTech_InvalidPassword() {
		String securityCode = "45632";
		String password = "123456";
		List<String> inputs = new ArrayList<>();
		inputs.add(securityCode);
		inputs.add(password);
		String sessionId = "session123";
		
		when(mockCacheService.getBySessionId(sessionId)).thenReturn(null);
		IVRUserSession session = new IVRUserSession();
		session.setSessionId(sessionId);
		session.setNewSession(true);
		when(mockCacheService.addSession(Mockito.any(IVRUserSession.class))).thenReturn(session);
		when(mockserviceHelper.validateSecurityCode(anyString(), Mockito.any(IVRUserSession.class))).thenReturn(HOOK_RETURN_1);
		when(mockserviceHelper.validateLoginPassword(anyString(), Mockito.any(IVRUserSession.class))).thenReturn(HOOK_RETURN_2);
		when(mockCacheService.updateSession(Mockito.any(IVRUserSession.class))).thenReturn(session);
		
		IVRWebHookResponseDto actualResponse = signonService.issueUserLogin(inputs, sessionId);
		
		assertEquals(HOOK_RETURN_2, actualResponse.getHookReturnCode());
	}
	
	@Test
	void testIssueUserLogin_newConversation_TechNotInDB() {
		String securityCode = "45632";
		String password = "123456";
		List<String> inputs = new ArrayList<>();
		inputs.add(securityCode);
		inputs.add(password);
		String sessionId = "session123";
		
		when(mockCacheService.getBySessionId(sessionId)).thenReturn(null);
		IVRUserSession session = new IVRUserSession();
		session.setSessionId(sessionId);
		session.setNewSession(true);
		when(mockCacheService.addSession(Mockito.any(IVRUserSession.class))).thenReturn(session);
		when(mockserviceHelper.validateSecurityCode(anyString(), Mockito.any(IVRUserSession.class))).thenReturn(HOOK_RETURN_0);
		when(mockCacheService.updateSession(Mockito.any(IVRUserSession.class))).thenReturn(session);
		
		IVRWebHookResponseDto actualResponse = signonService.issueUserLogin(inputs, sessionId);
		
		assertEquals(HOOK_RETURN_3, actualResponse.getHookReturnCode());
	}
	
	@Test
	void testIssueUserLogin_newConversation_TechNotEnabled() {
		String securityCode = "45632";
		String password = "123456";
		List<String> inputs = new ArrayList<>();
		inputs.add(securityCode);
		inputs.add(password);
		String sessionId = "session123";
		
		when(mockCacheService.getBySessionId(sessionId)).thenReturn(null);
		IVRUserSession session = new IVRUserSession();
		session.setSessionId(sessionId);
		session.setNewSession(true);
		when(mockCacheService.addSession(Mockito.any(IVRUserSession.class))).thenReturn(session);
		when(mockserviceHelper.validateSecurityCode(anyString(), Mockito.any(IVRUserSession.class))).thenReturn(HOOK_RETURN_2);
		when(mockCacheService.updateSession(Mockito.any(IVRUserSession.class))).thenReturn(session);
		
		IVRWebHookResponseDto actualResponse = signonService.issueUserLogin(inputs, sessionId);
		
		assertEquals(HOOK_RETURN_4, actualResponse.getHookReturnCode());
	}
	
	@Test
	void testIssueUserLogin_newConversation_EnabledTech_ResetPassword() {
		String securityCode = "45632";
		String password = "1";
		List<String> inputs = new ArrayList<>();
		inputs.add(securityCode);
		inputs.add(password);
		String sessionId = "session123";
		
		when(mockCacheService.getBySessionId(sessionId)).thenReturn(null);
		IVRUserSession session = new IVRUserSession();
		session.setSessionId(sessionId);
		session.setNewSession(true);
		when(mockCacheService.addSession(Mockito.any(IVRUserSession.class))).thenReturn(session);
		when(mockserviceHelper.validateSecurityCode(anyString(), Mockito.any(IVRUserSession.class))).thenReturn(HOOK_RETURN_1);
		when(mockCacheService.updateSession(Mockito.any(IVRUserSession.class))).thenReturn(session);
		
		IVRWebHookResponseDto actualResponse = signonService.issueUserLogin(inputs, sessionId);
		
		assertEquals(HOOK_RETURN_5, actualResponse.getHookReturnCode());
	}
	
	
	@Test
	void testIssueUserLogin_newConversation_TechEnabled_Jeopardy() {
		String securityCode = "45632";
		String password = "123456";
		List<String> inputs = new ArrayList<>();
		inputs.add(securityCode);
		inputs.add(password);
		String sessionId = "session123";
		
		when(mockCacheService.getBySessionId(sessionId)).thenReturn(null);
		IVRUserSession session = new IVRUserSession();
		session.setSessionId(sessionId);
		session.setNewSession(true);
		when(mockCacheService.addSession(Mockito.any(IVRUserSession.class))).thenReturn(session);
		when(mockserviceHelper.validateSecurityCode(anyString(), Mockito.any(IVRUserSession.class))).thenReturn(HOOK_RETURN_1);
		when(mockserviceHelper.validateLoginPassword(anyString(), Mockito.any(IVRUserSession.class))).thenReturn(HOOK_RETURN_6);
		when(mockCacheService.updateSession(Mockito.any(IVRUserSession.class))).thenReturn(session);
		
		IVRWebHookResponseDto actualResponse = signonService.issueUserLogin(inputs, sessionId);
		
		assertEquals(HOOK_RETURN_6, actualResponse.getHookReturnCode());
	}
	
	@Test
	void testIssueUserLogin_oldConversation_ValidTech() {
		String securityCode = "45632";
		String password = "123456";
		List<String> inputs = new ArrayList<>();
		inputs.add(securityCode);
		inputs.add(password);
		String sessionId = "session123";
		
		IVRUserSession mockUserSession = loadIvrSession(sessionId, securityCode, false);
		mockUserSession.setNewSession(false);
		mockUserSession.setEnabledFlag(true);
		//userSessionMap.put(sessionId, userSessionMock);
		FastIvrUser mockTech = loadMockFastIvrUser(securityCode, password, "Y", "24041994", "AD00211");
		
		//setup mock behavior of session map
		when(mockCacheService.getBySessionId(sessionId)).thenReturn(mockUserSession);
		when(mockserviceHelper.validateSecurityCode(securityCode, mockUserSession)).thenReturn(HOOK_RETURN_1);
		//setup mock behavior of helper class
		//when(mockserviceHelper.matchEmployeeID(securityCode)).thenReturn(mockTech);
		when(mockserviceHelper.validateLoginPassword(password, mockUserSession)).thenReturn(HOOK_RETURN_1);
		when(mockCacheService.updateSession(mockUserSession)).thenReturn(mockUserSession);
//		IVRPagerConfig pagerConfig = new IVRPagerConfig();
//		pagerConfig.setMailEnabled(true);
//		pagerConfig.setPhoneEnabled(true);
//		when(serviceHelperMock.loadTechPagerConfiguration(mockUserSession)).thenReturn(pagerConfig );
		IVRWebHookResponseDto actualResponse = signonService.issueUserLogin(inputs, sessionId);
		
		assertEquals(HOOK_RETURN_1, actualResponse.getHookReturnCode());
	}
	
	private IVRUserSession loadIvrSesion(String sessionId, boolean isAuthenticated) {
		IVRUserSession userSession = new IVRUserSession();
		userSession.setSessionId(sessionId);
		userSession.setAuthenticated(isAuthenticated);
		return userSession;
	}

	private IVRUserSession loadIvrSession(String sessionId, String callerSecurityCode, boolean isAuthenticated) {
		IVRUserSession userSession = new IVRUserSession();
		userSession.setSessionId(sessionId);
		userSession.setEmpID(callerSecurityCode);
		userSession.setAuthenticated(isAuthenticated);
		return userSession;
	}

	private FastIvrUser loadMockFastIvrUser(String securityCode, String password, String techEnabled, String birthdate,
			String cuid) {
		FastIvrUser mockTech = new FastIvrUser();
		mockTech.setEmpID(securityCode);
		mockTech.setPassword(password);
		mockTech.setCuid(cuid);
		mockTech.setBirthdate(birthdate);
		mockTech.setIsEnabledFlag(techEnabled);
		return mockTech;
	}

	@Test
	void testIssueValidateTechnicianProperties_Valid_Technician() {
		IVRUserSession mockUserSession = new IVRUserSession();
		mockUserSession.setSessionId("session123");
		mockUserSession.setPasswordExpireDate(LocalDate.now().plusDays(1L));
		mockUserSession.setCanBePagedMobile(true);
		mockUserSession.setCanBePagedEmail(true);
		mockUserSession.setBirthdate("04241994");
		
		//mock user session map
		when(mockCacheService.getBySessionId(mockUserSession.getSessionId())).thenReturn(mockUserSession);
		//mock service helper
		when(mockserviceHelper.hasUserExpired(mockUserSession.getPasswordExpireDate())).thenReturn(false);
		when(mockserviceHelper.isUserPagerEnabled(mockUserSession)).thenReturn(true);
		when(mockCacheService.updateSession(mockUserSession)).thenReturn(mockUserSession);
		
		String result = signonService.issueValidateTechnicianProperties(mockUserSession.getSessionId())
				.getHookReturnCode();
		assertEquals(HOOK_RETURN_1, result);
		
	}
	
	@Test
	void testIssueValidateTechnicianProperties_Password_Expired() {
		IVRUserSession mockUserSession = new IVRUserSession();
		mockUserSession.setSessionId("session123");
		mockUserSession.setPasswordExpireDate(LocalDate.now().plusDays(1L));
		
		//mock user session cache
		when(mockCacheService.getBySessionId(mockUserSession.getSessionId())).thenReturn(mockUserSession);
		//mock service helper
		when(mockserviceHelper.hasUserExpired((mockUserSession.getPasswordExpireDate()))).thenReturn(true);
		when(mockCacheService.updateSession(mockUserSession)).thenReturn(mockUserSession);
		
		String result = signonService.issueValidateTechnicianProperties(mockUserSession.getSessionId())
				.getHookReturnCode();
		assertEquals(HOOK_RETURN_2, result);
	}
	
//	@Test
//	void testIssueValidateTechnicianProperties_BirthDate_Missing() {
//		IVRUserSession mockUserSession = new IVRUserSession();
//		mockUserSession.setSessionId("session123");
//		mockUserSession.setPasswordExpireDate(LocalDate.now().plusDays(1L));
//		
//		//mock user session cache
//		when(mockCacheService.getBySessionId(mockUserSession.getSessionId())).thenReturn(mockUserSession);
//		//mock service helper
//		when(mockserviceHelper.hasUserExpired((mockUserSession.getPasswordExpireDate()))).thenReturn(false);
//		when(mockserviceHelper.isBirthdateSet(mockUserSession.getBirthdate())).thenReturn(false);
//		when(mockCacheService.updateSession(mockUserSession)).thenReturn(mockUserSession);
//		
//		String result = signonService.issueValidateTechnicianProperties(mockUserSession.getSessionId())
//				.getHookReturnCode();
//		assertEquals(HOOK_RETURN_3, result);
//	}
	
	@Test
	void testIssueValidateTechnicianProperties_Paging_Centre_Problem() {
		IVRUserSession mockUserSession = new IVRUserSession();
		mockUserSession.setSessionId("session123");
		mockUserSession.setCanBePagedMobile(false);
		mockUserSession.setCanBePagedEmail(false);
		mockUserSession.setPasswordExpireDate(LocalDate.now().plusDays(1L));
		
		//mock user session cache
		when(mockCacheService.getBySessionId(mockUserSession.getSessionId())).thenReturn(mockUserSession);
		//mock service helper
		when(mockserviceHelper.hasUserExpired((mockUserSession.getPasswordExpireDate()))).thenReturn(false);
		when(mockCacheService.updateSession(mockUserSession)).thenReturn(mockUserSession);
		
		String result = signonService.issueValidateTechnicianProperties(mockUserSession.getSessionId())
				.getHookReturnCode();
		assertEquals(HOOK_RETURN_4, result);
	}
	
	@Test
	void testValidatePasswordBusinessRules_sameAsCurrentPassword() {
		IVRUserSession mockUser = new IVRUserSession();
		mockUser.setEmpID("45634");
		mockUser.setSessionId("session123");
		String userInput = "1234567";
				
		//mock session map
		when(mockCacheService.getBySessionId(mockUser.getSessionId())).thenReturn(mockUser);
		when(mockserviceHelper.findPasswordByEmpID(mockUser.getEmpID())).thenReturn(userInput);
		when(mockCacheService.updateSession(mockUser)).thenReturn(mockUser);
		String result = signonService.validatePasswordBusinessRules(userInput, mockUser.getSessionId())
				.getHookReturnCode();
		assertEquals(HOOK_RETURN_0, result);
	}
	
	@Test
	void testValidatePasswordBusinessRules_lengthConstraint_MIN() {

		IVRUserSession mockUser = new IVRUserSession();
		mockUser.setSessionId("session123");
		String userInput = "12345";
		
		// mock session map
		when(mockCacheService.getBySessionId(mockUser.getSessionId())).thenReturn(mockUser);
		when(mockserviceHelper.findPasswordByEmpID(mockUser.getEmpID())).thenReturn(userInput);
		when(mockCacheService.updateSession(mockUser)).thenReturn(mockUser);
		String result = signonService.validatePasswordBusinessRules(userInput, mockUser.getSessionId())
				.getHookReturnCode();
		assertEquals(HOOK_RETURN_1, result);
	}
	
	@Test
	void testValidatePasswordBusinessRules_lengthConstraint_MAX() {

		IVRUserSession mockUser = new IVRUserSession();
		mockUser.setSessionId("session123");
		String userInput = "123456789";

		when(mockserviceHelper.findPasswordByEmpID(mockUser.getEmpID())).thenReturn(userInput);
		// mock session map
		when(mockCacheService.getBySessionId(mockUser.getSessionId())).thenReturn(mockUser);
		when(mockCacheService.updateSession(mockUser)).thenReturn(mockUser);
		String result = signonService.validatePasswordBusinessRules(userInput, mockUser.getSessionId())
				.getHookReturnCode();
		assertEquals(HOOK_RETURN_1, result);
	}
	
	
	@Test
	void testValidatePasswordBusinessRules_sameAsEmpID() {
		String userInput = "1234567";
		IVRUserSession mockUser = new IVRUserSession();
		mockUser.setEmpID(userInput);
		mockUser.setSessionId("session123");

		when(mockserviceHelper.findPasswordByEmpID(mockUser.getEmpID())).thenReturn("87654321");
		// mock session map
		when(mockCacheService.getBySessionId(mockUser.getSessionId())).thenReturn(mockUser);
		when(mockCacheService.updateSession(mockUser)).thenReturn(mockUser);
		String result = signonService.validatePasswordBusinessRules(userInput, mockUser.getSessionId())
				.getHookReturnCode();
		assertEquals(HOOK_RETURN_2, result);
	}
	
	@Test
	void testValidatePasswordBusinessRules_validPassword() {
		String userInput = "12345678";
		IVRUserSession mockUser = new IVRUserSession();
		mockUser.setEmpID("45634");
		mockUser.setSessionId("session123");

		// TODO: change this approach, value will be fetched from Database
		when(mockserviceHelper.findPasswordByEmpID(mockUser.getEmpID())).thenReturn("12345679");
		// mock session map
		when(mockCacheService.getBySessionId(mockUser.getSessionId())).thenReturn(mockUser);
		when(mockCacheService.updateSession(mockUser)).thenReturn(mockUser);

		String result = signonService.validatePasswordBusinessRules(userInput, mockUser.getSessionId())
				.getHookReturnCode();
		assertEquals(HOOK_RETURN_3, result);
	}

	@Test
	void testIssueUserLoginWithNewPassword_Success() {
		String userDTMFInput = "1";
		String sessionId = "session123";
		IVRUserSession mockUser = new IVRUserSession();
		mockUser.setEmpID("45634");
		mockUser.setSessionId(sessionId);
		//mockUser.setBirthdate("24041994");
		mockUser.setNewPassword("234567");
		
		//mock the helper class & session map
		when(mockCacheService.getBySessionId(mockUser.getSessionId())).thenReturn(mockUser);
		when(mockserviceHelper.updatePasswordInDB(mockUser.getNewPassword(), mockUser.getEmpID()))
		.thenReturn(1);
		when(mockserviceHelper.updateLoginJeopardyFlag(mockUser)).thenReturn(1);
		when(mockserviceHelper.updatePasswordCounter(mockUser)).thenReturn(1);
		when(mockserviceHelper.updatePasswordExpireDate(mockUser)).thenReturn(1);
		when(mockCacheService.updateSession(mockUser)).thenReturn(mockUser);
		
		String result = signonService.issueUserLoginWithNewPassword(userDTMFInput, mockUser.getSessionId())
				.getHookReturnCode();
		
		assertEquals(HOOK_RETURN_2, result);
	}
	
	@Test
	void testIssueUserLoginWithNewPassword_GPDOWN() {
		String userDTMFInput = "1";
		String sessionId = "session123";
		IVRUserSession mockUser = new IVRUserSession();
		mockUser.setEmpID("45634");
		mockUser.setSessionId(sessionId);
		mockUser.setBirthdate("24041994");
		mockUser.setNewPassword("234567");
		
		//mock the helper class & session map
		when(mockCacheService.getBySessionId(mockUser.getSessionId())).thenReturn(mockUser);
		when(mockserviceHelper.updatePasswordInDB(mockUser.getNewPassword(), mockUser.getEmpID()))
		.thenReturn(0);
		when(mockCacheService.updateSession(mockUser)).thenReturn(mockUser);
		
		String result = signonService.issueUserLoginWithNewPassword(userDTMFInput, mockUser.getSessionId())
				.getHookReturnCode();
		
		assertEquals(GPDOWN_ERR_MSG_CODE, result);
	}
	
//	@Test
//	void testIssueUserLoginWithNewPassword_Birthdate_Missing() {
//		String userDTMFInput = "1";
//		String sessionId = "session123";
//		IVRUserSession mockUser = new IVRUserSession();
//		mockUser.setEmpID("45634");
//		mockUser.setSessionId(sessionId);
//		mockUser.setBirthdate("");
//		mockUser.setNewPassword("234567");
//		
//		//mock the helper class & session map
//		when(mockUserSessionMap.get(sessionId)).thenReturn(mockUser);
//		when(mockserviceHelper.updatePasswordInDB(anyString(), anyString()))
//		.thenReturn(1);
//		when(mockserviceHelper.isBirthdateSet(mockUser.getBirthdate())).thenReturn(false);
//		
//		String result = signonService.issueUserLoginWithNewPassword(userDTMFInput, mockUser.getSessionId())
//				.getHookReturnCode();
//		
//		assertEquals(HOOK_RETURN_3, result);
//	}

	@Test
	void testCheckTechAreaCode_hasAreaCode() {
		String sessionId = "session123";
		
		IVRUserSession mockUser =  new IVRUserSession();
		mockUser.setSessionId(sessionId);
		mockUser.setNpaPrefix("303");
		
		when(mockCacheService.getBySessionId(mockUser.getSessionId())).thenReturn(mockUser);
		when(mockserviceHelper.isNpaExists(mockUser.getNpaPrefix())).thenReturn(true);
		
		String result = signonService.checkTechAreaCode(sessionId).getHookReturnCode();
		assertEquals(HOOK_RETURN_2, result);
	}
	
	@Test
	void testCheckTechAreaCode_DontHaveAreaCode() {
		String sessionId = "session123";

		IVRUserSession mockUser = new IVRUserSession();
		mockUser.setNpaPrefix("303");
		mockUser.setSessionId(sessionId);
		
		when(mockCacheService.getBySessionId(mockUser.getSessionId())).thenReturn(mockUser);
		when(mockserviceHelper.isNpaExists(mockUser.getNpaPrefix())).thenReturn(false);

		String result = signonService.checkTechAreaCode(sessionId).getHookReturnCode();
		assertEquals(HOOK_RETURN_1, result);
	}

	@Test
	void testValidateNPA_PresentInDB() {
		String userDtmfInput = "303";
		IVRUserSession mockUser = new IVRUserSession();
		String sessionId = "session123";
		mockUser.setSessionId(sessionId);
		when(mockCacheService.getBySessionId(mockUser.getSessionId())).thenReturn(mockUser);
		when(mockserviceHelper.isNpaPresentInDB(userDtmfInput)).thenReturn(true);
		
		String result = signonService.validateNPA(userDtmfInput, sessionId).getHookReturnCode();
		
		assertEquals(HOOK_RETURN_2, result);
	}
	
	@Test
	void testValidateNPA_LessThan3digit() {
		String userDtmfInput = "03";
		IVRUserSession mockUser = new IVRUserSession();
		String sessionId = "session123";
		mockUser.setSessionId(sessionId);
		when(mockCacheService.getBySessionId(mockUser.getSessionId())).thenReturn(mockUser);
		//when(mockserviceHelper.isNpaPresentInDB(userDtmfInput)).thenReturn(true);
		
		String result = signonService.validateNPA(userDtmfInput, sessionId).getHookReturnCode();
		
		assertEquals(HOOK_RETURN_0, result);
	}
	
	@Test
	void testValidateNPA_MoreThan3digit() {
		String userDtmfInput = "34567";
		IVRUserSession mockUser = new IVRUserSession();
		String sessionId = "session123";
		mockUser.setSessionId(sessionId);
		when(mockCacheService.getBySessionId(mockUser.getSessionId())).thenReturn(mockUser);
		//when(mockserviceHelper.isNpaPresentInDB(userDtmfInput)).thenReturn(true);
		
		String result = signonService.validateNPA(userDtmfInput, sessionId).getHookReturnCode();
		
		assertEquals(HOOK_RETURN_0, result);
	}
	
	@Test
	void testValidateNPA_AbsentInDB() {
		String userDtmfInput = "303";
		IVRUserSession mockUser = new IVRUserSession();
		String sessionId = "session123";
		mockUser.setSessionId(sessionId);
		when(mockCacheService.getBySessionId(mockUser.getSessionId())).thenReturn(mockUser);
		when(mockserviceHelper.isNpaPresentInDB(userDtmfInput)).thenReturn(false);
		
		String result = signonService.validateNPA(userDtmfInput, sessionId).getHookReturnCode();
		
		assertEquals(HOOK_RETURN_0, result);
	}
	
	@Test
	void testValidateNPA_DBError() {
		String userDtmfInput = "303";
		IVRUserSession mockUser = new IVRUserSession();
		String sessionId = "session123";
		mockUser.setSessionId(sessionId);
		when(mockCacheService.getBySessionId(mockUser.getSessionId())).thenReturn(mockUser);
		when(mockserviceHelper.isNpaPresentInDB(userDtmfInput)).thenThrow(RuntimeException.class);
		
		String result = signonService.validateNPA(userDtmfInput, sessionId).getHookReturnCode();
		
		assertEquals(GPDOWN_ERR_MSG_CODE, result);
	}

	@Test
	void testValidateBirthDate_Passed() {
		String userDtmfInput = "Birthdate";
		String sessionId = "session123";
		String empid = "45634";
		IVRUserSession mockUser = loadIvrSession(sessionId, empid, false);
		List<IVRParameter> params = new ArrayList<>();
		mockUser.setSessionId(sessionId);
		when(mockCacheService.getBySessionId(mockUser.getSessionId())).thenReturn(mockUser);
		when(mockserviceHelper.birthDateValidation(userDtmfInput, mockUser)).thenReturn(HOOK_RETURN_1);
		when(mockserviceHelper.addParamterData(userDtmfInput)).thenReturn(params);
		
		String result = signonService.validateBirthDate(userDtmfInput, sessionId).getHookReturnCode();
		assertEquals(HOOK_RETURN_1, result);
		assertEquals(userDtmfInput, mockUser.getNewBirthDate());
	}
	
	@Test
	void testValidateBirthDate_Failed() {
		String userDtmfInput = "Birthdate";
		String sessionId = "session123";
		String empid = "45634";
		IVRUserSession mockSession = loadIvrSession(sessionId, empid, false);
		List<IVRParameter> params = new ArrayList<>();
		mockSession.setSessionId(sessionId);
		when(mockCacheService.getBySessionId(mockSession.getSessionId())).thenReturn(mockSession);
		when(mockserviceHelper.birthDateValidation(userDtmfInput, mockSession)).thenReturn(HOOK_RETURN_0);
		
		IVRWebHookResponseDto result = signonService.validateBirthDate(userDtmfInput, sessionId);
		assertEquals(HOOK_RETURN_0, result.getHookReturnCode());
		assertEquals(INVALID_DATE_FORMAT, result.getHookReturnMessage());
	}
	
	@Test
	void testPersistNewBirthdate_Success() {
		String date = "birthdate";
		String empid = "123456";
		String sessionId = "session123";
		IVRUserSession mockUser = loadIvrSession(sessionId, empid, false);
		mockUser.setNewBirthDate(date);
		mockUser.setSessionId(sessionId);
		when(mockCacheService.getBySessionId(mockUser.getSessionId())).thenReturn(mockUser);
		when(mockserviceHelper.updateTechnicianBirthdate(date, empid)).thenReturn(1);
		
		IVRWebHookResponseDto response = signonService.persistNewBirthdate(sessionId);
		assertEquals(HOOK_RETURN_1, response.getHookReturnCode());
	}
	
	@Test
	void testPersistNewBirthdate_Failure() {
		String date = "birthdate";
		String empid = "123456";
		String sessionId = "session123";
		IVRUserSession mockUser = loadIvrSession(sessionId, empid, false);
		mockUser.setNewBirthDate(date);
		mockUser.setSessionId(sessionId);
		when(mockCacheService.getBySessionId(mockUser.getSessionId())).thenReturn(mockUser);
		when(mockserviceHelper.updateTechnicianBirthdate(date, empid)).thenReturn(0);
		
		IVRWebHookResponseDto response = signonService.persistNewBirthdate(sessionId);
		assertEquals(HOOK_RETURN_0, response.getHookReturnCode());
	}
	
	@Test
	void testValidateTechPagerConfiguredPasswodReset_true() {
		
		IVRUserSession session = loadIvrSession("session123", "", false);
		when(mockCacheService.getBySessionId(session.getSessionId())).thenReturn(session);
		when(mockserviceHelper.isUserPagerEnabled(session)).thenReturn(true);
		IVRWebHookResponseDto response = signonService.validateTechPagerConfiguredPasswordReset(session.getSessionId());
		assertEquals("1", response.getHookReturnCode());
	}
	
	@Test
	void testValidateTechPagerConfiguredPasswordReset_false() {
		
		IVRUserSession session = loadIvrSession("session123", "", false);
		when(mockCacheService.getBySessionId(session.getSessionId())).thenReturn(session);
		when(mockserviceHelper.isUserPagerEnabled(session)).thenReturn(false);
		IVRWebHookResponseDto response = signonService.validateTechPagerConfiguredPasswordReset(session.getSessionId());
		assertEquals("0", response.getHookReturnCode());
	}
	
	@Test
	void testValidateTechPagerConfiguredBirthdate_true() {
		
		IVRUserSession session = loadIvrSession("session123", "", false);
		when(mockCacheService.getBySessionId(session.getSessionId())).thenReturn(session);
		when(mockserviceHelper.isUserPagerEnabled(session)).thenReturn(true);
		IVRWebHookResponseDto response = signonService.validateTechPagerConfiguredBirthdate(session.getSessionId());
		assertEquals("1", response.getHookReturnCode());
	}
	
	@Test
	void testValidateTechPagerConfigureBirthdate_false() {
		
		IVRUserSession session = loadIvrSession("session123", "", false);
		when(mockCacheService.getBySessionId(session.getSessionId())).thenReturn(session);
		when(mockserviceHelper.isUserPagerEnabled(session)).thenReturn(false);
		IVRWebHookResponseDto response = signonService.validateTechPagerConfiguredBirthdate(session.getSessionId());
		assertEquals("0", response.getHookReturnCode());
	}
	
	@Test
	void testValidateTechSecurityCodeFromSession_Matches() {
		String sessionid = "session123";
		String userInput = "45364";
		IVRUserSession session = loadIvrSession(sessionid, userInput, false);
		when(mockCacheService.getBySessionId(session.getSessionId())).thenReturn(session);
		IVRWebHookResponseDto response = signonService.validateTechSecurityCodeFromSession(userInput, session.getSessionId());
		assertEquals("1", response.getHookReturnCode());
	}
	
	@Test
	void testValidateTechSecurityCodeFromSession_Invalid() {
		String sessionid = "session123";
		String userInput = "45364";
		String securityCode = "123456";
		IVRUserSession session = loadIvrSession(sessionid, securityCode, false);
		when(mockCacheService.getBySessionId(session.getSessionId())).thenReturn(session);
		IVRWebHookResponseDto response = signonService.validateTechSecurityCodeFromSession(userInput, session.getSessionId());
		assertEquals("0", response.getHookReturnCode());
	}
	
	@Test
	void testGeneratePagerOTP_Success() throws JsonProcessingException {
		String sessionid = "session123"; 
		String securityCode = "45654";
		String data = NET_PHONE_DEVICE;
		List<IVRParameter> params = new ArrayList<>();
		IVRParameter param = new IVRParameter();
		param.setData(data);
		params.add(param);
		IVRUserSession session = loadIvrSession(sessionid, securityCode, false);
		session.setCanBePagedMobile(true);
		when(mockCacheService.getBySessionId(session.getSessionId())).thenReturn(session);
		when(mockserviceHelper.generateFourDigitOtp()).thenReturn("4566");
		String responseString = "OPT sent to Tech";
		when(mockserviceHelper.pushOtpToTechCuid(session)).thenReturn(responseString );
		when(mockserviceHelper.processJsonStringNETMessaging(responseString)).thenReturn(true);
		when(mockserviceHelper.addParamterData(data)).thenReturn(params);
		IVRWebHookResponseDto response = signonService.generatePagerOTP(session.getSessionId());
		assertEquals("1", response.getHookReturnCode());
		assertEquals(data, response.getParameters().get(0).getData());
	}
	
	@Test
	void testGeneratePagerOTP_Failed() throws JsonProcessingException {
		String sessionid = "session123"; 
		String securityCode = "45654";
		IVRUserSession session = loadIvrSession(sessionid, securityCode, false);
		when(mockCacheService.getBySessionId(session.getSessionId())).thenReturn(session);
		when(mockserviceHelper.generateFourDigitOtp()).thenReturn("4566");
		String responseString = "OPT cannot be sent to Tech";
		when(mockserviceHelper.pushOtpToTechCuid(session)).thenReturn(responseString );
		when(mockserviceHelper.processJsonStringNETMessaging(responseString)).thenReturn(false);
		IVRWebHookResponseDto response = signonService.generatePagerOTP(session.getSessionId());
		assertEquals("0", response.getHookReturnCode());
	}
	
	@Test
	void testValidatePagerOtp_Valid() {
		String sessionid = "session123"; 
		String empid = "45654";
		String otp = "4563";
		String userInput = otp;
		IVRUserSession session = loadIvrSession(sessionid, empid, false);
		session.setOtpGenerated(otp);
		when(mockCacheService.getBySessionId(session.getSessionId())).thenReturn(session);
		IVRWebHookResponseDto response = signonService.validatePagerOtp(userInput, sessionid);
		
		assertEquals(HOOK_RETURN_1, response.getHookReturnCode());
		assertEquals(VALID_OTP, response.getHookReturnMessage());
	}
	
	@Test
	void testValidatePagerOtp_Invalid() {
		String sessionid = "session123"; 
		String empid = "45654";
		String otp = "4563";
		String userInput = "4666";
		IVRUserSession session = loadIvrSession(sessionid, empid, false);
		session.setOtpGenerated(otp);
		when(mockCacheService.getBySessionId(session.getSessionId())).thenReturn(session);
		IVRWebHookResponseDto response = signonService.validatePagerOtp(userInput, sessionid);
		
		assertEquals(HOOK_RETURN_0, response.getHookReturnCode());
		assertEquals(INVALID_OTP, response.getHookReturnMessage());
	}
	
	@Test
	void testInvalidSession_checkAreaCode() {
		String sessionid = "session123";
		IVRUserSession session = null;
		when(mockCacheService.getBySessionId(sessionid)).thenReturn(session);
		IVRWebHookResponseDto response = signonService.checkTechAreaCode(sessionid);
		
		assertEquals(GPDOWN_ERR_MSG_CODE, response.getHookReturnCode());
	}
	
	@Test
	void testInvalidSession_generateOTP() {
		String sessionid = "session123";
		IVRUserSession session = null;
		when(mockCacheService.getBySessionId(sessionid)).thenReturn(session);
		IVRWebHookResponseDto response = signonService.generatePagerOTP(sessionid);
		
		assertEquals(GPDOWN_ERR_MSG_CODE, response.getHookReturnCode());
	}
	
	@Test
	void testInvalidSession_techProperties() {
		String sessionid = "session123";
		IVRUserSession session = null;
		when(mockCacheService.getBySessionId(sessionid)).thenReturn(session);
		IVRWebHookResponseDto response = signonService.issueValidateTechnicianProperties(sessionid);
		
		assertEquals(GPDOWN_ERR_MSG_CODE, response.getHookReturnCode());
	}
	
	@Test
	void testInvalidSession_persistBirthdate_nosession() {
		String sessionid = "session123";
		IVRUserSession session = null;
		when(mockCacheService.getBySessionId(sessionid)).thenReturn(session);
		IVRWebHookResponseDto response = signonService.persistNewBirthdate(sessionid);
		
		assertEquals(GPDOWN_ERR_MSG_CODE, response.getHookReturnCode());
	}
	
	@Test
	void testInvalidSession_persistBirthdate_noNewBirthdate() {
		String sessionid = "session123";
		IVRUserSession session = loadIvrSesion(sessionid, false);
		when(mockCacheService.getBySessionId(sessionid)).thenReturn(session);
		IVRWebHookResponseDto response = signonService.persistNewBirthdate(sessionid);
		
		assertEquals(GPDOWN_ERR_MSG_CODE, response.getHookReturnCode());
	}
	
	@Test
	void testInvalidSession_birthdate() {
		String sessionid = "session123";
		String userInput = "birthdate";
		IVRUserSession session = null;
		when(mockCacheService.getBySessionId(sessionid)).thenReturn(session);
		IVRWebHookResponseDto response = signonService.validateBirthDate(userInput, sessionid);
		
		assertEquals(GPDOWN_ERR_MSG_CODE, response.getHookReturnCode());
	}
	
	@Test
	void testInvalidSession_validateNpa() {
		String sessionid = "session123";
		String userInput = "";
		IVRUserSession session = null;
		when(mockCacheService.getBySessionId(sessionid)).thenReturn(session);
		IVRWebHookResponseDto response = signonService.validateNPA(userInput, sessionid);
		
		assertEquals(GPDOWN_ERR_MSG_CODE, response.getHookReturnCode());
	}
	
	@Test
	void testInvalidSession_PagerOtp() {
		String sessionid = "session123";
		String userInput = "";
		IVRUserSession session = null;
		when(mockCacheService.getBySessionId(sessionid)).thenReturn(session);
		IVRWebHookResponseDto response = signonService.validatePagerOtp(userInput, sessionid);
		
		assertEquals(GPDOWN_ERR_MSG_CODE, response.getHookReturnCode());
	}
	
	@Test
	void testInvalidSession_newPassword() {
		String sessionid = "session123";
		String userInput ="";
		IVRUserSession session = null;
		when(mockCacheService.getBySessionId(sessionid)).thenReturn(session);
		IVRWebHookResponseDto response = signonService.validatePasswordBusinessRules(userInput, sessionid);
		
		assertEquals(GPDOWN_ERR_MSG_CODE, response.getHookReturnCode());
	}
	
	@Test
	void testInvalidSession_BirthdatePagerCheck() {
		String sessionid = "session123";
		String userInput ="";
		IVRUserSession session = null;
		when(mockCacheService.getBySessionId(sessionid)).thenReturn(session);
		IVRWebHookResponseDto response = signonService.validateTechPagerConfiguredBirthdate(sessionid);
		
		assertEquals(GPDOWN_ERR_MSG_CODE, response.getHookReturnCode());
	}
	
	@Test
	void testInvalidSession_PasswordResetPagerCheckd() {
		String sessionid = "session123";
		String userInput ="";
		IVRUserSession session = null;
		when(mockCacheService.getBySessionId(sessionid)).thenReturn(session);
		IVRWebHookResponseDto response = signonService.validateTechPagerConfiguredPasswordReset(sessionid);
		
		assertEquals(GPDOWN_ERR_MSG_CODE, response.getHookReturnCode());
	}
	
	@Test
	void testInvalidSession_SecurityCodeInSession() {
		String sessionid = "session123";
		String userInput ="";
		IVRUserSession session = null;
		when(mockCacheService.getBySessionId(sessionid)).thenReturn(session);
		IVRWebHookResponseDto response = signonService.validateTechSecurityCodeFromSession(userInput, sessionid);
		
		assertEquals(GPDOWN_ERR_MSG_CODE, response.getHookReturnCode());
	}	
	
	@Test
	void testInvalidSession_issueUserLoginWithNewPassword() {
		String sessionid = "session123";
		String userInput ="";
		IVRUserSession session = null;
		when(mockCacheService.getBySessionId(sessionid)).thenReturn(session);
		IVRWebHookResponseDto response = signonService.issueUserLoginWithNewPassword(userInput, sessionid);
		
		assertEquals(GPDOWN_ERR_MSG_CODE, response.getHookReturnCode());
	}
	
	
	@Test
	void testIssueUserAltLogin() {
		//fail("Not yet implemented");
	}

	@Test
	void testIssueSecCodeUpdate() {
		//fail("Not yet implemented");
	}

	@Test
	void testIssueLogout() {
		//fail("Not yet implemented");
	}

	@Test
	void testPlayVoiceGram() {
		//fail("Not yet implemented");
	}

	@Test
	void testUpdateVoiceGram() {
		//fail("Not yet implemented");
	}

}
