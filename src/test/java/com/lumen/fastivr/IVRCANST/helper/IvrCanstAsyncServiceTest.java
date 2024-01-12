package com.lumen.fastivr.IVRCANST.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumen.fastivr.IVRCANST.Dto.UpdateLoopResponseDto;
import com.lumen.fastivr.IVRCANST.entity.IVRCanstEntity;
import com.lumen.fastivr.IVRDto.ErrorList;
import com.lumen.fastivr.IVRDto.HostErrorList;
import com.lumen.fastivr.IVRDto.MessageStatus;
import com.lumen.fastivr.IVRDto.common.IVRHttpResponseDto;
import com.lumen.fastivr.IVRLFACS.IVRLfacsServiceHelper;
import com.lumen.fastivr.IVRSessionManagement.IVRUserSession;
import com.lumen.fastivr.httpclient.IVRHttpClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IvrCanstAsyncServiceTest {

	@InjectMocks
	private IvrCanstAsyncService ivrCanstAsyncService;

	@Mock
	private ObjectMapper objectMapper;

	@Mock
	private IVRHttpClient ivrHttpClient;

	@Mock
	private IVRLfacsServiceHelper ivrLfacsServiceHelper;

	@Test
	void testUpdateLoopRequestForFailure() throws Exception {
		// Arrange
		String sessionId = "session123";
		ErrorList errorList = new ErrorList();
		errorList.setErrorMessage("Error");
		List<ErrorList> errorListList = new ArrayList<>();
		errorListList.add(errorList);
		List<HostErrorList> hostErrorLists = new ArrayList<>();

		HostErrorList hostErrorList = new HostErrorList();
		hostErrorList.setErrorList(errorListList);
		hostErrorLists.add(hostErrorList);
		MessageStatus messageStatus = new MessageStatus();
		messageStatus.setHostErrorList(hostErrorLists);
		IVRHttpResponseDto responseDto = new IVRHttpResponseDto();
		responseDto.setResponseBody("response");
		UpdateLoopResponseDto updateLoopResponseDto = new UpdateLoopResponseDto();
		updateLoopResponseDto.setRequestId("7443875");
		updateLoopResponseDto.setMessageStatus(messageStatus);


		when(ivrHttpClient.httpPostApiCall(any(), any(), any(), any())).thenReturn(responseDto);
		when(ivrLfacsServiceHelper.cleanResponseString("response")).thenReturn("cleanJsonString");
		when(objectMapper.readValue("cleanJsonString", UpdateLoopResponseDto.class)).thenReturn(updateLoopResponseDto);

		CompletableFuture<String> result = ivrCanstAsyncService.updateLoopRequest(sessionId,"json string", "url", new IVRUserSession(), new IVRCanstEntity());

		// Assert
		assertEquals("SUCCESS", result.get());

	}

	@Test
	void testUpdateLoopRequestForSuccess() throws Exception {
		// Arrange
		String sessionId = "session123";

		MessageStatus messageStatus = new MessageStatus();
		messageStatus.setErrorStatus("S");

		IVRUserSession userSession = new IVRUserSession();
		userSession.setCanBePagedMobile(Boolean.TRUE);
		IVRHttpResponseDto responseDto = new IVRHttpResponseDto();
		responseDto.setResponseBody("response");
		UpdateLoopResponseDto updateLoopResponseDto = new UpdateLoopResponseDto();
		updateLoopResponseDto.setRequestId("7443875");
		updateLoopResponseDto.setMessageStatus(messageStatus);


		when(ivrHttpClient.httpPostApiCall(any(), any(), any(), any())).thenReturn(responseDto);
		when(ivrLfacsServiceHelper.cleanResponseString("response")).thenReturn("cleanJsonString");
		when(objectMapper.readValue("cleanJsonString", UpdateLoopResponseDto.class)).thenReturn(updateLoopResponseDto);

		CompletableFuture<String> result = ivrCanstAsyncService.updateLoopRequest(sessionId,"json string", "url", userSession, new IVRCanstEntity());

		// Assert
		assertEquals("SUCCESS", result.get());

	}

}