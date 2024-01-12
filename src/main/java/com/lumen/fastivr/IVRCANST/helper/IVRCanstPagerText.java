package com.lumen.fastivr.IVRCANST.helper;

import java.util.List;

import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumen.fastivr.IVRCANST.Dto.ChangeLoopAssignmentResponse;
import com.lumen.fastivr.IVRCANST.entity.IVRCanstEntity;
import com.lumen.fastivr.IVRCANST.repository.IVRCanstCacheService;
import com.lumen.fastivr.IVRCNF.entity.IVRCnfEntity;
import com.lumen.fastivr.IVRCNF.utils.IVRCnfUtilities;
import com.lumen.fastivr.IVRCacheManagement.IVRCacheService;
import com.lumen.fastivr.IVRDto.CurrentAssignmentResponseDto;
import com.lumen.fastivr.IVRDto.LOOP;
import com.lumen.fastivr.IVRDto.SEG;
import com.lumen.fastivr.IVRSessionManagement.IVRUserSession;

@Service
public class IVRCanstPagerText {

	@Autowired
	private IVRCanstCacheService ivrCanstCacheService;	
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private IVRCacheService cacheService;
	
	@Autowired
	private IVRCnfUtilities ivrCnfUtilities;
	
	@Autowired
	IVRCanstHelper ivrCanstHelper;
	
	
	public String getChangeNewSerTermPagerText(String sessionId) throws JsonMappingException, JsonProcessingException {
		
		StringBuilder pageText = new StringBuilder();
		CurrentAssignmentResponseDto currentAssignmentResponse =null;
		int segIndex = 0;
		
		IVRUserSession ivrUserSession = cacheService.getBySessionId(sessionId);
		if(ivrUserSession!=null && ivrUserSession.getCurrentAssignmentResponse()!=null) {
			currentAssignmentResponse = objectMapper.readValue(ivrUserSession.getCurrentAssignmentResponse(), CurrentAssignmentResponseDto.class);
		}
		
		IVRCanstEntity canstSession = ivrCanstCacheService.getBySessionId(sessionId);
		
//		if(canstSession!=null && canstSession.getChangeServTermResp()!=null) {
//			changeLoopAssignmentResponse = objectMapper.readValue(canstSession.getChangeServTermResp(),ChangeLoopAssignmentResponse.class);
//		}
//		
		
		List<SEG>segList=ivrCnfUtilities.getSegmentList(currentAssignmentResponse);

		for (int i = 0; i < segList.size(); i++) {
			if (segList != null && !segList.isEmpty() && (StringUtils.isNotBlank(segList.get(i).getPR())) 
					&& (StringUtils.isNotBlank(segList.get(i).getCA())) ) {	

				if ((canstSession.getCable() == segList.get(i).getCA()) &&  (canstSession.getPair() == segList.get(i).getPR())) {
					segIndex = i;
					break;
				}
			}
		}
		
		List<LOOP> loopList = currentAssignmentResponse.getReturnDataSet().getLoop();
		String serviceOrderNumber = ivrCnfUtilities.getServiceOrder(loopList);
		String status = currentAssignmentResponse.getReturnDataSet().getLoop().get(0).getSTAT();
		String replacementBindingPostColorCode = "";
		String replacementTerminalAddress = "";
	
		if (status.equalsIgnoreCase("PCF")) {

			if( (segList.get(segIndex).getBP() != null)  && (StringUtils.isNotBlank(segList.get(segIndex).getBP())) ) {
				replacementBindingPostColorCode = segList.get(segIndex).getBP();
			}

			if( (segList.get(segIndex).getTEA() != null)  && (StringUtils.isNotBlank(segList.get(segIndex).getTEA())) ) {
				replacementTerminalAddress = segList.get(segIndex).getTEA();
			}

		}
		if((status.equalsIgnoreCase("CF")) || (status.equalsIgnoreCase("CT"))) {

			if ((segList.get(segIndex).getBP() != null) && (!segList.get(segIndex).getBP().equals(""))) {
				replacementBindingPostColorCode = segList.get(segIndex).getBP();
			}

			if ((segList.get(segIndex).getTEA() != null) && (!segList.get(segIndex).getTEA().equals(""))) {
				replacementTerminalAddress = segList.get(segIndex).getTEA();
			}

		}
		
		String stat = ivrCnfUtilities.getCablePairStatus(currentAssignmentResponse);
		String bpType = ivrCnfUtilities.getBpType(segIndex+1, currentAssignmentResponse);
		String serviceAddress = ivrCnfUtilities.getServiceAddress(currentAssignmentResponse);
		String reqCircuit = (loopList.get(0).getCKID() != null) ? (loopList.get(0).getCKID()) : (loopList.get(0).getSO().get(0).getCKID());
		
		if(reqCircuit.length()>=12) {
			pageText.append(reqCircuit);
		}
		else {
			 pageText.append("NONE");
			}
		
		pageText.append(" CUT");

		if(serviceOrderNumber.length()> 0) {
			pageText.append("|").append(serviceOrderNumber.trim());
		}

		pageText.append("|f").append(Integer.toString(ivrCanstHelper.getSegmentNumber(canstSession) + 1))
		.append(": ").append(segList.get(ivrCanstHelper.getSegmentNumber(canstSession)).getCA().trim()).append("/")
		.append(segList.get(ivrCanstHelper.getSegmentNumber(canstSession)).getPR().trim())
		.append("|to: ")
		.append(canstSession.getCable().trim()).append("/").append(canstSession.getPair().trim()).append(" ");

		pageText.append(stat);

		pageText.append("|LFACS is updated.");

		if (stat.equals("CF") || stat.equals("CT") || stat.equals("PCF")) {

			pageText.append("|Break ").append(stat);

			if(bpType.equals("BP") ) {

				pageText.append(" BP");
			}

			if(replacementBindingPostColorCode.length() > 0) {

				pageText.append(" ").append(replacementBindingPostColorCode.trim());
			}

			pageText.append(" at tea: ").append(replacementTerminalAddress.trim());
			pageText.append("|addr: ").append(" ").append(serviceAddress);
		}
		
		return pageText.toString();
	}
	
	
	

}
