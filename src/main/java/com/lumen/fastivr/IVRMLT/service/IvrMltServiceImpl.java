package com.lumen.fastivr.IVRMLT.service;

import static com.lumen.fastivr.IVRMLT.utils.IVRMltUtilities.*;

import static com.lumen.fastivr.IVRUtils.IVRConstants.GPDOWN_ERR_MSG;
import static com.lumen.fastivr.IVRUtils.IVRConstants.INQUIRY_BY_TN;
import static com.lumen.fastivr.IVRUtils.IVRConstants.*;

import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_0;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_1;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_2;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_3;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_4;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_5;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_6;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_7;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_8;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.HOOK_RETURN_9;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumen.fastivr.IVRCacheManagement.IVRCacheService;
import com.lumen.fastivr.IVRDto.IVRParameter;
import com.lumen.fastivr.IVRDto.IVRWebHookResponseDto;
import com.lumen.fastivr.IVRLFACS.LfacsValidation;
import com.lumen.fastivr.IVRMLT.caching.IvrMltCacheService;
import com.lumen.fastivr.IVRMLT.entity.IvrMltSession;
import com.lumen.fastivr.IVRMLT.helper.IvrMltHelper;
import com.lumen.fastivr.IVRMLT.helper.IvrMltLoopCareServices;
import com.lumen.fastivr.IVRMLT.utils.IVRMltConstants;
import com.lumen.fastivr.IVRMLT.utils.MdataUtils;
import com.lumen.fastivr.IVRSessionManagement.IVRUserSession;
import com.lumen.fastivr.IVRUtils.IVRConstants;
import com.lumen.fastivr.IVRUtils.IVRHookReturnCodes;
import com.lumen.fastivr.IVRMLT.helper.MltPagerText;

import tollgrade.loopcare.testrequestapi.ACSIG;
import tollgrade.loopcare.testrequestapi.DCSIGCRAFT;
import tollgrade.loopcare.testrequestapi.Mdata;

