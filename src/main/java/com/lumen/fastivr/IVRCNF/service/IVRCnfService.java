package com.lumen.fastivr.IVRCNF.service;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.lumen.fastivr.IVRDto.IVRWebHookResponseDto;

public interface IVRCnfService {

	public IVRWebHookResponseDto processFND035(String sessionId, String currentState, List<String> userInputDTMFList) throws JsonMappingException, JsonProcessingException;
	
	public IVRWebHookResponseDto processFND055(String sessionId, String currentState, String userDTMFInput);
	
	public IVRWebHookResponseDto processFND059(String sessionId, String currentState) throws JsonMappingException, JsonProcessingException;

	public IVRWebHookResponseDto processFND060(String sessionId, String currentState) throws JsonMappingException, JsonProcessingException;
	
	public IVRWebHookResponseDto processFND075(String sessionId, String currentState, String userDTMFInput);
	
	public IVRWebHookResponseDto processFND085(String sessionId, String currentState);
	
	public IVRWebHookResponseDto processFND090(String sessionId, String currentState)  throws JsonMappingException, JsonProcessingException;
	
	public IVRWebHookResponseDto processFND135(String sessionId, String currentState);
	
	public IVRWebHookResponseDto processFND141(String sessionId, String currentState);
	
	public IVRWebHookResponseDto processFND143(String sessionId, String currentState) throws JsonMappingException, JsonProcessingException;
	
	public IVRWebHookResponseDto processFND145(String sessionId, String currentState, String userDTMFInput);
	
	public IVRWebHookResponseDto processFND155(String sessionId, String currentState, String userDTMFInput);
	
	public IVRWebHookResponseDto processFND170(String sessionId, String currentState, String userDTMFInput);
	
	public IVRWebHookResponseDto processFND215(String sessionId, String currentState, String nextState, String userDTMFInput);
	
	public IVRWebHookResponseDto processFND216(String sessionId, String nextState) throws JsonMappingException, JsonProcessingException;
	
	public IVRWebHookResponseDto processFND700(String sessionId, String currentState, String userDTMFInput);
	
	public IVRWebHookResponseDto processFND740(String sessionId, String currentState, List<String> userInputDTMFList);
	
	public IVRWebHookResponseDto processFND741(String sessionId, String currentState, List<String> userInputDTMFList);
	
	public IVRWebHookResponseDto processFND742(String sessionId, String currentState, List<String> userInputDTMFList);
	
	public IVRWebHookResponseDto processFND746(String sessionId, String currentState, List<String> userInputDTMFList) throws JsonMappingException, JsonProcessingException;
}
