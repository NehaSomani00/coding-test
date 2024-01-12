/**
 * 
 */
package com.lumen.fastivr.IVRCallerId;

import com.lumen.fastivr.IVRDto.IVRWebHookResponseDto;

/**
 * 
 */
public interface IVRCallerIdService {
	
	public IVRWebHookResponseDto processIDD011StateCode(String sessionId, String previousState, String currentState);
	public IVRWebHookResponseDto processIDD020StateCode(String sessionId, String currentState, String userDTMFInput);
	public IVRWebHookResponseDto processIDD035StateCode(String sessionId, String currentState, String userDTMFInput);
	
}
