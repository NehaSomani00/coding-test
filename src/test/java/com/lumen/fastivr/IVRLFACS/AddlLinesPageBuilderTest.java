package com.lumen.fastivr.IVRLFACS;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumen.fastivr.IVRDto.MessageStatus;
import com.lumen.fastivr.IVRDto.additionalLines.AdditionalLinesReportResponseDto;
import com.lumen.fastivr.IVRSessionManagement.IVRUserSession;
import com.lumen.fastivr.IVRUtils.IVRConstants;

@ExtendWith(MockitoExtension.class)
class AddlLinesPageBuilderTest {
	
	@InjectMocks
	private AddlLinesPageBuilder addlLinesPageBuilder;
	
	@Mock
	private ObjectMapper mockObjectMapper;
	
	@Mock
	private IVRLfacsServiceHelper mockIvrLfacsServiceHelper;
	
	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testProcessAddlLinesResponse() throws JsonProcessingException {
		String ckid = "inquired TN";
		List<String> list = new ArrayList<>();
		list.add(ckid);
		list.add("Addl Lines1");
		IVRUserSession session = new IVRUserSession();
		session.setSessionId("session123");
		AdditionalLinesReportResponseDto response = new AdditionalLinesReportResponseDto();
		MessageStatus messageStatus = new MessageStatus();
		messageStatus.setErrorStatus("S");
		messageStatus.setErrorMessage("");
		response.setMessageStatus(messageStatus );
		response.setReturnDataSet(list);
		
		when(mockIvrLfacsServiceHelper.sendTestResultToTech(anyString(), anyString(), anyString(), any())).thenReturn(true);
		addlLinesPageBuilder.processAddlLinesResponse(response, session, ckid, IVRConstants.NET_MAIL_DEVICE);
		verify(mockIvrLfacsServiceHelper, times(1)).sendTestResultToTech(anyString(), anyString(), anyString(), any());
	}

}
