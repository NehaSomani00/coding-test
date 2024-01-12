package com.lumen.fastivr.IVRController;

import static com.lumen.fastivr.IVRUtils.IVRConstants.DTMF_INPUT_1;
import static com.lumen.fastivr.IVRUtils.IVRConstants.LFACS_EVENT;
import static com.lumen.fastivr.IVRUtils.IVRConstants.SIGNON_EVENT;
import static com.lumen.fastivr.IVRUtils.IVRConstants.SUCCESS;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_3;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumen.fastivr.IVRCacheManagement.IVRCacheService;
import com.lumen.fastivr.IVRDto.IVRParameter;
import com.lumen.fastivr.IVRDto.IVRWebHookResponseDto;
import com.lumen.fastivr.IVREndpointSecurity.entity.EndpointSecurity;
import com.lumen.fastivr.IVREndpointSecurity.service.EndpointSecurityServiceImpl;
import com.lumen.fastivr.IVRService.IVRServiceImpl;
import com.lumen.fastivr.IVRUtils.IVRConstants;
import com.lumen.fastivr.IVRUtils.IVRUtility;
//static imports: MockMvcRequestBuilders.*, MockMvcResultMatchers.*
import com.lumen.fastivr.IVRWebHookEvent.GenesysCloudWebhookEvent;
import com.lumen.fastivr.IVRWebHookEvent.IVRDtmfInput;

//@WebMvcTest(IVRWebHookController.class)
@ExtendWith(MockitoExtension.class)
class IVRWebHookControllerTest {

	private static final String SERVICE_ASSURANCE_V1_TROUBLE_VOICE_IVR_WEBHOOK_SANITY = "/ServiceAssurance/v1/Trouble/voiceIvrWebhook/sanity";

	private static final String SERVICE_ASSURANCE_V1_TROUBLE_VOICE_IVR_WEBHOOK = "/ServiceAssurance/v1/Trouble/voiceIvrWebhook";
	
	private static final String SERVICE_ASSURANCE_V1_TROUBLE_VOICE_IVR_WEBHOOK_ADDUSER = "/ServiceAssurance/v1/Trouble/voiceIvrWebhook/security/addUser";
	
	private static final String SERVICE_ASSURANCE_V1_TROUBLE_VOICE_IVR_WEBHOOK_UPDATEUSER = "/ServiceAssurance/v1/Trouble/voiceIvrWebhook/security/updateUser";

	private MockMvc mockMvc;

	private IVRWebHookController controller;

	@Mock
	private IVRUtility mockIvrUtility;

	@Mock
	private IVRServiceImpl mockIvrService;
	
	@Mock
	private EndpointSecurityServiceImpl mockSecurityService;
	
//	@MockBean
//	private EndpointSecurityServiceImpl mockEndpointSecurityService;
//	
	@Mock
	private IVRCacheService cacheService;

	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);
		controller = new IVRWebHookController(mockIvrService, mockIvrUtility, mockSecurityService);
		mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
	}

	/*
	 * endpoint: /api/fastivr/v1/sanity
	 */
	@Test
	void testSanityTestEndpoint() throws Exception {
		IVRWebHookResponseDto mockResponse = new IVRWebHookResponseDto();
		IVRParameter param = new IVRParameter();
		param.setData("Dummy-Param");
		List<IVRParameter> params = new ArrayList<>();
		params.add(param);

		mockResponse.setSessionId("Dummy Session Id");
		mockResponse.setCurrentState("FASTIVR is running");
		mockResponse.setHookReturnMessage("Sucess");
		mockResponse.setParameters(params);

		// Mocking the behavior
		when(mockIvrService.getSanityResponse()).thenReturn(mockResponse);

		// perform the GET request
		mockMvc.perform(get(SERVICE_ASSURANCE_V1_TROUBLE_VOICE_IVR_WEBHOOK_SANITY))
//		.andDo(print())
				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.hook_return_message").value("Sucess"));

		// Verify that the service method was called
		verify(mockIvrService, times(1)).getSanityResponse();
		verifyNoMoreInteractions(mockIvrService);
	}

	/*
	 * endpoint: /api/fastivr/v1/webhook
	 */
	@Test
	void testHandleWebhookEvent() throws JsonProcessingException, Exception {
		GenesysCloudWebhookEvent mockEvent = new GenesysCloudWebhookEvent();

		IVRDtmfInput userInput1 = new IVRDtmfInput();
		userInput1.setDtmfInput("security_code");
		IVRDtmfInput userInput2 = new IVRDtmfInput();
		userInput2.setDtmfInput("password");
		List<IVRDtmfInput> inputs = new ArrayList<IVRDtmfInput>();
		inputs.add(userInput1);
		inputs.add(userInput2);

		mockEvent.setSessionId("session123");
		mockEvent.setCurrentState("SS0110");
		mockEvent.setUserDtmfInputs("security_code,password");

		IVRWebHookResponseDto mockResponse = new IVRWebHookResponseDto();
		mockResponse.setSessionId("session123");
		mockResponse.setCurrentState("SSD110");
		mockResponse.setHookReturnMessage("Invalid Credentials");
		mockResponse.setHookReturnCode("2");

		when(mockIvrUtility.convertStateToEvent(Mockito.anyString(), anyString())).thenReturn(SIGNON_EVENT);
		when(mockIvrService.processSignOn(anyString(), anyString(), Mockito.anyList())).thenReturn(mockResponse);

		mockMvc.perform(post(SERVICE_ASSURANCE_V1_TROUBLE_VOICE_IVR_WEBHOOK).contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(mockEvent)))
