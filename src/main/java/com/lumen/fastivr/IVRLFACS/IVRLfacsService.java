/**
 * 
 */
package com.lumen.fastivr.IVRLFACS;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.lumen.fastivr.IVRDto.IVRWebHookResponseDto;

/**
 * 
 */
public interface IVRLfacsService {
	
	
	public IVRWebHookResponseDto processFID400Code(String sessionId, String nextState, String userDTMFInput);
	
	public IVRWebHookResponseDto processFID420Code(String sessionId, String nextState);
	
	public IVRWebHookResponseDto processFID429Code(String sessionId, String nextState);

	public IVRWebHookResponseDto processFID011(String sessionId, String nextState, String userDTMFInput);

	public IVRWebHookResponseDto processFID020Code(String sessionId, String nextState);
	
	public IVRWebHookResponseDto processFID500Code(String sessionId, String nextState, List<String> userInputDTMFList)throws JsonMappingException, JsonProcessingException;
	
	public IVRWebHookResponseDto processFID068Code(String sessionId, String nextState, List<String> userInputDTMFList)throws JsonMappingException, JsonProcessingException;

	public IVRWebHookResponseDto processFID025Code(String sessionId, String nextState, String userDTMFInput)throws JsonMappingException, JsonProcessingException;

	public IVRWebHookResponseDto processFID035Code(String sessionId, String nextState);

	public IVRWebHookResponseDto processFID045Code(String sessionId, String nextState);
	
	public IVRWebHookResponseDto processFID445Code(String sessionId, String previousState, String nextState, int userDTMFInput);
	
	public IVRWebHookResponseDto processFID455Code(String sessionId, String nextState);

	public IVRWebHookResponseDto processFID055Code(String sessionId, String nextState,String userInputDTMFList)throws JsonMappingException, JsonProcessingException;
	
	public IVRWebHookResponseDto processFID060Code(String sessionId, String nextState);
	
	public IVRWebHookResponseDto processFID515Code(String sessionId, String nextState)throws JsonMappingException, JsonProcessingException;

	public IVRWebHookResponseDto processFID525Code(String sessionId, String nextState,List<String>userInputDTMFList)throws JsonMappingException, JsonProcessingException;

	public IVRWebHookResponseDto processFID532Code(String sessionId, String nextState)throws JsonMappingException, JsonProcessingException;

	public IVRWebHookResponseDto processFID211Code(String sessionId, String nextState, String userDTMFInput);
	
	public IVRWebHookResponseDto processFID237Code(String sessionId, String nextState, List<String> userInputDTMFList);
	
	public IVRWebHookResponseDto processFID070Code(String sessionId, String nextState,int segmentNumber);

	public IVRWebHookResponseDto processFID535Code(String sessionId, String nextState)throws JsonMappingException, JsonProcessingException;

//	public IVRWebHookResponseDto processFID040Code(String sessionId, String nextState);
	
	public IVRWebHookResponseDto processFID600Code(String sessionId, String nextState);
	
	public IVRWebHookResponseDto processFID080Code(String sessionId, String nextState);
	
	public IVRWebHookResponseDto processFID285Code(String sessionId, String nextState);

	public IVRWebHookResponseDto processFID560Code(String sessionId, String nextState);
		
	public IVRWebHookResponseDto processFID271Code(String sessionId, String nextState);

	public IVRWebHookResponseDto processFID250Code(String sessionId, String nextState, int dmInput);

	public IVRWebHookResponseDto processFID274Code(String sessionId, String nextState);

	public IVRWebHookResponseDto processFID090Code(String sessionId, String nextState);
	
	public IVRWebHookResponseDto processFID300Code(String sessionId, String nextState);

	public IVRWebHookResponseDto processFID291Code(String sessionId, String nextState)throws JsonMappingException, JsonProcessingException;
	
	public IVRWebHookResponseDto processFID630Code(String sessionId, String nextState, int tnIndex);
	
	public IVRWebHookResponseDto processAdditonalLinesVoiceFID615(String sessionId, String nextState);

	public IVRWebHookResponseDto processAdditonalLinesVoiceFID625(String sessionId, String nextState);
	
	public IVRWebHookResponseDto processAdditonalLinesVoiceFID635(String sessionId);
	
	public IVRWebHookResponseDto processFID272Code(String sessionId, String nextState, List<String> userInputDTMFList);
	
	public IVRWebHookResponseDto processFID273Code(String sessionId, String nextState, List<String> userInputDTMFList);		
	
	public IVRWebHookResponseDto processFID282Code(String sessionId, String nextState);
	
	public IVRWebHookResponseDto processFID700Code(String sessionId, String nextState) throws JsonMappingException, JsonProcessingException;
	
	public IVRWebHookResponseDto processFID224Code(String sessionId, String nextState, List<String> userInputDTMFList);
}
