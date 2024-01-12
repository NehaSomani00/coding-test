package com.lumen.fastivr.IVRSignon;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumen.fastivr.IVRCacheManagement.IVRCacheService;
import com.lumen.fastivr.IVRDto.IVRPagerConfig;
import com.lumen.fastivr.IVRDto.IVRParameter;
import com.lumen.fastivr.IVRDto.NETMessagingRequestDto;
import com.lumen.fastivr.IVRDto.NETMessagingResponseDto;
import com.lumen.fastivr.IVREntity.FastIvrUser;
import com.lumen.fastivr.IVREntity.Npa;
import com.lumen.fastivr.IVRRepository.FastIvrDBInterface;
import com.lumen.fastivr.IVRRepository.NpaRepository;
import com.lumen.fastivr.IVRRepository.PcoutRepository;
import com.lumen.fastivr.IVRSessionManagement.IVRUserSession;
import com.lumen.fastivr.httpclient.IVRHttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.*;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IVRSignOnServiceHelperTest {
	
	@Mock
	private FastIvrDBInterface mockFastIvrDBInterface;
	
	@Mock
	private NpaRepository npaRepository;
	
	@Mock
	private PcoutRepository pcoutRepository;
	
//	@Mock
//	private Map<String, IVRUserSession> mockSessionMap;
	
	@Mock
	private IVRCacheService mockCacheService;
	
	@Mock
	private ObjectMapper mockObjectMapper;
	
	@Mock
	private IVRHttpClient mockIvrHttpClient;
	
	@InjectMocks
	private IVRSignOnServiceHelper serviceHelper;

	@BeforeEach
	void setUp() throws Exception {
		//MockitoAnnotations.openMocks(this);
	}
	
	private IVRUserSession loadIvrSession(String sessionId, String empid, boolean isAuthenticated) {
		IVRUserSession userSession = new IVRUserSession();
		userSession.setSessionId(sessionId);
		userSession.setAuthenticated(isAuthenticated);
		userSession.setEmpID(empid);
		return userSession;
	}
	
	@Test
	void testValidateSecurityCode_TechNotInDB() {
		String userInput = "170526";
		IVRUserSession session = loadIvrSession("session123", "17026", false);
		
		when(mockFastIvrDBInterface.findByempID(userInput)).thenReturn(Optional.empty());
		String hookReturnCode = serviceHelper.validateSecurityCode(userInput, session);
		assertEquals(HOOK_RETURN_0, hookReturnCode);
	}
	
	@Test
	void testValidateSecurityCode_TechEnabled_NewSession() throws IllegalArgumentException, IllegalAccessException {
		String userInput = "170526";
		IVRUserSession session = loadIvrSession("session123", userInput, false);
		session.setNewSession(true);
		FastIvrUser mockDBTech = loadMockFastIvrUser(userInput,"","Y","","AD00912");
		
		when(mockFastIvrDBInterface.findByempID(userInput)).thenReturn(Optional.of(mockDBTech));
		testLoadTechPagerConfiguration_Success();
		String hookReturnCode = serviceHelper.validateSecurityCode(userInput, session);
		assertEquals(HOOK_RETURN_1, hookReturnCode);
		assertTrue(session.isEnabledFlag());
	}
	
	@Test
	void testValidateSecurityCode_TechNotEnabled_OldSession() {
		String userInput = "170526";
		IVRUserSession session = loadIvrSession("session123", userInput, false);
		session.setNewSession(false);
		FastIvrUser mockDBTech = loadMockFastIvrUser(userInput,"","Y","","AD00912");
		
		when(mockFastIvrDBInterface.findByempID(userInput)).thenReturn(Optional.of(mockDBTech));
		String hookReturnCode = serviceHelper.validateSecurityCode(userInput, session);
		assertEquals(HOOK_RETURN_2, hookReturnCode);
	}

	@Test
	void testUpdateLoginJeopardyFlag() {
		
		String sessionId = "session123";
		String securityId = "45634";
		boolean enabledFlag = true;
		boolean isLoginJeopardyFlag = false;
		IVRUserSession ivrSession = loadIvrSession(sessionId , securityId , false);
		ivrSession.setEnabledFlag(enabledFlag);
		ivrSession.setLoginJeopardyFlag(isLoginJeopardyFlag);
		ivrSession.setEmpID(securityId);
		
		String jep = ivrSession.isLoginJeopardyFlag() ? "Y" : "N";
		//mock the database call
		when(mockFastIvrDBInterface.updateLoginJeopardyFlagByempID(jep, securityId)).thenReturn(1);
		
		
		serviceHelper.updateLoginJeopardyFlag(ivrSession);
		
		//Verify service method was called
		verify(mockFastIvrDBInterface, times(1)).updateLoginJeopardyFlagByempID(isLoginJeopardyFlag?"Y":"N", securityId);
		verifyNoMoreInteractions(mockFastIvrDBInterface);
	}

	@Test
	void testUpdatePasswordCounter() {
		IVRUserSession mockUserSession = loadIvrSession("session123", "", false);
		mockUserSession.setEmpID("45634");
		mockUserSession.setPasswordAttemptCounter(2);
		
		when(mockFastIvrDBInterface.updatePasswordCounterByempID(mockUserSession.getPasswordAttemptCounter(),
				mockUserSession.getEmpID())).thenReturn(1);
		
		serviceHelper.updatePasswordCounter(mockUserSession);
		
		verify(mockFastIvrDBInterface, times(1)).updatePasswordCounterByempID(mockUserSession.getPasswordAttemptCounter(),
				mockUserSession.getEmpID());
		verifyNoMoreInteractions(mockFastIvrDBInterface);
	}

	@Test
	void testUpdatePasswordInDB() {
		when(mockFastIvrDBInterface.updatePasswordByempID(anyString(), anyString())).thenReturn(1);
		serviceHelper.updatePasswordInDB("", "");
	}

	@Test
	void testUpdateAreaCodeInDB() {
		when(mockFastIvrDBInterface.updateAreaCodeByempID(anyString(), anyString())).thenReturn(1);
		serviceHelper.updateAreaCodeInDB("", "");
	}
	@Test
	void testIsNpaPresentInDB_true() {
		String npaPrefix = "303";
		List<Npa> mockNpaList = new ArrayList<>();
		Npa npa = new Npa();
		npa.setNpaPrefix("303");
		mockNpaList.add(npa);
		
		when(npaRepository.findByNpaPrefix(npaPrefix)).thenReturn(mockNpaList);
		
		boolean result = serviceHelper.isNpaPresentInDB(npaPrefix);
		assertEquals(true, result);
	}
	
	@Test
	void testIsNpaPresentInDB_false() {
		String npaPrefix = "303";
		List<Npa> mockNpaList = new ArrayList<>();
		
		when(npaRepository.findByNpaPrefix(npaPrefix)).thenReturn(mockNpaList);
		
		boolean result = serviceHelper.isNpaPresentInDB(npaPrefix);
		assertEquals(false, result);
	}

	@Test
	void testMatchEmployeeID_Sucess() {
		String securityCode = "45634";
		
		Optional<FastIvrUser> optionalUser = Optional.of(loadMockFastIvrUser(securityCode,"","N","",""));
		
		when(mockFastIvrDBInterface.findByempID(securityCode)).thenReturn(optionalUser);
		FastIvrUser responseTechData = serviceHelper.matchEmployeeID(securityCode);
		
		assertEquals(securityCode, responseTechData.getEmpID());
	}
	
	@Test
	void testMatchEmployeeID_Failure() {
		String securityCode = "45634";
		
		when(mockFastIvrDBInterface.findByempID(securityCode)).thenReturn(Optional.empty());
		FastIvrUser responseTechData = serviceHelper.matchEmployeeID(securityCode);
		
		assertNull(responseTechData);
	}

	@Test
	void testIsBirthdateSet_true() {
		assertTrue(serviceHelper.isBirthdateSet("232323"));
	}
	
	@Test
	void testIsBirthdateSet_false() {
		assertFalse(serviceHelper.isBirthdateSet(""));
	}

	@Test
	void testIsNpaExists_true() {
		assertTrue(serviceHelper.isNpaExists("303"));
	}
	
	@Test
	void testIsNpaExists_false() {
		assertFalse(serviceHelper.isNpaExists(""));
	}

	@Test
	void testValidateLoginPassword_Success() {
		String userEnteredPassword = "123456";
		String sessionId = "session123";
		String securityCode = "45634";
		IVRUserSession mockUserSession = loadIvrSession(sessionId, securityCode , false);
		String actualPassword = userEnteredPassword;
//		when(mockFastIvrDBInterface.findPasswordFromDB(mockUserSession.getEmpID())).thenReturn(actualPassword );
		when(mockFastIvrDBInterface.findPasswordByempID(mockUserSession.getEmpID())).thenReturn(Optional.of(actualPassword));
		String hookReturnCode = serviceHelper.validateLoginPassword(userEnteredPassword, mockUserSession);
		
		assertEquals(HOOK_RETURN_1, hookReturnCode);
	}
	
	@Test
	void testValidateLoginPassword_WrongPassword() {
		String userEnteredPassword = "123456";
		String sessionId = "session123";
		String securityCode = "45634";
		int secCodeLoginAttempt = 1;
		IVRUserSession mockUserSession = loadIvrSession(sessionId, securityCode , false);
		//TODO session object will not store password, fetch from DB and check 
		String actualPassword = "654321";
		
		//when(mockFastIvrDBInterface.findPasswordFromDB(mockUserSession.getEmpID())).thenReturn(actualPassword );
		when(mockFastIvrDBInterface.findPasswordByempID(mockUserSession.getEmpID())).thenReturn(Optional.of(actualPassword));
		String hookReturnCode = serviceHelper.validateLoginPassword(userEnteredPassword, mockUserSession);
		
		assertEquals(HOOK_RETURN_2, hookReturnCode);
	}
	
	@Test
	void testValidateLoginPassword_Jeopardy() {
		String userEnteredPassword = "123456";
		String sessionId = "session123";
		String empid = "45634";
		IVRUserSession mockUserSession = loadIvrSession(sessionId, empid , false);
		mockUserSession.setLoginJeopardyFlag(false);
		//TODO session object will not store password, fetch from DB and check 
		mockUserSession.setPasswordAttemptCounter(6);
		when(mockFastIvrDBInterface.findPasswordByempID(empid)).thenReturn(Optional.of("123456"));
		String jep = !mockUserSession.isLoginJeopardyFlag() ? "Y" :"N";
		when(mockFastIvrDBInterface.updateLoginJeopardyFlagByempID(jep, mockUserSession.getEmpID()))
		.thenReturn(1);
		
		String hookReturnCode = serviceHelper.validateLoginPassword(userEnteredPassword, mockUserSession);
		assertEquals(HOOK_RETURN_6, hookReturnCode);
	}

	@Test
	void testHasUserExpired_true() {
		//YYYY-MM-DD
		LocalDate dateToCheck =LocalDate.now().minusDays(1L);
		boolean result = serviceHelper.hasUserExpired(dateToCheck);
		assertTrue(result);
		
	}
	
	@Test
	void testHasUserExpired_false() {
		LocalDate dateToCheck =LocalDate.now().plusDays(1L);
		boolean result = serviceHelper.hasUserExpired(dateToCheck);
		assertFalse(result);
	}

//	@Test
//	void testIsUserPagerEnabled_PGCNTR_ALL() {
//		when(pcoutRepository.countPageCentreALL("ALL")).thenReturn(1);
//		IVRUserSession mockSession = loadIvrSession("session123","45634",true);
//		mockSession.setPagerCo("NET");
//		boolean actual = serviceHelper.isUserPagerEnabled(mockSession, mockSession.getPagerCo());
//		assertEquals(false, actual);
//	}
//	
//	@Test
//	void testIsUserPagerEnabled_PGCNTR_NOTALL_TECH_PAGERCO_EXISTS_IN_PCOUT() {
//		IVRUserSession mockSession = loadIvrSession("session123","45634",true);
//		mockSession.setEmpID(mockSession.getEmpID());
//		mockSession.setPagerCo("NET");
//		
//		when(pcoutRepository.countPageCentreALL("ALL")).thenReturn(0);
//		when(pcoutRepository.countPagerByEmp(mockSession.getEmpID())).thenReturn(1);
//		
//		boolean actual = serviceHelper.isUserPagerEnabled(mockSession, mockSession.getPagerCo());
//		assertEquals(false, actual);
//	}
//	
//	@Test
//	void testIsUserPagerEnabled_PGCNTR_NOTALL_TECH_PAGERCO_ABSENT_IN_PCOUT() {
//		IVRUserSession mockSession = loadIvrSession("session123","45634",true);
//		mockSession.setPagerCo("NET");
//		mockSession.setEmpID(mockSession.getEmpID());
//		
//		when(pcoutRepository.countPageCentreALL("ALL")).thenReturn(0);
//		when(pcoutRepository.countPagerByEmp(mockSession.getEmpID())).thenReturn(0);
//		
//		boolean actual = serviceHelper.isUserPagerEnabled(mockSession, mockSession.getPagerCo());
//		assertEquals(true, actual);
//	}
	
	/**
	 * Date format : MMddyyyy
	 */
	@Test
	void testBirthDateValidation_INPUT_SANITY_FAILED() {
		String mockUserInput = "";
	    String mockSessionId = "session123";
	    IVRUserSession mockSession = new IVRUserSession();
	    mockSession.setSessionId(mockSessionId);
	    String actualHookCode = serviceHelper.birthDateValidation(mockUserInput, mockSession);
	    String expectedHookCode = HOOK_RETURN_0 ;
	    
	    assertEquals(expectedHookCode,actualHookCode);
	}
	
	/**
	 * Date format : MMddyyyy
	 */
	@Test
	void testBirthDateValidation_VALID_DATE() {
		String mockUserInput = "02122023";
	    String mockSessionId = "session123";
	    IVRUserSession mockSession = new IVRUserSession();
	    mockSession.setEmpID("45634");
	    mockSession.setSessionId(mockSessionId);
	    
	    String actualHookCode = serviceHelper.birthDateValidation(mockUserInput, mockSession);
	    String expectedHookCode = HOOK_RETURN_1;
	    
	    assertEquals(expectedHookCode,actualHookCode);
	    assertEquals(mockUserInput, mockSession.getNewBirthDate());
	}
	
	@Test
	void testBirthDateValidation_INVALID_DATE_BASIC_SANITY_FAILED() {
		String mockUserInput = "02322023";
	    String mockSessionId = "session123";
	    IVRUserSession mockSession = new IVRUserSession();
	    mockSession.setEmpID("45634");
	    mockSession.setSessionId(mockSessionId);
	    String actualHookCode = serviceHelper.birthDateValidation(mockUserInput, mockSession);
	    String expectedHookCode = HOOK_RETURN_0;
	    
	    assertEquals(expectedHookCode,actualHookCode);
	}
	
	@Test
	void testBirthDateValidation_INVALID_DATE_OUT_OF_RANGE() {
		String mockUserInput = "09312023";
	    String mockSessionId = "session123";
	    IVRUserSession mockSession = new IVRUserSession();
	    mockSession.setEmpID("45634");
	    mockSession.setSessionId(mockSessionId);
	    String actualHookCode = serviceHelper.birthDateValidation(mockUserInput, mockSession);
	    String expectedHookCode = HOOK_RETURN_0;
	    
	    assertEquals(expectedHookCode,actualHookCode);
	}
	
	@Mock HttpClient mockHttpClient;

	@Mock HttpResponse<String> mockHttpResponse;
	
	@SuppressWarnings("unchecked")
	@Test
	void testPushOtpToTechCuid() throws IllegalArgumentException, IllegalAccessException, JsonProcessingException {
		String cuid = "testCuid";
        String otp = "123456";
        String mockResponse = "Response from API";
        String sessionId = "session123";
        IVRUserSession session = loadIvrSession(sessionId, "45653", false);
        NETMessagingRequestDto netRequestDto = new NETMessagingRequestDto();
		String mockRequestStr = "mock-net-request";
		
        when(mockObjectMapper.writeValueAsString(any(NETMessagingRequestDto.class))).thenReturn(mockRequestStr);
        when(mockIvrHttpClient.httpPostCall(anyString(), anyString(), anyString(), anyString())).thenReturn(mockResponse);
        
        Field field = ReflectionUtils.findField(IVRSignOnServiceHelper.class, "netUri");
        field.setAccessible(true);
        field.set(serviceHelper, "http://localhost/messaging");
		String actualResponse = serviceHelper.pushOtpToTechCuid(session);
		
		assertEquals(mockResponse, actualResponse);
	}
	
	@Test
	void testGenerateFourDigitOtp() {
		String generateFourDigitOtp = serviceHelper.generateFourDigitOtp();
		assertEquals(4, generateFourDigitOtp.length());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	void testLoadTechPagerConfiguration_Success() throws IllegalArgumentException, IllegalAccessException {
		String cuid = "testCuid";
		String mockResponse = "{\"deviceId\":[\"1,255167,PHONE,198765543\",\"2,252549,MAIL,,abc@xyz.com\"]}";
		IVRUserSession user = new IVRUserSession();
		user.setSessionId("session123");
		user.setCuid(cuid);
		
		//mock client 
		when(mockHttpClient.sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
		.thenReturn(CompletableFuture.completedFuture(mockHttpResponse));
		when(mockHttpResponse.body()).thenReturn(mockResponse);
		
		Field field = ReflectionUtils.findField(IVRSignOnServiceHelper.class, "netDeviceBaseUrl");
        field.setAccessible(true);
        field.set(serviceHelper, "http://localhost/messaging/device");
		
        IVRPagerConfig pagerConfig = serviceHelper.loadTechPagerConfiguration(user);
        assertTrue(pagerConfig.isMailEnabled());
        assertTrue(pagerConfig.isPhoneEnabled());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	void testLoadTechPagerConfiguration_Failure() throws IllegalArgumentException, IllegalAccessException {
		String cuid = "testCuid";
		IVRUserSession user = new IVRUserSession();
		user.setSessionId("session123");
		user.setCuid(cuid);
		String mockResponse = "{\"device_id\":[\"sdsds,POST\",\"dsdsd,DRAFT\"]}";
		
		//mock client 
		when(mockHttpClient.sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
		.thenReturn(CompletableFuture.completedFuture(mockHttpResponse));
		when(mockHttpResponse.body()).thenReturn(mockResponse);
		
		Field field = ReflectionUtils.findField(IVRSignOnServiceHelper.class, "netDeviceBaseUrl");
        field.setAccessible(true);
        field.set(serviceHelper, "http://localhost/messaging/device");
		
        IVRPagerConfig pagerConfig = serviceHelper.loadTechPagerConfiguration(user);
        assertFalse(pagerConfig.isMailEnabled());
        assertFalse(pagerConfig.isPhoneEnabled());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	void testLoadTechPagerConfiguration_Error() throws IllegalArgumentException, IllegalAccessException {
		String cuid = "testCuid";
		String mockResponse = "{\"error\":\"CUID'AB28383'notfoundinthecorporatedirectory\"}";
		IVRUserSession user = new IVRUserSession();
		user.setSessionId("session123");
		user.setCuid(cuid);
		//mock client 
		when(mockHttpClient.sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
		.thenReturn(CompletableFuture.completedFuture(mockHttpResponse));
		when(mockHttpResponse.body()).thenReturn(mockResponse);
		
		Field field = ReflectionUtils.findField(IVRSignOnServiceHelper.class, "netDeviceBaseUrl");
        field.setAccessible(true);
        field.set(serviceHelper, "http://localhost/messaging/device");
		
        IVRPagerConfig pagerConfig = serviceHelper.loadTechPagerConfiguration(user);
        assertFalse(pagerConfig.isMailEnabled());
        assertFalse(pagerConfig.isPhoneEnabled());
       
	}
	
	@Test
	void testIsUserPagerEnabled_Mobile_true() {
		String cuid = "testCuid";
		IVRUserSession user = new IVRUserSession();
		user.setSessionId("session123");
		user.setCuid(cuid);
		user.setCanBePagedEmail(false);
		user.setCanBePagedMobile(true);
		boolean userPagerEnabled = serviceHelper.isUserPagerEnabled(user);
		assertTrue(userPagerEnabled);
	}
	
	@Test
	void testIsUserPagerEnabled_Email_true() {
		String cuid = "testCuid";
		IVRUserSession user = new IVRUserSession();
		user.setSessionId("session123");
		user.setCuid(cuid);
		user.setCanBePagedEmail(true);
		user.setCanBePagedMobile(false);
		boolean userPagerEnabled = serviceHelper.isUserPagerEnabled(user);
		assertTrue(userPagerEnabled);
	}
	
	@Test
	void testIsUserPagerEnabled_false() {
		String cuid = "testCuid";
		IVRUserSession user = new IVRUserSession();
		user.setSessionId("session123");
		user.setCuid(cuid);
		user.setCanBePagedEmail(false);
		user.setCanBePagedMobile(false);
		boolean userPagerEnabled = serviceHelper.isUserPagerEnabled(user);
		assertFalse(userPagerEnabled);
	}
	
	@Test
	void testProcessJsonStringNETMessaging_Success() throws JsonMappingException, JsonProcessingException {
		String jsonString = "{\"eventId\":\"XXXX\",\"eventTime\":\"2023-09-15T13:38:34.316Z\",\"status\":\"Verified\",\"reasonCode\":\"200\",\"reasonDescription\":\"PostedeventXXXtotheNETqueue.CUID:XXXX\"}";
		
		NETMessagingResponseDto mockResponse = new ObjectMapper().readValue(jsonString, NETMessagingResponseDto.class);
		when(mockObjectMapper.readValue(jsonString, NETMessagingResponseDto.class)).thenReturn(mockResponse);
		boolean responseStatus = serviceHelper.processJsonStringNETMessaging(jsonString);
		assertTrue(responseStatus);
	}
	
	@Test
	void testProcessJsonStringNETMessaging_Failed() throws JsonMappingException, JsonProcessingException {
		String jsonString = "{\"eventId\":\"180575297\",\"eventTime\":\"2023-09-12T16:22:32.192Z\",\"status\":\"Error:Verificationfailed.ApplicationIdfailedverification.\",\"reasonCode\":\"1132\",\"reasonDescription\":\"MessagesendtoNETfailed\"}";
		
		NETMessagingResponseDto mockResponse = new ObjectMapper().readValue(jsonString, NETMessagingResponseDto.class);
		when(mockObjectMapper.readValue(jsonString, NETMessagingResponseDto.class)).thenReturn(mockResponse);
		boolean responseStatus = serviceHelper.processJsonStringNETMessaging(jsonString);
		assertFalse(responseStatus);
	}
	
	@Test
	void testUpdateTechnicianBirthdate() {
		String date = "04241994";
		String empid = "45634";
		when(mockFastIvrDBInterface.updateBirthdateByEmpID(date, empid)).thenReturn(1);
		int response = serviceHelper.updateTechnicianBirthdate(date, empid);
		
		assertEquals(1, response);
	}
	
	@Test
	void testAddParamterData() {
		String data1 = "Name";
		String data2 = "Address";
		List<IVRParameter> params = new ArrayList<>();
		IVRParameter param1 = new IVRParameter();
		IVRParameter param2 = new IVRParameter();
		param1.setData(data1);
		param2.setData(data2);
		params.add(param1);
		params.add(param2);
		
		List<IVRParameter> responseList = serviceHelper.addParamterData(data1, data2);
		assertEquals(data1, responseList.get(0).getData());
		assertEquals(data2, responseList.get(1).getData());
	}
	
	@Test
	void testCheckDeviceMobileOrEmail_SingleDevice() {
		String responseString = "{\"deviceId\":\"1,396965,MAIL,,Ashutosh.Mohanty@lumen.com\"}";
		IVRPagerConfig response = serviceHelper.checkDeviceMobileOrEmail(responseString);
		assertTrue(response.isMailEnabled());
		assertFalse(response.isPhoneEnabled());
	}
	
	@Test
	void testCheckDeviceMobileOrEmail_MultiDevice() {
		String responseString = "{\"deviceId\":[\"1,255167,PHONE,1,987654321\",\"2,252549,MAIL,,abc1234@abc.com\"]}";
		IVRPagerConfig response = serviceHelper.checkDeviceMobileOrEmail(responseString);
		assertTrue(response.isMailEnabled());
		assertTrue(response.isPhoneEnabled());
	}
	
	@Test
	void testUpdatePasswordExpireDate() {
		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setEmpID("123456");
		mockSession.setAge(30);
		LocalDate date = LocalDate.now().plusDays(mockSession.getAge());
		when(mockFastIvrDBInterface.updatePasswordExpireByEmpID(date, mockSession.getEmpID())).thenReturn(1);
		int records = serviceHelper.updatePasswordExpireDate(mockSession);
		assertEquals(1, records);
	}
	
	//COMMENT after testing 
//	@Test
//	void testLoadTechPagerConfiguration_ActualAPI() throws IllegalArgumentException, IllegalAccessException {
//		IVRUserSession session = loadIvrSession("session123", "456734", true);
//		session.setCuid("AD00912");
//		Field field = ReflectionUtils.findField(IVRSignOnServiceHelper.class, "netDeviceBaseUrl");
//        field.setAccessible(true);
//        field.set(serviceHelper, "https://netapp.corp.intranet/cgi-bin/cuid_display.cgi");
//		IVRPagerConfig techPagerConfiguration = serviceHelper.loadTechPagerConfiguration(session );
//		System.out.println("Phone enabled : "+techPagerConfiguration.isPhoneEnabled()+"\n Mail enabled: "+ techPagerConfiguration.isMailEnabled());
//	}

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
	
}
