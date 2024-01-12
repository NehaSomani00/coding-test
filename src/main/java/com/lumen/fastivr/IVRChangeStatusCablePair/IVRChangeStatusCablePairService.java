/**
 * 
 */
package com.lumen.fastivr.IVRChangeStatusCablePair;

import java.util.List;

import com.lumen.fastivr.IVRDto.IVRWebHookResponseDto;

/**
 * 
 */
public interface IVRChangeStatusCablePairService {
	
	public IVRWebHookResponseDto processFPD005StateCode(String sessionId, String previousState, String currentState);
	public IVRWebHookResponseDto processFPD011StateCode(String sessionId, String currentState, String userDTMFInput);
	public IVRWebHookResponseDto processFPD020StateCode(String sessionId, String currentState, String userDTMFInput);
	public IVRWebHookResponseDto processFPD060DefectiveCode(String sessionId, String currentState, String nextState, String userDTMFInput);
	public IVRWebHookResponseDto processFPD060StateCode(String sessionId, String currentState, IVRWebHookResponseDto response, List<String> userDtmfsList);
	public IVRWebHookResponseDto processFPD100StateCode(IVRWebHookResponseDto response);
	
}
