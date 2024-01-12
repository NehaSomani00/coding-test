package com.lumen.fastivr.IVRCANST.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.lumen.fastivr.IVRDto.IVRWebHookResponseDto;

import java.net.http.HttpTimeoutException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface IVRCanstService {

	public IVRWebHookResponseDto processFTD011(String sessionId, String nextState, String userDTMFInput);
	
	public IVRWebHookResponseDto processFTD030(String sessionId, String nextState) throws JsonMappingException, JsonProcessingException;
	
	public IVRWebHookResponseDto processFTD035(String sessionId, String nextState) throws JsonMappingException, JsonProcessingException;
	
	public IVRWebHookResponseDto processFTD060(String sessionId, String nextState, List<String> userInputDTMFList) throws JsonMappingException, JsonProcessingException;
	
	public IVRWebHookResponseDto processFTD135(String sessionId, String nextState, String userDTMFInput);
	
	public IVRWebHookResponseDto processFTD120(String sessionId, String nextState);
	
	public IVRWebHookResponseDto processFTD160(String sessionId, String nextState) throws JsonMappingException, JsonProcessingException;
	
	public IVRWebHookResponseDto processFTD170(String sessionId, String nextState) throws JsonMappingException, JsonProcessingException;
	
	public IVRWebHookResponseDto processFTD190(String sessionId, String nextState) throws JsonMappingException, JsonProcessingException;
	
	public IVRWebHookResponseDto processFTD197(String sessionId, String nextState) throws JsonMappingException, JsonProcessingException;
	
	public IVRWebHookResponseDto processFTD210(String sessionId, String nextState);
	
	public IVRWebHookResponseDto processFTD220(String sessionId, String nextState) throws JsonMappingException, JsonProcessingException;
	
	public IVRWebHookResponseDto processFTD231(String sessionId) throws JsonProcessingException;

	
	public IVRWebHookResponseDto processFTD240(String sessionId) throws JsonProcessingException;
	
	public IVRWebHookResponseDto processFTD300(String sessionId, String nextState) throws JsonMappingException, JsonProcessingException;
	
	public IVRWebHookResponseDto processFTD315(String sessionId, String nextState, List<String> userInputDTMFList);
	
	public IVRWebHookResponseDto processFTD317(String sessionId, String nextState)throws JsonProcessingException;
	
	public IVRWebHookResponseDto processFTD320(String sessionId, String nextState) throws JsonMappingException, JsonProcessingException, HttpTimeoutException, InterruptedException, ExecutionException;

	public IVRWebHookResponseDto processFTD330(String sessionId, String stateFtd330)throws JsonMappingException, JsonProcessingException;

	public IVRWebHookResponseDto processFTD351(String sessionId, String nextState, String userDTMFInput);

	public IVRWebHookResponseDto processFTD400(String sessionId) throws JsonProcessingException, HttpTimeoutException, ExecutionException, InterruptedException;

	public IVRWebHookResponseDto processFTD370(String sessionId, String nextState, List<String> userInputDTMFList);

	public IVRWebHookResponseDto processFTD371(String sessionId, String nextState, List<String> userDTMFInput)
			throws JsonMappingException, JsonProcessingException;
	
	public IVRWebHookResponseDto processFTD380(String sessionId, String nextState)throws JsonMappingException, JsonProcessingException;
}