//        	.andDo(print())
				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.hook_return_message").value("Invalid Credentials"))
				.andExpect(jsonPath("$.hook_return_code").value("2"));

		// Verify that the service method was called
		verify(mockIvrUtility, times(1)).convertStateToEvent(Mockito.anyString(), anyString());
		verify(mockIvrService, times(1)).processSignOn(anyString(), anyString(), Mockito.anyList());
	}

	@Test
	void testHandleWebhookLFACSEvent() throws JsonProcessingException, Exception {

		GenesysCloudWebhookEvent mockEvent = new GenesysCloudWebhookEvent();

		IVRDtmfInput userInput1 = new IVRDtmfInput();
		userInput1.setDtmfInput(DTMF_INPUT_1);
		List<IVRDtmfInput> inputs = new ArrayList<IVRDtmfInput>();
		inputs.add(userInput1);
		
		mockEvent.setSessionId("session123");
		mockEvent.setCurrentState("FI0030");
		mockEvent.setUserDtmfInputs(DTMF_INPUT_1);

		IVRWebHookResponseDto mockResponse = new IVRWebHookResponseDto();
		mockResponse.setSessionId("session123");
		mockResponse.setCurrentState("FID035");
		mockResponse.setHookReturnMessage(SUCCESS);
		mockResponse.setHookReturnCode(HOOK_RETURN_3);

		when(mockIvrUtility.convertStateToEvent(Mockito.anyString(), anyString())).thenReturn(LFACS_EVENT);
		when(mockIvrService.processLFACS(anyString(), anyString(), Mockito.anyList())).thenReturn(mockResponse);

		mockMvc.perform(post(SERVICE_ASSURANCE_V1_TROUBLE_VOICE_IVR_WEBHOOK).contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(mockEvent)))
//        	.andDo(print())
				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.hook_return_message").value(SUCCESS))
				.andExpect(jsonPath("$.hook_return_code").value(HOOK_RETURN_3));

		// Verify that the service method was called
		verify(mockIvrUtility, times(1)).convertStateToEvent(Mockito.anyString(), anyString());
		verify(mockIvrService, times(1)).processLFACS(anyString(), anyString(), Mockito.anyList());
	}

	// Helper method to convert an object to JSON string
	private String asJsonString(Object obj) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(obj);
	}
	
	@Test
	void testaddIVRUser() throws JsonProcessingException, Exception {
		EndpointSecurity user = new  EndpointSecurity();
		user.setUsername("admin");
		user.setSecret("admin");
		user.setEnabled(true);
		
		doNothing().when(mockSecurityService).addUser(any(EndpointSecurity.class));
		
		mockMvc.perform( post(SERVICE_ASSURANCE_V1_TROUBLE_VOICE_IVR_WEBHOOK_ADDUSER).contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(user)).contentType(MediaType.APPLICATION_JSON))
		.andDo(print())
				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().string(IVRConstants.ENDPOINT_SECURITY_SUCCESS_ADD));
	}
	
	@Test
	void testupdateIVRUser() throws JsonProcessingException, Exception {
		EndpointSecurity user = new  EndpointSecurity();
		user.setUsername("admin");
		user.setSecret("admin");
		user.setEnabled(true);
		
		doNothing().when(mockSecurityService).updateUser(any(EndpointSecurity.class));
		
		mockMvc.perform( post(SERVICE_ASSURANCE_V1_TROUBLE_VOICE_IVR_WEBHOOK_UPDATEUSER).contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(user)).contentType(MediaType.APPLICATION_JSON))
		.andDo(print())
				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().string(IVRConstants.ENDPOINT_SECURITY_SUCCESS_UPD));
	}

//	@Test
//	void testCleanUserDTMFInput() {
//		String sessionId = "session123";
//		String dtmfInput = "1#";
//
//		String actual = controller.cleanUserDTMFInput(sessionId, dtmfInput);
//
//		assertEquals("1", actual);
//	}
//
//	@Test
//	void testCleanUserDTMFInput_BadUserInput() {
//		String sessionId = "session123";
//		String dtmfInput = "";
//
//		Exception ex = assertThrows(BadUserInputException.class,
//				() -> controller.cleanUserDTMFInput(sessionId, dtmfInput));
//
//		assertEquals("User has provided invalid input as :" + dtmfInput, ex.getMessage());
//
//	}
	
	@Test
	void testMultipleCommaInputs() {
		String userInputs = "";
		 List<String> list = Arrays.asList(userInputs.split(","));
		 System.out.println(list);
		
	}

}