@Service
public class IvrMltServiceImpl implements IvrMltService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(IvrMltServiceImpl.class);
	
	@Autowired
	private IvrMltCacheService mltCacheService;
	
	@Autowired
	private IvrMltHelper ivrMltHelper;
	
	@Autowired
	private IvrMltLoopCareServices mltLoopCareServices;
	
	@Autowired
	private IVRCacheService ivrCacheService;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private LfacsValidation tnValidation;
	
	@Autowired
	MltPagerText mltPagerText;
	
	
	//MLD021-checkValidTNandMLTTestable
	@Override
	public IVRWebHookResponseDto processMLD021(String sessionId, String currentState,List<String> userDTMFInput) {
		
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		
		String testType="";
		IvrMltSession mltSession =null;
		IVRUserSession session = ivrCacheService.getBySessionId(sessionId);
		
		if (mltCacheService.getBySessionId(sessionId) != null) {
			mltSession = mltCacheService.getBySessionId(sessionId);

		} else {
			mltSession = new IvrMltSession();
			mltSession.setSessionId(sessionId);
			mltCacheService.addSession(mltSession);
		}
		
		if(session!=null) {
		if(userDTMFInput.size()>0) {
			
			String testTypeSelection = userDTMFInput.get(0);
			
			if(testTypeSelection!=null) 
			{
				testType = ivrMltHelper.findTestCategory(testTypeSelection);
				mltSession.setTestType(testType);
				
				if(testTypeSelection.equals("1")) {
					
					String _7digitTN = userDTMFInput.get(1);
					response = tnValidation.validateFacsTN(_7digitTN, session);
					session.setFacsInqType(INQUIRY_BY_TN);
					mltSession.setInquiredTn(session.getNpaPrefix()+_7digitTN);
				}
				else if(testTypeSelection.equals("2") || testTypeSelection.equals("3")) 
				{
					String parentOverrideType = userDTMFInput.get(1);
					if(parentOverrideType!=null) {
						
						if(parentOverrideType.equals("1")){
							mltSession.setOverride("N");
						}
						else {
							
							String childOverrideType = userDTMFInput.get(3);
							if(childOverrideType!=null && !childOverrideType.isEmpty()) {
								String resultOverride=ivrMltHelper.getOverrideType(childOverrideType);
								mltSession.setOverride(resultOverride);
							}
						}
						
					}
					String _7digitTN = userDTMFInput.get(2);
					response = tnValidation.validateFacsTN(_7digitTN, session);
					session.setFacsInqType(INQUIRY_BY_TN);
					mltSession.setInquiredTn(session.getNpaPrefix()+_7digitTN);
					
					testType = ivrMltHelper.findTestCategory(userDTMFInput.get(0));
					mltSession.setTestType(testType);
					
				}
				
			}	
		  }
		}

		else {
			response.setHookReturnMessage(INVALID_SESSION_ID);
		}
		
		ivrMltHelper.resetTestFields(mltSession);
		mltCacheService.updateSession(mltSession);
		ivrCacheService.updateSession(session);
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		return response;
	}


	//MLD026 - check tech is workig from the same line
	@Override
	public IVRWebHookResponseDto checkTechWorkingFromSameLineOrNot(String sessionId, String currentState,
			String userDTMFInput) {

		String hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_1;
		String hookReturnMessage = IVRConstants.GPDOWN_ERR_MSG;
		IVRWebHookResponseDto ivrDto = new IVRWebHookResponseDto();
		ivrDto.setCurrentState(currentState);

		IvrMltSession mltSession = mltCacheService.getBySessionId(sessionId);
		if (mltSession != null) {
			// mbSameLine - set false default
			mltSession.setTechOnSameLine(Boolean.FALSE);
			hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_0;
			hookReturnMessage = "Tech not Working on same line";

			if (userDTMFInput.equals("3")) {
				// mbSameLine - set true here
				mltSession.setTechOnSameLine(Boolean.TRUE);
				hookReturnMessage = "Tech not Working on same line";
			}
		}

		mltCacheService.updateSession(mltSession);
		ivrDto.setHookReturnCode(hookReturnCode);
		ivrDto.setHookReturnMessage(hookReturnMessage);
		return ivrDto;
	}

	//MLD027 - issue the actual MLT test to be conducted on the copper
	@Override
	public IVRWebHookResponseDto issueMLTTest(String sessionId) throws JsonMappingException, JsonProcessingException {
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(IVRConstants.STATE_MLD027);
		String returnMessage = "";
		String hookReturnCode = "";
		
		hookReturnCode = mltLoopCareServices.prepareRequest(sessionId);
		returnMessage = getReturnMessageForMLD027(hookReturnCode); 
		
		 if(returnMessage.equalsIgnoreCase(GPDOWN_ERR_MSG) && !hookReturnCode.equalsIgnoreCase(HOOK_RETURN_4)) {
			 hookReturnCode = GPDOWN_ERR_MSG_CODE;
		 }
		response.setHookReturnCode(hookReturnCode);
		response.setHookReturnMessage(returnMessage);
		return response;
	}

	//MLD040 - retrieve the MLT results, this is called when Technician don't have mobile phone configured in NET
	//and result is delivered via Voice prompts 
	@Override
	public IVRWebHookResponseDto retriveMLTResult(String sessionId) {
		IvrMltSession mltSession = mltCacheService.getBySessionId(sessionId);
		String testType = mltSession.getTestType(); 
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(IVRConstants.STATE_MLD040);
		String returnMessage = "";
		String returnCode = "";
		String resp = mltLoopCareServices.retrieveMLTTestResults(sessionId);
		try {

			if (resp != null) {
				try {
					response = mltLoopCareServices.validateMltResult(sessionId);
					returnCode = response.getHookReturnCode();
				} catch (JsonProcessingException e) {
					returnCode = HOOK_RETURN_1;
					e.printStackTrace();
				}
			} else {
				returnCode = HOOK_RETURN_9;
			}

		} catch (Exception e) {
			LOGGER.error("Error while retrieving MLT result ",e);
			returnCode = HOOK_RETURN_1;
		}
		
		switch (testType) {
		case QUICKX_TEST:
		case LOOPX_TEST:
		case FULLX_TEST:
			returnMessage = getReturnMessageForMLD040(returnMessage, returnCode, testType);
			break;
		case TONE_PLUS_TEST:
			returnMessage = getReturnMessageForTonePlus(returnMessage, returnCode);
			break;
		}
		response.setHookReturnCode(returnCode);
		response.setHookReturnMessage(returnMessage);
		response.setCurrentState(IVRConstants.STATE_MLD040);
		return response;
	}

	private String getReturnMessageForTonePlus(String returnMessage, String returnCode) {
		// TODO Auto-generated method stub
		switch (returnCode) {
		case HOOK_RETURN_1:
			returnMessage = IVRConstants.GPDOWN_ERR_MSG;
		case HOOK_RETURN_2:
			returnMessage = "Timeout";
			break;
		case HOOK_RETURN_3:
			returnMessage = "NPA-NXX not in LMOS";
			break;
		case HOOK_RETURN_4:
			returnMessage = "MLT Error";
			break;
		case HOOK_RETURN_5:
			returnMessage = "rc NEQ 5/20";
			break;
		case HOOK_RETURN_6:
			returnMessage = "rc 5/20";
			break;
		case HOOK_RETURN_8: 
			returnMessage =  "Tone Initiated";
			break;
		case HOOK_RETURN_9:
			returnMessage  = "MLT Results are not available, Please try again";
			break;
		}
		return returnMessage;
	}


	private String getReturnMessageForMLD027(String returnCode) {
		String returnMessage = "";
		switch (returnCode) {
		case HOOK_RETURN_0:
			returnMessage = "Has Alpha Pager, Not Calling from Same Line";
			break;
		case HOOK_RETURN_1:
			returnMessage = "No Alpha Pager, Not Calling from Same Line";
			break;
		case HOOK_RETURN_2:
			returnMessage = "Has Alpha Pager, Calling from Same Line";
			break;
		case HOOK_RETURN_3:
			returnMessage = "No Alpha Pager, Calling from Same Line";
			break;
		case HOOK_RETURN_4:
			returnMessage = IVRConstants.GPDOWN_ERR_MSG;
			break;
		case HOOK_RETURN_5:
			returnMessage = "NPA-NXX not in LNO";
			break;
		default:
			returnMessage = IVRConstants.GPDOWN_ERR_MSG;
			break;
		}
		return returnMessage;
	}
	
	private String getReturnMessageForMLD040(String returnMessage, String returnCode, String testType) {
		switch (returnCode) {
		case HOOK_RETURN_1:
			returnMessage = IVRConstants.GPDOWN_ERR_MSG;
		case HOOK_RETURN_2:
			returnMessage = "MLT Error";
			break;
		case HOOK_RETURN_3:
			returnMessage = "NPA-NXX not in LMOS";
			break;
		case HOOK_RETURN_4:
			returnMessage = "Timeout";
			break;
		case HOOK_RETURN_5:
			returnMessage = "rc NEQ 5/20";
			break;
		case HOOK_RETURN_6:
			returnMessage = "rc 5/20";
			break;
		case HOOK_RETURN_7:
			returnMessage =  testType + " test";
			break;
		case HOOK_RETURN_8: 
			returnMessage =  testType + " test";
			break;
		case HOOK_RETURN_9:
			returnMessage  = "MLT Results are not available, Please try again";
			break;
		default:
			returnMessage = IVRConstants.GPDOWN_ERR_MSG;
			break;
		}
		return returnMessage;
	}

	//MLD075
	@Override
	public IVRWebHookResponseDto checkPlayVoltageInformation(String sessionId, String currentState) {
		LOGGER.info("Inside :: checkPlayVoltageInformation.");
		IVRWebHookResponseDto ivrDto = new IVRWebHookResponseDto();
		ivrDto.setHookReturnCode( IVRHookReturnCodes.HOOK_RETURN_0);
		ivrDto.setHookReturnMessage(IVRMltConstants.NO_VOLTAGE_INFORMATION_REQUIRED);
		ivrDto.setCurrentState(currentState);
		ivrDto.setSessionId(sessionId);
		IvrMltSession mltSession = mltCacheService.getBySessionId(sessionId);
		if (mltSession != null) {
			try {
				Mdata mData = objectMapper.readValue(mltSession.getMltTestResult(), Mdata.class);
				if (mData!=null && mData.getTestRsp() != null 
						&& mData.getTestRsp().getF()!= null && StringUtils.isNotBlank(mData.getTestRsp().getF().getTestCode()) 
						&& (!ivrMltHelper.voltageInformationRequired(mData.getTestRsp().getF().getTestCode()))) {
					
					ivrDto.setHookReturnCode( IVRHookReturnCodes.HOOK_RETURN_1);
					ivrDto.setHookReturnMessage(IVRMltConstants.VOLTAGE_INFORMATION_REQUIRED);
					
				}
			} catch (JsonProcessingException e) {
				LOGGER.error("MLT Test data not found.");
				ivrDto.setHookReturnMessage("No MLT Test data found.");
			}
		}

		mltCacheService.updateSession(mltSession);
		LOGGER.info("END :: checkPlayVoltageInformation.");
		return ivrDto;
	}

	@Override
	public int checkDcCraftSignatures() {
		// TODO Auto-generated method stub
		return 0;
	}

	public IVRWebHookResponseDto playTipRingDcOhm(String sessionId, String currentState) throws JsonMappingException, JsonProcessingException {
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		
		String hookReturnMessage = IVRMltConstants.NOT_PLAY_DC_CRAFT_RES;
		String hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_2;
		String dcResTr=IVRMltConstants.UNSPECIFIED_VOICE_MSG_NUMBER;
		
		IvrMltSession mltSession = mltCacheService.getBySessionId(sessionId);
		if (mltSession != null) {
		try {
			Mdata mData = objectMapper.readValue(mltSession.getMltTestResult(), Mdata.class);
			DCSIGCRAFT dcSigCraft = MdataUtils.getDcCraft(mData);
			if(dcSigCraft!=null && StringUtils.isNotBlank(dcSigCraft.getDcResTr())) 
			{
				dcResTr=dcSigCraft.getDcResTr();
				hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_1;
				hookReturnMessage= IVRMltConstants.PLAY_DC_CRAFT_RES;
			}
			
			List<IVRParameter> ivrParameterList = new ArrayList<IVRParameter>();
			IVRParameter ivrParameter = new IVRParameter();
			ivrParameter.setData(dcResTr);
			ivrParameterList.add(ivrParameter);
			response.setParameters(ivrParameterList);
			
		}
		catch (JsonProcessingException e) {
			hookReturnMessage="No MLT Test data found.";
		}
		}
		
		response.setHookReturnCode(hookReturnCode);
		response.setHookReturnMessage(hookReturnMessage);
		return response;
	}
	@Override
	public IVRWebHookResponseDto playTipGroundDcOhm(String sessionId, String currentState) throws JsonMappingException, JsonProcessingException {
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		
		String hookReturnMessage = IVRMltConstants.NOT_PLAY_DC_CRAFT_RES;
		String hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_2;
		String dcResTg=IVRMltConstants.UNSPECIFIED_VOICE_MSG_NUMBER;
		
		IvrMltSession mltSession = mltCacheService.getBySessionId(sessionId);
		if (mltSession != null) {
		try {
			Mdata mData = objectMapper.readValue(mltSession.getMltTestResult(), Mdata.class);
			
			DCSIGCRAFT dcSigCraft = MdataUtils.getDcCraft(mData);
			if(dcSigCraft!=null && StringUtils.isNotBlank(dcSigCraft.getDcResTg())) 
			{
				dcResTg=dcSigCraft.getDcResTg();
				
				hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_1;
				hookReturnMessage= IVRMltConstants.PLAY_DC_CRAFT_RES;
			}
			
			List<IVRParameter> ivrParameterList = new ArrayList<IVRParameter>();
			IVRParameter ivrParameter = new IVRParameter();
			ivrParameter.setData(dcResTg);
			ivrParameterList.add(ivrParameter);
			response.setParameters(ivrParameterList);
			
		}
		catch (JsonProcessingException e) {
			hookReturnMessage="No MLT Test data found.";
		}
		}
		response.setHookReturnCode(hookReturnCode);
		response.setHookReturnMessage(hookReturnMessage);
		return response;
	}
	@Override
	public IVRWebHookResponseDto playRingGroundDcOhm(String sessionId, String currentState) {
		LOGGER.info("Inside :: playRingGroundDcOhm.");
		IVRWebHookResponseDto ivrDto = new IVRWebHookResponseDto();
		ivrDto.setHookReturnCode(IVRHookReturnCodes.HOOK_RETURN_2);
		ivrDto.setHookReturnMessage(IVRMltConstants.NO_NEED_TO_PLAY_RESISTANCE);
		ivrDto.setCurrentState(currentState);
		ivrDto.setSessionId(sessionId);
		IvrMltSession mltSession = mltCacheService.getBySessionId(sessionId);
		if (mltSession != null) {

			try {
				String dcResRg = IVRMltConstants.UNSPECIFIED_VOICE_MSG_NUMBER;
				List<IVRParameter> parameterList = new ArrayList<>();
				Mdata mData = objectMapper.readValue(mltSession.getMltTestResult(), Mdata.class);

				DCSIGCRAFT dcSigCraft = MdataUtils.getDcCraft(mData);
				if(dcSigCraft!=null && StringUtils.isNotBlank(dcSigCraft.getDcResRg())) 
				{
					dcResRg = dcSigCraft.getDcResRg();
					ivrDto.setHookReturnCode(IVRHookReturnCodes.HOOK_RETURN_1);
					ivrDto.setHookReturnMessage(IVRMltConstants.NEED_TO_PLAY_RESISTANCE);

				}

				IVRParameter param = new IVRParameter();
				param.setData(dcResRg);
				parameterList.add(param);
				ivrDto.setParameters(parameterList);

			} catch (JsonProcessingException e) {
				LOGGER.error("MLT Test data not found.");
				ivrDto.setHookReturnMessage("No MLT Test data found.");
			}
		}

		
		mltCacheService.updateSession(mltSession);
		LOGGER.info("END :: playRingGroundDcOhm.");
		return ivrDto;
	}

	//MLD086
	@Override
	public IVRWebHookResponseDto  playTipGroundVolts(String sessionId, String currentState) {
		LOGGER.info("Inside :: playTipGroundVolts.");
		IVRWebHookResponseDto ivrDto = new IVRWebHookResponseDto();
		ivrDto.setHookReturnCode(IVRHookReturnCodes.HOOK_RETURN_2);
		ivrDto.setHookReturnMessage(IVRMltConstants.NO_TIP_GROUND_VOLTS);
		ivrDto.setCurrentState(currentState);
		ivrDto.setSessionId(sessionId);

		IvrMltSession mltSession = mltCacheService.getBySessionId(sessionId);
		if (mltSession != null) {

			try {
				String dcResRg = IVRMltConstants.UNSPECIFIED_VOICE_MSG_NUMBER;
				List<IVRParameter> parameterList = new ArrayList<>();
				Mdata mData = objectMapper.readValue(mltSession.getMltTestResult(), Mdata.class);

				DCSIGCRAFT dcSigCraft = MdataUtils.getDcCraft(mData);
				if(dcSigCraft!=null && StringUtils.isNotBlank(dcSigCraft.getDcVoltTg())) 
				{
					dcResRg = dcSigCraft.getDcVoltTg();
					ivrDto.setHookReturnCode(IVRHookReturnCodes.HOOK_RETURN_1);
					ivrDto.setHookReturnMessage(IVRMltConstants.TIP_GROUND_VOLTS);

				}

				IVRParameter param = new IVRParameter();
				param.setData(dcResRg);
				parameterList.add(param);
				ivrDto.setParameters(parameterList);

			} catch (JsonProcessingException e) {
				LOGGER.error("MLT Test data not found.");
				ivrDto.setHookReturnMessage("No MLT Test data found.");
			}
		}

		mltCacheService.updateSession(mltSession);
		LOGGER.info("END :: playTipGroundVolts.");
		return ivrDto;
	}

	//MLD088
		@Override
		public IVRWebHookResponseDto playRingGroundVolts(String sessionId, String currentState) throws JsonMappingException, JsonProcessingException {
			IVRWebHookResponseDto response = new IVRWebHookResponseDto();
			response.setSessionId(sessionId);
			response.setCurrentState(currentState);
			
			String hookReturnMessage = IVRMltConstants.NO_NEED_TO_PLAY_RESISTANCE;
			String hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_2;
			String dcRes=IVRMltConstants.UNSPECIFIED_VOICE_MSG_NUMBER;
			
			IvrMltSession mltSession = mltCacheService.getBySessionId(sessionId);
			if (mltSession != null) {
			try {
				Mdata mData = objectMapper.readValue(mltSession.getMltTestResult(), Mdata.class);
				
				if(mData != null && mData.getTestRsp() != null && !mData.getTestRsp().getD().getItem().isEmpty()
						&& mData.getTestRsp().getD().getItem().get(0).getPotsResults1() != null
						&& mData.getTestRsp().getD().getItem().get(0).getPotsResults1().getDcCraft() != null
						&& StringUtils.isNotBlank(mData.getTestRsp().getD().getItem().get(0).getPotsResults1()
								.getDcCraft().getDcVoltRg())) 
				{
					dcRes=mData.getTestRsp().getD().getItem().get(0).getPotsResults1().getDcCraft().getDcVoltRg();
					hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_1;
					hookReturnMessage= IVRMltConstants.NEED_TO_PLAY_RESISTANCE;
				}
				
				List<IVRParameter> ivrParameterList = new ArrayList<IVRParameter>();
				IVRParameter ivrParameter = new IVRParameter();
				ivrParameter.setData(dcRes);
				ivrParameterList.add(ivrParameter);
				response.setParameters(ivrParameterList);
				
			}
			catch (JsonProcessingException e) {
				hookReturnMessage="No MLT Test data found.";
			}
			}
			
			response.setHookReturnCode(hookReturnCode);
			response.setHookReturnMessage(hookReturnMessage);
			return response;
		}

	@Override
	public int checkAcCraftSignatures() {
		// TODO Auto-generated method stub
		return 0;
	}

	//MLD090
	@Override
	public IVRWebHookResponseDto playTipRingAcOhm(String sessionId, String currentState) {
		LOGGER.info("Inside :: playTipRingAcOhm.");
		IVRWebHookResponseDto ivrDto = new IVRWebHookResponseDto();
		ivrDto.setHookReturnCode(IVRHookReturnCodes.HOOK_RETURN_2);
		ivrDto.setHookReturnMessage(IVRMltConstants.NO_TIP_RING_AC_OHMS);
		ivrDto.setCurrentState(currentState);
		ivrDto.setSessionId(sessionId);

		IvrMltSession mltSession = mltCacheService.getBySessionId(sessionId);
		if (mltSession != null) {

			try {
				String acResTr = IVRMltConstants.UNSPECIFIED_VOICE_MSG_NUMBER;
				List<IVRParameter> parameterList = new ArrayList<>();
				Mdata mData = objectMapper.readValue(mltSession.getMltTestResult(), Mdata.class);

				ACSIG acSig = MdataUtils.getAcSig(mData);
				if(acSig!=null && StringUtils.isNotBlank(acSig.getAcResTr())) 
				{
					acResTr = acSig.getAcResTr();
					ivrDto.setHookReturnCode(IVRHookReturnCodes.HOOK_RETURN_1);
					ivrDto.setHookReturnMessage(IVRMltConstants.TIP_RING_AC_OHMS);

				}

				IVRParameter param = new IVRParameter();
				param.setData(acResTr);
				parameterList.add(param);
				ivrDto.setParameters(parameterList);

			} catch (JsonProcessingException e) {
				LOGGER.error("MLT Test data not found.");
				ivrDto.setHookReturnMessage("No MLT Test data found.");
			}
		}

		mltCacheService.updateSession(mltSession);
		LOGGER.info("END :: playTipRingAcOhm.");
		return ivrDto;
	}

	// MLD092
	@Override
	public IVRWebHookResponseDto playTipGroundAcOhm(String sessionId, String currentState) {
		LOGGER.info("Inside :: playTipGroundAcOhm.");
		IVRWebHookResponseDto ivrDto = new IVRWebHookResponseDto();
		ivrDto.setHookReturnCode(IVRHookReturnCodes.HOOK_RETURN_2);
		ivrDto.setHookReturnMessage(IVRMltConstants.NO_TIP_GROUND_AC_OHMS);
		ivrDto.setCurrentState(currentState);
		ivrDto.setSessionId(sessionId);
		
		IvrMltSession mltSession = mltCacheService.getBySessionId(sessionId);
		if (mltSession != null) {

			try {
				String acResTg = IVRMltConstants.UNSPECIFIED_VOICE_MSG_NUMBER;
				List<IVRParameter> parameterList = new ArrayList<>();
				Mdata mData = objectMapper.readValue(mltSession.getMltTestResult(), Mdata.class);

				ACSIG acSig = MdataUtils.getAcSig(mData);
				if (acSig != null && StringUtils.isNotBlank(acSig.getAcResTg())) {
					acResTg = acSig.getAcResTg();
					ivrDto.setHookReturnCode(IVRHookReturnCodes.HOOK_RETURN_1);
					ivrDto.setHookReturnMessage(IVRMltConstants.TIP_GROUND_AC_OHMS);

				}

				IVRParameter param = new IVRParameter();
				param.setData(acResTg);
				parameterList.add(param);
				ivrDto.setParameters(parameterList);
				

			} catch (JsonProcessingException e) {
				LOGGER.error("MLT Test data not found.");
				ivrDto.setHookReturnMessage("No MLT Test data found.");
			}
		}

		mltCacheService.updateSession(mltSession);
		LOGGER.info("END :: playTipGroundAcOhm.");
		return ivrDto;
	}

	//MLD094
		@Override
		public IVRWebHookResponseDto playRingGroundAcOhm(String sessionId, String currentState) throws JsonMappingException, JsonProcessingException
		 {
			IVRWebHookResponseDto response = new IVRWebHookResponseDto();
			response.setSessionId(sessionId);
			response.setCurrentState(currentState);
			
			String hookReturnMessage = IVRMltConstants.NO_NEED_TO_PLAY_RESISTANCE;
			String hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_2;
			String dcRes=IVRMltConstants.UNSPECIFIED_VOICE_MSG_NUMBER;
			
			IvrMltSession mltSession = mltCacheService.getBySessionId(sessionId);
			if (mltSession != null) {
			try {
				Mdata mData = objectMapper.readValue(mltSession.getMltTestResult(), Mdata.class);
				
				if(mData != null && mData.getTestRsp() != null && !mData.getTestRsp().getD().getItem().isEmpty()
						&& mData.getTestRsp().getD().getItem().get(0).getPotsResults1() != null
						&& mData.getTestRsp().getD().getItem().get(0).getPotsResults1().getAc() != null
						&& StringUtils.isNotBlank(mData.getTestRsp().getD().getItem().get(0).getPotsResults1()
					    .getAc().getAcResRg())) 
								
				{
					dcRes=mData.getTestRsp().getD().getItem().get(0).getPotsResults1().getAc().getAcResRg();
					hookReturnCode = IVRHookReturnCodes.HOOK_RETURN_1;
					hookReturnMessage= IVRMltConstants.NEED_TO_PLAY_RESISTANCE;
				}
				
				List<IVRParameter> ivrParameterList = new ArrayList<IVRParameter>();
				IVRParameter ivrParameter = new IVRParameter();
				ivrParameter.setData(dcRes);
				ivrParameterList.add(ivrParameter);
				response.setParameters(ivrParameterList);
				
			}
			catch (JsonProcessingException e) {
				hookReturnMessage="No MLT Test data found.";
			}
			}
			
			response.setHookReturnCode(hookReturnCode);
			response.setHookReturnMessage(hookReturnMessage);
			return response;
		}


	@Override
	public int playTestVerCode(String value) {
		// TODO Auto-generated method stub
		return 0;
	}

	
	//////////////////////////////////////////////////////////////////////////////////////
	//Name:				validateFacsTnTone
	//Input Parameters:	DTMF input
	//Global Data		:	None														
	//Processing		:	Sets the telephone number working on. Validates if we have 
	//the interfacing sys info for the tn	
	//Precondition	:	Must have 7 digit TN in OF7
	//Output			:	Returns the hook return code.		
	//Instructions	:	Call this function to validate the TN
	//Current State	:	MLD300, MLD500
	//Next State		:	ML0301(0), MLE302(1), ML0300(-1)
	//					: MLE502 (0), ML0501(1), ML0500(-1)
	//////////////////////////////////////////////////////////////////////////////////////
	@Override
	public IVRWebHookResponseDto validateFacsTnTone(String sessionId, String currentState, List<String> userInputs) {
		String hookReturnMessage = "";
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		IVRUserSession userSession = ivrCacheService.getBySessionId(sessionId);
		IvrMltSession mltSession = null;
		
		if (mltCacheService.getBySessionId(sessionId) != null) {
			mltSession = mltCacheService.getBySessionId(sessionId);

		} else {
			mltSession = new IvrMltSession();
			mltSession.setSessionId(sessionId);
			mltCacheService.addSession(mltSession);
		}
		
		String testTypeInd = userInputs.get(0);
		String testType = "";
		String _7digitTn = userInputs.get(1);
		
		switch (testTypeInd) {
		case "4":
			testType = TONE_PLUS_TEST;
			break;
		case "5":
			testType = TONE_REMOVAL_TEST;
			break;
		}
		
		response = tnValidation.validateFacsTN(_7digitTn, userSession);
		
		userSession.setFacsInqType(INQUIRY_BY_TN);
		mltSession.setInquiredTn(userSession.getNpaPrefix()+_7digitTn);

		switch (response.getHookReturnCode()) {
		case HOOK_RETURN_1:
			hookReturnMessage = "Valid TN";
			break;
		case HOOK_RETURN_0:
			hookReturnMessage = "TN not found in Table";
			break;
		case "-1":
			hookReturnMessage = "Invalid digits";
			break;

		}
		
		ivrMltHelper.resetTestFields(mltSession);
		response.setHookReturnCode(response.getHookReturnCode());
		response.setHookReturnMessage(hookReturnMessage);
		response.setSessionId(sessionId);
		response.setCurrentState(currentState);
		mltSession.setTestType(testType);
		mltCacheService.updateSession(mltSession);
		ivrCacheService.updateSession(userSession);
		return response;
	}

	
	//////////////////////////////////////////////////////////////////////////////////////
	//Name:				MLD307
	//Input Parameters:	UINT
	//Global Data		:	None														
	//Processing		:	Validate the tone duration
	//Output			:	Returns the hook return code.		
	//Current State		:	MLD307
	//Next State		:	ML0307(0)-Valid, MLE308(1)-Invalid
	//////////////////////////////////////////////////////////////////////////////////////
	@Override
	public IVRWebHookResponseDto addToneDuration_MLD307(String sessionId, String userInput) {
		IvrMltSession mltSession = mltCacheService.getBySessionId(sessionId);
		String hookReturnCode = HOOK_RETURN_1;
		String hookReturnMessage = "Invalid Tone Duration";
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		
		try {
			int duration = Integer.parseInt(userInput);
			if (duration > 0 && duration < 50) {
				hookReturnCode = HOOK_RETURN_0;
				hookReturnMessage = "Valid Tone Duration";
				mltSession.setToneDuration(duration);
				List<IVRParameter> data = ivrMltHelper.addParamterData(String.valueOf(duration));
				response.setParameters(data);
			}
			
		} catch(NumberFormatException e) {
			LOGGER.error("Tone Duration should be a number between 0 and 50");
		}
		
		response.setSessionId(sessionId);
		response.setCurrentState(IVRConstants.STATE_MLD307);
		response.setHookReturnCode(hookReturnCode);
		response.setHookReturnMessage(hookReturnMessage);
		mltCacheService.updateSession(mltSession);
		return response;
	}

	////////////////////////////////////////////////////////////////////////////////////
	//Name:				MLD310
	//Input Parameters:	SessionId
	//Processing		:Issue Tone+ request
	//Current State	:	MLD310
	//Next State		:	ML0340
	////////////////////////////////////////////////////////////////////////////////////
	@Override
	public IVRWebHookResponseDto issueTonePlusRequest(String sessionId, String userInput) throws JsonMappingException, JsonProcessingException {
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		IvrMltSession mltSession = mltCacheService.getBySessionId(sessionId);
		String returnCode = "";
		String returnMessage = "";
		boolean mbSameLine = false;
		
		if(userInput.equalsIgnoreCase("3")) {
			mbSameLine = true;
		} else {
			mbSameLine	=	false;	// by default it is false, but in case somebody forgets to reset
		}
		
		mltSession.setTechOnSameLine(mbSameLine);
		mltCacheService.updateSession(mltSession);
		returnCode = mltLoopCareServices.prepareRequest(sessionId);
		returnMessage = getReturnMessageForTonePlusRequest(returnCode);
		
		 if(returnMessage.equalsIgnoreCase(GPDOWN_ERR_MSG) && !returnCode.equalsIgnoreCase(HOOK_RETURN_4)) {
			 returnCode = GPDOWN_ERR_MSG_CODE;
		 }
		
		response.setSessionId(sessionId);
		response.setCurrentState(IVRConstants.STATE_MLD310);
		response.setHookReturnCode(returnCode);
		response.setHookReturnMessage(returnMessage);
		
		mltCacheService.updateSession(mltSession); //since this will happen after some time
		return response;
	}
	

	private String getReturnMessageForTonePlusRequest(String returnCode) {
		String returnMessage = GPDOWN_ERR_MSG;
		
		switch (returnCode) {
		case HOOK_RETURN_0:
			returnMessage = "Has Alpha Pager, Not Calling from Same Line";
			break;
		case HOOK_RETURN_1:
			returnMessage = "No Alpha Pager, Not Calling from Same Line";
			break;
		case HOOK_RETURN_2:
			returnMessage = "Has Alpha Pager, Calling from Same Line";
			break;
		case HOOK_RETURN_3:
			returnMessage = "No Alpha Pager, Calling from Same Line";
			break;
		case HOOK_RETURN_4:
			returnMessage = GPDOWN_ERR_MSG;
			break;
		case HOOK_RETURN_5:
			returnMessage = "NPA-NXX not in LNO";
			break;
		default:
			returnMessage = GPDOWN_ERR_MSG;
			break;
		}
		return returnMessage;
	}


	////////////////////////////////////////////////////////////////////////////////////
	//Name:				MLD510
	//Input Parameters:	sessionId
	//Processing		:	Issue X request
	//Current State	:	MLD510
	//Next State		:	ML0530(0) - Success, MLD510(1) - Fas interface down
	////////////////////////////////////////////////////////////////////////////////////
	@Override
	public IVRWebHookResponseDto issueXRequest(String sessionId) throws JsonMappingException, JsonProcessingException {
		IVRWebHookResponseDto response = new IVRWebHookResponseDto();
		String returnCode = mltLoopCareServices.prepareRequest(sessionId);
		
		String returnMessage = GPDOWN_ERR_MSG; //default msg 
		switch(returnCode) {
		case HOOK_RETURN_1:
			returnMessage = "Success";
			returnCode = HOOK_RETURN_0;
			break;
		case HOOK_RETURN_5:
			returnMessage = "NPA-NXX not in LNO";
			break;
		default:
			returnMessage = GPDOWN_ERR_MSG;
			returnCode = HOOK_RETURN_1;
		}		
		
		response.setHookReturnCode(returnCode);
		response.setHookReturnMessage(returnMessage);
		response.setSessionId(sessionId);
		response.setCurrentState(STATE_MLD510);
		
		return response;
	}

}
