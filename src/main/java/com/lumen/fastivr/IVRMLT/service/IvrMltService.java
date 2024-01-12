package com.lumen.fastivr.IVRMLT.service;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.lumen.fastivr.IVRDto.IVRWebHookResponseDto;

/**
 * Contract for the IVR MLT testing 
 * @author 002L2N744
 *
 */
public interface IvrMltService {
	
	// MLT Quick, Loop and Full Test
	
	//MLD021
	public IVRWebHookResponseDto processMLD021(String sessionId, String currentState,List<String> userDTMFInput);
	
	//MLD026
	public IVRWebHookResponseDto checkTechWorkingFromSameLineOrNot(String sessionId, String currentState,String userDTMFInput);
	
	//MLD027
	IVRWebHookResponseDto issueMLTTest(String sessionId)throws JsonMappingException, JsonProcessingException;
	
	//MLD040
	IVRWebHookResponseDto retriveMLTResult(String sessionId);
	
	//MLD075
	public IVRWebHookResponseDto checkPlayVoltageInformation(String sessionId, String currentState);
	
	int checkDcCraftSignatures();
	
	//MLD080
	IVRWebHookResponseDto playTipRingDcOhm(String sessionId, String currentState)throws JsonMappingException, JsonProcessingException;
	
	//MLD082
	public IVRWebHookResponseDto playTipGroundDcOhm(String sessionId, String currentState) throws JsonMappingException, JsonProcessingException;
	
	//MLD084
	public IVRWebHookResponseDto playRingGroundDcOhm(String sessionId, String currentState);
	
	//MLD086
	public IVRWebHookResponseDto playTipGroundVolts(String sessionId, String currentState);
	
	//MLD088
	public IVRWebHookResponseDto playRingGroundVolts(String sessionId, String currentState) throws JsonMappingException, JsonProcessingException;
	
	int checkAcCraftSignatures();
	
	//MLD090
	public IVRWebHookResponseDto playTipRingAcOhm(String sessionId, String currentState);
	
	//MLD092
	public IVRWebHookResponseDto playTipGroundAcOhm(String sessionId, String currentState);
	
	//MLD094
	public IVRWebHookResponseDto playRingGroundAcOhm(String sessionId, String currentState) throws JsonMappingException, JsonProcessingException;
	
	//MLD115
	int playTestVerCode(String value);
	
	
	//Common validate method for Add a tone & Remove a tone
	IVRWebHookResponseDto validateFacsTnTone(String sessionId, String currentState, List<String> userDTMFInputs);
	
	//Add tone
	IVRWebHookResponseDto addToneDuration_MLD307(String sessionId, String userInput);
	
	IVRWebHookResponseDto issueTonePlusRequest(String sessionId, String userInput)throws JsonMappingException, JsonProcessingException;
	
	
	//Remove a tone
	IVRWebHookResponseDto issueXRequest(String sessionId)throws JsonMappingException, JsonProcessingException;
	
}
