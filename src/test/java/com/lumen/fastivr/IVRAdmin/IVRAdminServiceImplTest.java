package com.lumen.fastivr.IVRAdmin;

import com.lumen.fastivr.IVRCacheManagement.IVRCacheService;
import com.lumen.fastivr.IVRDto.IVRWebHookResponseDto;
import com.lumen.fastivr.IVRSessionManagement.IVRUserSession;
import com.lumen.fastivr.IVRSignon.IVRSignOnInterface;
import com.lumen.fastivr.IVRSignon.IVRSignOnServiceHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static com.lumen.fastivr.IVRUtils.IVRConstants.*;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IVRAdminServiceImplTest {

	@Mock
	private IVRCacheService mockCacheService;

	@Mock
	private IVRSignOnServiceHelper serviceHelper;

	@Mock
	private IVRSignOnInterface ivrSignOnInterface;

	@InjectMocks
	private IVRAdminServiceImpl ivrAdminService;

	@Test
	void testGetCurrentAreaCodeWhenNPAExists() {

		String sessionId = "session123";

		IVRUserSession session = new IVRUserSession();

		session.setSessionId(sessionId);
		session.setNpaPrefix("56");

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(session);

		when(serviceHelper.isNpaExists(anyString())).thenReturn(true);

		IVRWebHookResponseDto actualResponse = ivrAdminService.getCurrentAreaCode(sessionId);

		assertEquals(HOOK_RETURN_0, actualResponse.getHookReturnCode());
		verify(serviceHelper, times(1)).isNpaExists(anyString());

	}

	@Test
	void testVoiceResults() {

		String sessionId = "session123";

		IVRUserSession session = new IVRUserSession();

		session.setSessionId(sessionId);

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(session);

		IVRWebHookResponseDto actualResponse = ivrAdminService.voiceResults(sessionId);

		assertEquals(HOOK_RETURN_0, actualResponse.getHookReturnCode());
		verify(serviceHelper, times(0)).isNpaExists(anyString());

	}

	@Test
	void testGetCurrentAreaCodeWhenNPADoesNotExists() {

		String sessionId = "session123";

		IVRUserSession session = new IVRUserSession();

		session.setSessionId(sessionId);
		session.setNpaPrefix("56");

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(session);

		when(serviceHelper.isNpaExists(anyString())).thenReturn(false);

		IVRWebHookResponseDto actualResponse = ivrAdminService.getCurrentAreaCode(sessionId);

		assertEquals("", actualResponse.getHookReturnCode());
		verify(serviceHelper, times(1)).isNpaExists(anyString());

	}

	@Test
	void testValidateAreaCodeWhenSameAreaCodeAsOld() {

		String sessionId = "session123";

		IVRUserSession session = new IVRUserSession();

		IVRWebHookResponseDto responseDto = new IVRWebHookResponseDto();
		responseDto.setHookReturnCode(HOOK_RETURN_1);

		session.setSessionId(sessionId);
		session.setNpaPrefix("32");

		List<String> userInputs = new ArrayList<>();
		userInputs.add("32");

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(session);

		when(ivrSignOnInterface.validateNPA(anyString(), anyString())).thenReturn(responseDto);

		IVRWebHookResponseDto actualResponse = ivrAdminService.validateAreaCode(userInputs.get(0), sessionId);

		assertEquals(HOOK_RETURN_1, actualResponse.getHookReturnCode());
		assertEquals(AREA_CODE_SAME_AS_OLD, actualResponse.getHookReturnMessage());
		verify(ivrSignOnInterface, times(1)).validateNPA(anyString(), anyString());

	}

	@Test
	void testValidateAreaCodeWhenInvalidAreaCode() {

		String sessionId = "session123";

		IVRUserSession session = new IVRUserSession();

		IVRWebHookResponseDto responseDto = new IVRWebHookResponseDto();
		responseDto.setHookReturnCode(HOOK_RETURN_0);

		session.setSessionId(sessionId);

		List<String> userInputs = new ArrayList<>();
		userInputs.add("32");

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(session);

		when(ivrSignOnInterface.validateNPA(anyString(), anyString())).thenReturn(responseDto);

		IVRWebHookResponseDto actualResponse = ivrAdminService.validateAreaCode(userInputs.get(0), sessionId);

		assertEquals(HOOK_RETURN_2, actualResponse.getHookReturnCode());
		assertEquals(INVALID_AREA_CODE, actualResponse.getHookReturnMessage());
		verify(ivrSignOnInterface, times(1)).validateNPA(anyString(), anyString());

	}

	@Test
	void testValidateAreaCodeWhenOKAreaCode() {

		String sessionId = "session123";

		IVRUserSession session = new IVRUserSession();

		IVRWebHookResponseDto responseDto = new IVRWebHookResponseDto();
		responseDto.setHookReturnCode(HOOK_RETURN_2);

		session.setSessionId(sessionId);

		List<String> userInputs = new ArrayList<>();
		userInputs.add("32");

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(session);

		when(ivrSignOnInterface.validateNPA(anyString(), anyString())).thenReturn(responseDto);

		IVRWebHookResponseDto actualResponse = ivrAdminService.validateAreaCode(userInputs.get(0), sessionId);

		assertEquals(HOOK_RETURN_3, actualResponse.getHookReturnCode());
		assertEquals(OK, actualResponse.getHookReturnMessage());
		verify(ivrSignOnInterface, times(1)).validateNPA(anyString(), anyString());

	}

	@Test
	void testUpdateAreaCodeWhenDBUpdate() {

		String sessionId = "session123";

		IVRUserSession session = new IVRUserSession();

		session.setSessionId("123");
		session.setEmpID("345364");
		session.setVariableNpaFlag(Boolean.FALSE);
		session.setNpaPrefix("564");
		
		int rowsUpdated = 1;

		List<String> userInputs = new ArrayList<>();
		userInputs.add("1");

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(session);

		when(serviceHelper.updateAreaCodeInDB(anyString(), anyString())).thenReturn(rowsUpdated);

		IVRWebHookResponseDto actualResponse = ivrAdminService.setTechNPARequest(userInputs.get(0), sessionId);

		assertEquals(HOOK_RETURN_0, actualResponse.getHookReturnCode());
		assertEquals(UPDATE_AREA_CODE, actualResponse.getHookReturnMessage());
		verify(serviceHelper, times(1)).updateAreaCodeInDB(anyString(), anyString());

	}

	@Test
	void testUpdateAreaCodeWhenNoDBUpdate() {

		String sessionId = "session123";

		IVRUserSession session = new IVRUserSession();

		session.setSessionId(sessionId);
		session.setEmpID("76234823");

		session.setVariableNpaFlag(Boolean.TRUE);

		List<String> userInputs = new ArrayList<>();
		userInputs.add("1");

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(session);

		IVRWebHookResponseDto actualResponse = ivrAdminService.setTechNPARequest(userInputs.get(0), sessionId);

		assertEquals(HOOK_RETURN_0, actualResponse.getHookReturnCode());
		verify(serviceHelper, times(0)).updateAreaCodeInDB(anyString(), anyString());

	}

	
	
	@Test
	void testValidatePasswordBusinessRules_sameAsCurrentPassword() {
		IVRUserSession mockUser = new IVRUserSession();
		mockUser.setEmpID("45634");
		mockUser.setSessionId("session123");
		String userInput = "1234567";
				
		//mock session map
		when(mockCacheService.getBySessionId(mockUser.getSessionId())).thenReturn(mockUser);
		when(serviceHelper.findPasswordByEmpID(mockUser.getEmpID())).thenReturn(userInput);
		when(mockCacheService.updateSession(mockUser)).thenReturn(mockUser);
		String result = ivrAdminService.validatePassword(userInput, mockUser.getSessionId())
				.getHookReturnCode();
		assertEquals(HOOK_RETURN_1, result);
	}
	
	@Test
	void testValidatePasswordBusinessRules_lengthConstraint_MIN() {

		IVRUserSession mockUser = new IVRUserSession();
		mockUser.setSessionId("session123");
		String userInput = "12345";
		
		// mock session map
		when(mockCacheService.getBySessionId(mockUser.getSessionId())).thenReturn(mockUser);
		when(serviceHelper.findPasswordByEmpID(mockUser.getEmpID())).thenReturn(userInput);
		when(mockCacheService.updateSession(mockUser)).thenReturn(mockUser);
		String result = ivrAdminService.validatePassword(userInput, mockUser.getSessionId())
				.getHookReturnCode();
		assertEquals(HOOK_RETURN_2, result);
	}
	
	@Test
	void testValidatePasswordBusinessRules_lengthConstraint_MAX() {

		IVRUserSession mockUser = new IVRUserSession();
		mockUser.setSessionId("session123");
		String userInput = "123456789";

		when(serviceHelper.findPasswordByEmpID(mockUser.getEmpID())).thenReturn(userInput);
		// mock session map
		when(mockCacheService.getBySessionId(mockUser.getSessionId())).thenReturn(mockUser);
		when(mockCacheService.updateSession(mockUser)).thenReturn(mockUser);
		String result = ivrAdminService.validatePassword(userInput, mockUser.getSessionId())
				.getHookReturnCode();
		assertEquals(HOOK_RETURN_2, result);
	}
	
	
	@Test
	void testValidatePasswordBusinessRules_sameAsEmpID() {
		String userInput = "1234567";
		IVRUserSession mockUser = new IVRUserSession();
		mockUser.setEmpID(userInput);
		mockUser.setSessionId("session123");

		when(serviceHelper.findPasswordByEmpID(mockUser.getEmpID())).thenReturn("87654321");
		// mock session map
		when(mockCacheService.getBySessionId(mockUser.getSessionId())).thenReturn(mockUser);
		when(mockCacheService.updateSession(mockUser)).thenReturn(mockUser);
		String result = ivrAdminService.validatePassword(userInput, mockUser.getSessionId())
				.getHookReturnCode();
		assertEquals(HOOK_RETURN_3, result);
	}
	
	@Test
	void testValidatePasswordBusinessRules_validPassword() {
		String userInput = "12345678";
		IVRUserSession mockUser = new IVRUserSession();
		mockUser.setEmpID("45634");
		mockUser.setSessionId("session123");

		// TODO: change this approach, value will be fetched from Database
		when(serviceHelper.findPasswordByEmpID(mockUser.getEmpID())).thenReturn("12345679");
		// mock session map
		when(mockCacheService.getBySessionId(mockUser.getSessionId())).thenReturn(mockUser);
		when(mockCacheService.updateSession(mockUser)).thenReturn(mockUser);

		String result = ivrAdminService.validatePassword(userInput, mockUser.getSessionId())
				.getHookReturnCode();
		assertEquals(HOOK_RETURN_4, result);
	}

	
	//@Test
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
		when(serviceHelper.updatePasswordInDB(mockUser.getNewPassword(), mockUser.getEmpID()))
		.thenReturn(0);
		when(mockCacheService.updateSession(mockUser)).thenReturn(mockUser);
		
		String result = ivrAdminService.issueUserLoginWithNewPassword(userDTMFInput, mockUser.getSessionId())
				.getHookReturnCode();
		
		assertEquals(GPDOWN_ERR_MSG_CODE, result);
	}
	
	@Test
	void testValidateVariableAreaCode() {

		String sessionId = "session123";

		IVRUserSession session = new IVRUserSession();

		session.setSessionId(sessionId);
		session.setEmpID("76234823");

		session.setVariableNpaFlag(Boolean.TRUE);

		List<String> userInputs = new ArrayList<>();
		userInputs.add("32");

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(session);

		IVRWebHookResponseDto actualResponse = ivrAdminService.validateVariableAreaCode(userInputs.get(0), sessionId);

		assertEquals(HOOK_RETURN_1, actualResponse.getHookReturnCode());
		assertEquals(VARIABLE_AREA_CODE_NOT_PRESENT, actualResponse.getHookReturnMessage());

	}

	@Test
	void testValidateVariableAreaCodeasDeafaultAreaCode() {

		String sessionId = "session123";

		IVRUserSession session = new IVRUserSession();

		session.setSessionId(sessionId);
		session.setEmpID("345364");
		session.setVariableNpaFlag(Boolean.FALSE);


		List<String> userInputs = new ArrayList<>();
		userInputs.add("32");

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(session);


		IVRWebHookResponseDto actualResponse = ivrAdminService.validateVariableAreaCode(userInputs.get(0), sessionId);

		assertEquals(HOOK_RETURN_0, actualResponse.getHookReturnCode());
		assertEquals(VARIABLE_AREA_CODE_SAME_AS_DEFAULT, actualResponse.getHookReturnMessage());

	}
	/**
	 * 
	 */
	@Test
	void testUpdateVariableAreaCodeWithNPAInDB() {
		String sessionId = "session123";

		IVRUserSession session = new IVRUserSession();

		session.setSessionId("123");
		session.setEmpID("345364");
		session.setVariableNpaFlag(Boolean.TRUE);
		session.setNpaPrefix("564");
		
		int rowsUpdated = 1;

		List<String> userInputs = new ArrayList<>();
		userInputs.add("1");

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(session);

		when(serviceHelper.updateAreaCodeInDB(anyString(), anyString())).thenReturn(rowsUpdated);

		IVRWebHookResponseDto actualResponse = ivrAdminService.issueSetVarNpaRequest(userInputs.get(0), sessionId);

		assertEquals(HOOK_RETURN_0, actualResponse.getHookReturnCode());
		assertEquals(UPDATE_AREA_CODE, actualResponse.getHookReturnMessage());
		verify(serviceHelper, times(1)).updateAreaCodeInDB(anyString(), anyString());

	}

	@Test
	void testUpdateVariableAreaCodeWithNullNPA() {

		String sessionId = "session123";

		IVRUserSession session = new IVRUserSession();

		session.setSessionId(sessionId);
		session.setEmpID("76234823");

		session.setVariableNpaFlag(Boolean.TRUE);

		List<String> userInputs = new ArrayList<>();
		userInputs.add(" ");

		when(mockCacheService.getBySessionId(sessionId)).thenReturn(session);

		IVRWebHookResponseDto actualResponse = ivrAdminService.issueSetVarNpaRequest(userInputs.get(0), sessionId);

		assertEquals(HOOK_RETURN_1, actualResponse.getHookReturnCode());
		assertEquals(GPDOWN_ERR_MSG, actualResponse.getHookReturnMessage());

	}



}
