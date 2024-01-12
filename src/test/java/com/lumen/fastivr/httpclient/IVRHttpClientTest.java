package com.lumen.fastivr.httpclient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import com.lumen.fastivr.IVRDto.common.IVRHttpResponseDto;
import com.lumen.fastivr.IVRSessionManagement.IVRUserSession;

@ExtendWith(MockitoExtension.class)
public class IVRHttpClientTest {
	
	@InjectMocks
	private IVRHttpClient ivrHttpClient;
	
	@Mock
	private HttpClient mockHttpClient;
	
	@Mock 
	private HttpResponse<String> mockHttpResponse;
	
	@Mock 
	private CompletableFuture<HttpResponse<String>> mockFutureResponse;
	
	@SuppressWarnings("unchecked")
	@Test
	void shouldReturnSuccessResp() {
		String sessionId = "session123";
		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId(sessionId);
		String jsonRequestString = "mock-request-string";
		String responseString = "\"mock-lfacs-response\"";
		
		// mocking
		when(mockHttpClient.sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
				.thenReturn(CompletableFuture.completedFuture(mockHttpResponse));
		when(mockHttpResponse.body()).thenReturn(responseString);

		String responseJsonStr = ivrHttpClient.httpPostCall(jsonRequestString,"http://localhost/lfacs/currentassignment", sessionId, "Test API");
		String actualJsonStr =  ivrHttpClient.cleanResponseString(responseJsonStr);
		assertEquals("mock-lfacs-response", actualJsonStr);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	void testHttpGetCall() {
		String sessionId = "session123";
		String responseString = "mock-response";
		URI uri = URI.create("http://localhost/lfacs/dummy");
		when(mockHttpClient.sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
		.thenReturn(CompletableFuture.completedFuture(mockHttpResponse));
		when(mockHttpResponse.body()).thenReturn(responseString);	
		
		String response = ivrHttpClient.httpGetCall(uri, sessionId, "Test API");
		assertEquals(responseString, response);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	void shouldReturnOKRespObject() throws HttpTimeoutException, InterruptedException, ExecutionException {
		String sessionId = "session123";
		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId(sessionId);
		String jsonRequestString = "mock-request-string";
		String responseString = "\"mock-lfacs-response\"";
		
		
		when(mockHttpClient.sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
				.thenReturn(mockFutureResponse);
		when(mockFutureResponse.get()).thenReturn(mockHttpResponse);
		when(mockFutureResponse.get().body()).thenReturn(responseString);
		when(mockFutureResponse.get().statusCode()).thenReturn(HttpStatus.OK.value());

		IVRHttpResponseDto responseDto = ivrHttpClient.httpPostApiCall(jsonRequestString,"http://localhost/lfacs/currentassignment", sessionId, "Test API");
		String actualJsonStr =  ivrHttpClient.cleanResponseString(responseDto.getResponseBody());
		assertEquals("mock-lfacs-response", actualJsonStr);
		assertEquals(HttpStatus.OK.value(),responseDto.getStatusCode());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	void shouldReturnBadRequestExceptionStatusCode() throws HttpTimeoutException {
		String sessionId = "session123";
		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId(sessionId);
		String jsonRequestString = "mock-request-string";
		
		
		when(mockHttpClient.sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
		.thenThrow(IllegalArgumentException.class);
		
		IVRHttpResponseDto responseDto = ivrHttpClient.httpPostApiCall(jsonRequestString,"http://localhost/lfacs/currentassignment", sessionId, "Test API");
		assertEquals(HttpStatus.BAD_REQUEST.value(),responseDto.getStatusCode());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	void shouldReturnBadGateWayExceptionStatusCode() throws HttpTimeoutException, ExecutionException, InterruptedException {
		String sessionId = "session123";
		IVRUserSession mockSession = new IVRUserSession();
		mockSession.setSessionId(sessionId);
		String jsonRequestString = "mock-request-string";
		
		
		when(mockHttpClient.sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
				.thenReturn(mockFutureResponse);
		
		when(mockFutureResponse.get()).thenThrow(InterruptedException.class);

		IVRHttpResponseDto responseDto = ivrHttpClient.httpPostApiCall(jsonRequestString,"http://localhost/lfacs/currentassignment", sessionId, "Test API");
		assertEquals(HttpStatus.BAD_GATEWAY.value(),responseDto.getStatusCode());
	}
}
