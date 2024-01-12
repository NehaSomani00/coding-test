package com.lumen.fastivr.IVRService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.lumen.fastivr.IVRDto.IVRWebHookResponseDto;

import java.net.http.HttpTimeoutException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface IVRService {
	
	/**
	 * To process any event that comes under Sigon-on flow
	 * @param sessionId, currentState, userDtmfs
	 */
	public IVRWebHookResponseDto processSignOn(String sessionId, String currentState, List<String> userDtmfs);
	
	/**
	 * To process any event that comes under MLT-on flow
	 * @param request
	 */
	public IVRWebHookResponseDto processMLT(String sessionId, String currentState, List<String> userInputDTMFList)throws JsonMappingException, JsonProcessingException;

	public IVRWebHookResponseDto getSanityResponse();
	
	/**
	 * To process the current assignment Request by Telephone Number
	 * @param sessionId, currentState, userDtmfs
	 */
	public IVRWebHookResponseDto processLFACS(String sessionId, String currentState, List<String> userDtmfs)throws JsonMappingException, JsonProcessingException; 
	
	public IVRWebHookResponseDto processCNF(String sessionId, String currentState, List<String> userDtmfs) throws JsonMappingException, JsonProcessingException;

	public IVRWebHookResponseDto processAdministration(String sessionId, String currentState, List<String> userInputDTMFList)
			throws JsonMappingException, JsonProcessingException;

	public IVRWebHookResponseDto processChangeStatusOfCablePair(String sessionId, String currentState, List<String> userDtmfs) throws JsonMappingException, JsonProcessingException;

	public IVRWebHookResponseDto processConstructionActivity(String sessionId, String currentState,List<String> cleanDtmfInputList)throws JsonMappingException, JsonProcessingException;

	//public IVRWebHookResponseDto processMLT(String sessionId, String currentState, List<String> cleanDtmfInputList)throws JsonMappingException, JsonProcessingException;
	

	public IVRWebHookResponseDto processChangeorNewAssignServingTerminal(String sessionId, String currentState, List<String> userInputDTMFList) throws JsonProcessingException, HttpTimeoutException, ExecutionException, InterruptedException;

	
	public IVRWebHookResponseDto processCallerId(String sessionId, String currentState, List<String> userInputDTMFList) throws JsonMappingException, JsonProcessingException;
	

}