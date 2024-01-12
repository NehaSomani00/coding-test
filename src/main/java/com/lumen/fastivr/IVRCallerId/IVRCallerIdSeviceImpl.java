/**
 * 
 */
package com.lumen.fastivr.IVRCallerId;

import static com.lumen.fastivr.IVRUtils.IVRConstants.INQUIRY_BY_TN;
import static com.lumen.fastivr.IVRUtils.IVRConstants.INVALID_SESSION_ID;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.lumen.fastivr.IVRCacheManagement.IVRCacheService;
import com.lumen.fastivr.IVRDto.IVRWebHookResponseDto;
import com.lumen.fastivr.IVRLFACS.LfacsValidation;
import com.lumen.fastivr.IVRSessionManagement.IVRUserSession;
import com.lumen.fastivr.IVRUtils.IVRHookReturnCodes;

@Service
public class IVRCallerIdSeviceImpl implements IVRCallerIdService {

	private static final Logger LOGGER = LoggerFactory.getLogger(IVRCallerIdSeviceImpl.class);

	@Autowired
	private IVRCacheService cacheService;

	@Autowired
	private LfacsValidation tnValidation;


	@Value("#{${firstSetoFDefectiveCodes}}")
	Map<String, String> firsetSetOFDefectiveCodes = new HashMap<String, String>();

	@Value("#{${secondSetoFDefectiveCodes}}")
	Map<String, String> secondSetOFDefectiveCodes = new HashMap<String, String>();

	@Value("${cnf.change.pair.status.url}")
	private String changePairStatusUrl;

	@Override
	public IVRWebHookResponseDto processIDD011StateCode(String sessionId, String currentState, String userDTMFInput) {
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		IVRUserSession session = cacheService.getBySessionId(sessionId);

		if (session != null) {
			response = tnValidation.validateFacsTN(userDTMFInput, session);
			session.setFacsInqType(INQUIRY_BY_TN);
		} else {
			response.setHookReturnMessage(INVALID_SESSION_ID);
		}

		cacheService.updateSession(session);
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		return response;
	}

	@Override
	public IVRWebHookResponseDto processIDD020StateCode(String sessionId, String previousState, String currentState) {
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		IVRUserSession session = cacheService.getBySessionId(sessionId);

		if (session != null) {
			response.setHookReturnCode(IVRHookReturnCodes.HOOK_RETURN_1);
		} else {
			response.setHookReturnMessage(INVALID_SESSION_ID);
		}

		cacheService.updateSession(session);
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		return response;
	}

	@Override
	public IVRWebHookResponseDto processIDD035StateCode(String sessionId, String currentState, String userDTMFInput) {
		// TODO Auto-generated method stub
		return null;
	}
}