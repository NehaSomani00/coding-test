package com.lumen.fastivr.httpclient;

import static com.lumen.fastivr.IVRUtils.IVRConstants.NETAPI_APPLICATION_ID;
import static com.lumen.fastivr.IVRUtils.IVRConstants.NETAPI_MESSAGING_SUCCESS_CODE;
import static com.lumen.fastivr.IVRUtils.IVRConstants.NETAPI_SEND_TYPE;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumen.fastivr.IVRBusinessException.BusinessException;
import com.lumen.fastivr.IVRDto.NETMessagingRequestDto;
import com.lumen.fastivr.IVRDto.NETMessagingResponseDto;
import com.lumen.fastivr.IVRDto.common.IVRHttpResponseDto;

@Component
public class IVRHttpClient {

	final static Logger LOGGER = LoggerFactory.getLogger(IVRHttpClient.class);

	@Autowired
	private HttpClient httpClient;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Value("${netapp.api.pager.secret}")
	private String applicationSecret;

	/**
	 * This is the POST API call for FASTIVR to External application.
	 * This is a generic POST method, that can be reused throughout the IVR application
	 * Please use {@code IVRHttpClient-> httpPostApiCall} 
	 * @param jsonRequest
	 * @param url
	 * @param sessionId
	 * @return responseString
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	@Deprecated(since = "1.0.0", forRemoval = true)
	public String httpPostCall(String jsonRequest, String url, String sessionId, String requestName) {
		LOGGER.info("Session id: "+ sessionId + ", "+ requestName+ ", Request JSON: "+ jsonRequest);
		LOGGER.info("Session id: "+ sessionId + ", "+ ", Request URL: "+ url);
		String responseString = null;
		HttpRequest httpRequest = HttpRequest.newBuilder()
								.timeout(Duration.ofMinutes(2L))
								.header("Content-Type", "application/json")
								.uri(URI.create(url.trim()))
								.POST(BodyPublishers.ofString(jsonRequest)).build();

		CompletableFuture<String> futureResponse = httpClient.sendAsync(httpRequest, BodyHandlers.ofString())
				.thenApply(response -> response.body());
		
		try {
			responseString = futureResponse.get();
			LOGGER.info("Session id:" + sessionId + ", " + requestName + " : Response JSON: " + responseString);

		} catch (InterruptedException | ExecutionException e) {
			LOGGER.error("Session id:" + sessionId + ", Error while fetching API response: ", e);
			throw new BusinessException(sessionId, "Error while fetching data from API");
		}
		return responseString;
	}
	
	/**
	 * This is the GET API call for FASTIVR to External application.
	 * This is a generic GET method, that can be reused throughout the IVR application 
	 * @param uri
	 * @param sessionId
	 * @param requestName
	 * @return
	 */
	public String httpGetCall(URI uri, String sessionId, String requestName) {
		LOGGER.info("Session id: " + sessionId + ", " + requestName + ", Request URI: " + uri.toString());
		HttpRequest httpRequest = HttpRequest.newBuilder()
									.uri(uri)
									.GET()
									.build();

		CompletableFuture<String> futureResponse = httpClient
													.sendAsync(httpRequest, BodyHandlers.ofString())
													.thenApply(response -> response.body());
		
		String responseString = "";
		try {
			responseString = futureResponse.get();
			LOGGER.info("Session id:" + sessionId + ", " + requestName + " : Response JSON: " + responseString);
		} catch (InterruptedException | ExecutionException e) {
			LOGGER.error("Session id:" + sessionId + ", Error while fetching API response: ", e);
			throw new BusinessException(sessionId, "Error while fetching data from API");
		}

		return responseString;
	}
	
	/**
	 * This is created specifically for LFACS purpose, as we need to clean the response before using it
	 * @param responseString
	 * @return
	 */
	public String cleanResponseString(String responseString) {
		//Since Wrapper api is sending json response enclosed within a "" and having escape characters \
		String jsonString = responseString.replace("\\", "");
		return jsonString.substring(1, jsonString.length()-1);
	}
	
	/**
	 * Builds the Request payload for the NET Messaging Endpoint api
	 * @param cuid
	 * @param messageSubject
	 * @param messageText
	 * @param device
	 * @return
	 */
	public NETMessagingRequestDto buildNetRequest(String cuid, String messageSubject, String messageText, String device) {
		NETMessagingRequestDto netRequest = new NETMessagingRequestDto();
		netRequest.setApplicationId(NETAPI_APPLICATION_ID);
		netRequest.setApplicationKey(applicationSecret);
		netRequest.setTo(cuid);
		netRequest.setFrom(NETAPI_APPLICATION_ID);
		netRequest.setSendType(NETAPI_SEND_TYPE);
		netRequest.setDevice(device);
		netRequest.setSubject(messageSubject);
		netRequest.setMessageText(messageText);
		
		return netRequest;
	}
	
	/**
	 * Processes the Response String.
	 * If the OTP has been sent to Technician, it sends a true , else false
	 * @param jsonString
	 * @return
	 */
	public boolean processJsonStringNETMessaging(String jsonString) {
		try {
			NETMessagingResponseDto response = objectMapper.readValue(jsonString, NETMessagingResponseDto.class);
			if (response.getReasonCode().equalsIgnoreCase(NETAPI_MESSAGING_SUCCESS_CODE)) {
				return true;
			}
		} catch (JsonProcessingException e) {
			LOGGER.error("Error in processing the JSON from NET api: ", e);
		}
		return false;
	}
	
	/**
	 * This is the POST API call for FASTIVR to External application.
	 * This is a generic POST method, that can be reused throughout the IVR application 
	 * The only difference from the above duplicate post call is that this method returns a {@link IVRHttpResponseDto} object
	 * which contains more metadata than the String output of the previous method
	 * @param jsonRequest
	 * @param url
	 * @param sessionId
	 * @param requestName
	 * @return IVRHttpResponseDto
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public IVRHttpResponseDto httpPostApiCall(String jsonRequest, String url, String sessionId, String requestName) throws HttpTimeoutException {
		LOGGER.info("Session id: "+ sessionId + ", "+ requestName+ ", Request JSON: "+ jsonRequest);
		LOGGER.info("Session id: "+ sessionId + ", " +requestName+ ", Request URL: "+ url);
		IVRHttpResponseDto response = new IVRHttpResponseDto();
		try {
		
		HttpRequest httpRequest = HttpRequest.newBuilder()
								.timeout(Duration.ofMinutes(2L))
								.header("Content-Type", "application/json")
								.uri(URI.create(url.trim()))
								.POST(BodyPublishers.ofString(jsonRequest)).build();

		CompletableFuture<HttpResponse<String>> futureResponse = httpClient.sendAsync(httpRequest, BodyHandlers.ofString());
		
			response.setStatusCode(futureResponse.get().statusCode());
			response.setResponseBody(futureResponse.get().body());
			LOGGER.info("Session id:" + sessionId + ", " + requestName + " : Response JSON: " + response.getResponseBody());

		}catch(IllegalArgumentException  e) {
			response.setStatusCode(HttpStatus.BAD_REQUEST.value());
			LOGGER.error("Session id:" + sessionId + ", IllegalArgumentException Exception Occured :: httpPostApiCall : ", e);
		}
		catch (InterruptedException | ExecutionException e) {
			response.setStatusCode(HttpStatus.BAD_GATEWAY.value());
			LOGGER.error("Session id:" + sessionId + ", Exception Exception Occured :: httpPostApiCall :", e);
		}
		return response;
	}
}
