package com.lumen.fastivr.IVRController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.lumen.fastivr.IVRDto.IVRWebHookResponseDto;
import com.lumen.fastivr.IVREndpointSecurity.entity.EndpointSecurity;
import com.lumen.fastivr.IVREndpointSecurity.service.EndpointSecurityService;
import com.lumen.fastivr.IVRService.IVRService;
import com.lumen.fastivr.IVRUtils.IVRConstants;
import com.lumen.fastivr.IVRUtils.IVRUtility;
import com.lumen.fastivr.IVRWebHookEvent.GenesysCloudWebhookEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpTimeoutException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.lumen.fastivr.IVRUtils.IVRConstants.*;

@RestController
@RequestMapping("/ServiceAssurance/v1/Trouble/voiceIvrWebhook")
public class IVRWebHookController {
	
	//TODO change all bean injections to constructor injections
	private final IVRService ivrService;
	
	private final IVRUtility ivrUtility;
	
	private final EndpointSecurityService endpointSecurityService;
	
	public IVRWebHookController(IVRService ivrService, IVRUtility ivrUtility,
			EndpointSecurityService endpointSecurityService) {
		super();
		this.ivrService = ivrService;
		this.ivrUtility = ivrUtility;
		this.endpointSecurityService = endpointSecurityService;
	}

	final static Logger LOGGER = LoggerFactory.getLogger(IVRWebHookController.class);
	
	@GetMapping(path = "/sanity", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<IVRWebHookResponseDto> sanityTestEndpoint() {
		IVRWebHookResponseDto response = ivrService.getSanityResponse();
		return ResponseEntity.ok(response);
	}
	
	/**
	 * This endpoint to be used to Add any security user in IVR database
	 * This user will be able to access the endpoints of the applications 
	 * servlet path= /ServiceAssurance/v1/Trouble/voiceIvrWebhook/security/addUser
	 * @param securityUser
	 * @return
	 */
	@PostMapping(path = "/security/addUser", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> addIVRUser(@RequestBody EndpointSecurity securityUser) {
		try {
			endpointSecurityService.addUser(securityUser);
			return new ResponseEntity<String>(IVRConstants.ENDPOINT_SECURITY_SUCCESS_ADD, HttpStatus.OK);
		} catch (Exception e) {
			String errMsg = IVRConstants.ENDPOINT_SECURITY_SUCCESS_ADD_ERR+ ":::" + e.getLocalizedMessage();
			throw new SecurityException(errMsg, e);
		}

	}
	
	
	/**
	 * This endpoint to be used to Update any security user in IVR database
	 * This user will be able to access the endpoints of the applications 
	 * servlet path= /ServiceAssurance/v1/Trouble/voiceIvrWebhook/security/updateUser
	 * @param securityUser
	 * @return
	 */
	@PostMapping(path = "/security/updateUser", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> updateIVRUser(@RequestBody EndpointSecurity securityUser) {
		try {
			endpointSecurityService.updateUser(securityUser);
			return new ResponseEntity<String>(IVRConstants.ENDPOINT_SECURITY_SUCCESS_UPD, HttpStatus.OK);
		} catch (Exception e) {
			String errMsg = IVRConstants.ENDPOINT_SECURITY_SUCCESS_UPD_ERR+ ":::" + e.getLocalizedMessage();
			throw new SecurityException(errMsg, e);
		}

	}

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
    		produces = MediaType.APPLICATION_JSON_VALUE)
	public IVRWebHookResponseDto handleWebhookEvent(@RequestBody GenesysCloudWebhookEvent webhookEvent)
			throws JsonMappingException, JsonProcessingException, HttpTimeoutException, ExecutionException, InterruptedException {
    
    	String sessionId = webhookEvent.getSessionId();
        String currentState = webhookEvent.getCurrentState();
        List<String> cleanDtmfInputList = Arrays.asList(webhookEvent.getUserDtmfInputs().split(","));
        String event = ivrUtility.convertStateToEvent(currentState, cleanDtmfInputList.get(0));
        
        
        IVRWebHookResponseDto response = null;
        LOGGER.info("Session: "+ sessionId + "," + " current state:"+currentState+ ", Event: "+ event+", User input:"+cleanDtmfInputList);

        switch (event) {
        
		case SIGNON_EVENT:
			response = ivrService.processSignOn(sessionId, currentState, cleanDtmfInputList);
			break;
			
		case LFACS_EVENT:
			response = ivrService.processLFACS(sessionId, currentState, cleanDtmfInputList);
			break;
			
		case MLT_EVENT:
			response = ivrService.processMLT(sessionId, currentState, cleanDtmfInputList);
			break;
			
		case CUT_TO_NEW_FACILITIES:
			response = ivrService.processCNF(sessionId, currentState, cleanDtmfInputList);
			break;

		case ADMIN:
			response = ivrService.processAdministration(sessionId, currentState, cleanDtmfInputList);
			break;
			
		case IVRConstants.CHANGE_STATUS:
			response = ivrService.processChangeStatusOfCablePair(sessionId, currentState, cleanDtmfInputList);
			break;
			
		case CHANGE_SERVICE_TERMINAL:
			response = ivrService.processChangeorNewAssignServingTerminal(sessionId, currentState, cleanDtmfInputList);
			break;

		case IVRConstants.CONSTRUCTION_ACTIVITY:
			response = ivrService.processConstructionActivity(sessionId, currentState, cleanDtmfInputList);
			break;
			
		default:
			//
			break;
		}
        
    return response;
    }

}
