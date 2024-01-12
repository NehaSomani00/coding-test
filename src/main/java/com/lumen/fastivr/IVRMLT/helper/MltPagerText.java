package com.lumen.fastivr.IVRMLT.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumen.fastivr.IVRMLT.caching.IvrMltCacheService;
import com.lumen.fastivr.IVRMLT.entity.IvrMltSession;
import com.lumen.fastivr.IVRMLT.utils.IVRMltUtilities;
import com.lumen.fastivr.IVRMLT.utils.MdataUtils;

import tollgrade.loopcare.testrequestapi.ACSIG;
import tollgrade.loopcare.testrequestapi.DCSIGCRAFT;
import tollgrade.loopcare.testrequestapi.FAULTINFO1;
import tollgrade.loopcare.testrequestapi.MLTTESTRSPCIDFMTP;
import tollgrade.loopcare.testrequestapi.Mdata;
import tollgrade.loopcare.testrequestapi.POTSRESULTS1;
import tollgrade.loopcare.testrequestapi.POTSRESULTS2;

@Service
public class MltPagerText {
	
	@Autowired
	private IvrMltCacheService mltCacheService;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	
	public String getPagerText(String testType,String sessionId) throws JsonMappingException, JsonProcessingException
	{
		String pageText ="";
		
		switch(testType) {
		
		case IVRMltUtilities.FULLX_TEST:
			pageText=getFullexPagerText(sessionId);
			break;
			
		case IVRMltUtilities.LOOPX_TEST:
			pageText=getFullexPagerText(sessionId);
			break;
			
		case IVRMltUtilities.QUICKX_TEST:
			pageText=getQuickexPagerText(sessionId);
			break;
			
		case IVRMltUtilities.TONE_PLUS_TEST:
			pageText=getAddTonePagerText(sessionId);
			break;
			
		case IVRMltUtilities.TONE_REMOVAL_TEST:
			pageText=getRemoveTonePagerText(sessionId);
			break;
		}
		
		return pageText;
	}
	
	
	/////for Loopex and Fullex////////
	public String getFullexPagerText(String sessionId) throws JsonMappingException, JsonProcessingException {
		
		StringBuilder pageText = new StringBuilder();
		IvrMltSession mltSession = mltCacheService.getBySessionId(sessionId);
	
		if (mltSession != null && StringUtils.isNotBlank(mltSession.getMltTestResult())) 
		{
			Mdata mData = objectMapper.readValue(mltSession.getMltTestResult(), Mdata.class);
			if(mData!=null) 
			{
				pageText.append(getTnText(mData));
				pageText.append(getAC_DCPagerText(mData));
				POTSRESULTS1 pResults1=MdataUtils.getPOTSRESULTS1(mData);
				//POTSRESULTS2 pResults2=MdataUtils.getPOTSRESULTS2(mData);
				
				///////////////Ringer//////////////////////
				if(pResults1!=null && pResults1.getRinger()!=null && pResults1.getRinger().getRingers()!=null)
				{
					pageText.append("|YES");
				}
			
				if (mData.getTestRsp()!=null && mData.getTestRsp().getF()!=null && 
						StringUtils.isNotBlank(mData.getTestRsp().getF().getTestCodeDescription())) 
				{
					pageText.append(getTestDescriptionText(mData.getTestRsp().getF().getTestCodeDescription()));
				}
			
			
				if (pResults1!=null && pResults1.getDistance()!=null && 
						pResults1.getDistance().getNotOpen()!=null && 
						StringUtils.isNotBlank(pResults1.getDistance().getNotOpen().getLoopFeet())) 
				{
					pageText.append("|LENGTH:");
					pageText.append(pResults1.getDistance().getNotOpen().getLoopFeet());
					pageText.append(".0FT");
				}
			}
		}
		
		return pageText.toString();
		
	}

	
	public String getQuickexPagerText(String sessionId) throws JsonMappingException, JsonProcessingException {
		
		StringBuilder pageText = new StringBuilder();
		
		IvrMltSession mltSession = mltCacheService.getBySessionId(sessionId);
		if (mltSession != null && StringUtils.isNotBlank(mltSession.getMltTestResult())) {
			Mdata mData = objectMapper.readValue(mltSession.getMltTestResult(), Mdata.class);
		
			if(mData!=null) {
				
				pageText.append(getTnText(mData));
				pageText.append(getAC_DCPagerText(mData));
				
				POTSRESULTS1 pResults1=MdataUtils.getPOTSRESULTS1(mData);
				//POTSRESULTS2 pResults2=MdataUtils.getPOTSRESULTS2(mData);
				
				if (mData.getTestRsp()!=null && mData.getTestRsp().getF()!=null && 
					StringUtils.isNotBlank(mData.getTestRsp().getF().getTestCodeDescription())) 
				{
				    
					pageText.append(getTestDescriptionText(mData.getTestRsp().getF().getTestCodeDescription()));
				}
			
				/////////////////capacitive balance/////////////////////////
				if (pResults1!=null && StringUtils.isNotBlank(pResults1.getBalanceC()))	
				{
					pageText.append("|CAP BAL");
					pageText.append(mData.getTestRsp().getD().getItem().get(0).getPotsResults1().getBalanceC());
					pageText.append(" %");
				}
			
				/////////length//////////////
				if (pResults1!=null && pResults1.getDistance()!=null && 
						pResults1.getDistance().getNotOpen()!=null && 
						StringUtils.isNotBlank(pResults1.getDistance().getNotOpen().getLoopFeet())) 
				{
					pageText.append("|LENGTH:");
					pageText.append(pResults1.getDistance().getNotOpen().getLoopFeet());
					pageText.append(".0FT");
				}
			
			}
		}
		
		return pageText.toString();
		
	}
	
	public String getAddTonePagerText(String sessionId) throws JsonMappingException, JsonProcessingException {
		StringBuilder pageText = new StringBuilder();

		IvrMltSession mltSession = mltCacheService.getBySessionId(sessionId);
		
		if (mltSession != null && mltSession.getMltTestResult()!=null) {
			Mdata mData = objectMapper.readValue(mltSession.getMltTestResult(), Mdata.class);	
		
			String primaryNpa = mData.getTestRsp().getKey().getCid().getFmtP().getNpa();
			String primaryNxx = mData.getTestRsp().getKey().getCid().getFmtP().getNnx();	
			String lineNo=mData.getTestRsp().getKey().getCid().getFmtP().getLine();
			
			if(StringUtils.isNotBlank(primaryNpa) && StringUtils.isNotBlank(primaryNxx) && StringUtils.isNotBlank(lineNo))
			{
				if(mData.getTestRsp()!=null && mData.getTestRsp().getFinalFlag()==89) {/// check for message status='S'
				
					pageText.append(" ").append(primaryNpa).append("-").append(primaryNxx).append("-").append(lineNo).append(" ");
					pageText.append("|TONE ADDED");
				
				}
				else {
					pageText.append(" ").append(primaryNpa).append("-").append(primaryNxx).append("-").append(lineNo).append(" ");
					pageText.append("|ERROR ADDING TONE");
				}
			}
		}
		
		return pageText.toString();
	}

	public String getRemoveTonePagerText(String sessionId) throws JsonMappingException, JsonProcessingException {
		StringBuilder pageText = new StringBuilder();
		IvrMltSession mltSession = mltCacheService.getBySessionId(sessionId);
		
		if (mltSession != null && mltSession.getMltTestResult()!=null) {
			Mdata mData = objectMapper.readValue(mltSession.getMltTestResult(), Mdata.class);	
		
			String primaryNpa = mData.getTestRsp().getKey().getCid().getFmtP().getNpa();
			String primaryNxx = mData.getTestRsp().getKey().getCid().getFmtP().getNnx();	
			String lineNo=mData.getTestRsp().getKey().getCid().getFmtP().getLine();
			
			if(StringUtils.isNotBlank(primaryNpa) && StringUtils.isNotBlank(primaryNxx) && StringUtils.isNotBlank(lineNo))
			{
				if(mData.getTestRsp()!=null && mData.getTestRsp().getFinalFlag()==89) {/// check for message status='S'
				
					pageText.append(" ").append(primaryNpa).append("-").append(primaryNxx).append("-").append(lineNo).append(" ");
					pageText.append("|TONE REMOVED");
				
				}
				else {
					pageText.append(" ").append(primaryNpa).append("-").append(primaryNxx).append("-").append(lineNo).append(" ");
					pageText.append("|ERROR REMOVING TONE");
				}
			}
		}
		
		return pageText.toString();
	}
	
	public boolean isTestCodeAvailable(String code) {
		boolean flag=false;
		
		if("SU".equals(code) || "BG".equals(code)|| "BO".equals(code) || "BS".equals(code))
		{
			flag=true;
		}
		return flag;
	}
	
	public StringBuilder getAC_DCPagerText(Mdata mData) {
		
		StringBuilder pageText = new StringBuilder();
		ACSIG acSig = MdataUtils.getAcSig(mData);
		DCSIGCRAFT dcSigCraft = MdataUtils.getDcCraft(mData);
		
			pageText.append("DC-KOHM/VOLT AC-KOHM ");
			if(dcSigCraft!=null && dcSigCraft.getDcResTr()!=null)
			{
				pageText.append(dcSigCraft.getDcResTr());
				pageText.append("        ");
			}
			else {
				pageText.append("            ");
			}
			
			///////////////////AC resistance tipRing///////////////////////////
			if(acSig!=null && acSig.getAcResTr()!=null)
			{
				pageText.append(acSig.getAcResTr());
				pageText.append("   ");
			}
			else {
				pageText.append("     ");
			}
			pageText.append("T-R ");
			
			///////////////DC resistance tip ground//////////////////
			if(dcSigCraft!=null && dcSigCraft.getDcResTg()!=null)
			{
				pageText.append(dcSigCraft.getDcResTg());
				pageText.append("   ");
			}
			else {
				pageText.append("       ");
			}
			
			///////////DC volt tipGround/////////////
			if(dcSigCraft!=null && dcSigCraft.getDcVoltTg()!=null)
			{
				pageText.append(dcSigCraft.getDcVoltTg());
				pageText.append(" ");
			}
			else {
				pageText.append("     ");
			}
			
			///////////////AC Resistance tipGround/////////////////////
			if(acSig!=null && acSig.getAcResTg()!=null)
			{
				pageText.append(acSig.getAcResTg());
				pageText.append("  ");
			}
			else {
				pageText.append("     ");
			}
			
			pageText.append("T-G ");
			
			/////////////////DC Resistance RingGround////////////////////////
			if(dcSigCraft!=null && dcSigCraft.getDcResRg()!=null)
			{
				pageText.append(dcSigCraft.getDcResRg());
				pageText.append("   ");
			}
			else {
				pageText.append("       ");
			}
			
			/////////////DC Volt RingGround///////////////////
			if(dcSigCraft!=null && dcSigCraft.getDcVoltRg()!=null)
			{
				pageText.append(dcSigCraft.getDcVoltRg());
				pageText.append(" ");
			}
			else {
				pageText.append("     ");
			}
			
			/////////////AC Resistance RingGround///////////////////////
			if(acSig!=null && acSig.getAcResRg()!=null)
			{
				pageText.append(acSig.getAcResRg());
				pageText.append("  ");
			}
			else {
				pageText.append("     ");
			}
			
			pageText.append("R-G");
			
		 
		return pageText;
	}
	
	public StringBuilder getTnText(Mdata mData ) {
		StringBuilder pageText = new StringBuilder();
		
		FAULTINFO1 f=MdataUtils.getFAULTINFO1(mData);
		MLTTESTRSPCIDFMTP fmtp=MdataUtils.getMLTTESTRSPCIDFMTP(mData);
		String primaryNpa = fmtp.getNpa();
		String primaryNxx = fmtp.getNnx();	
		String lineNo=fmtp.getLine();
		
		if(f!=null && f.getTestCode()!=null && StringUtils.isNotBlank(primaryNpa) && StringUtils.isNotBlank(primaryNxx) && StringUtils.isNotBlank(lineNo))
		{
			if(isTestCodeAvailable(mData.getTestRsp().getF().getTestCode()))
			{
				pageText.append("*").append(primaryNpa).append(primaryNxx).append(lineNo).append(" VER ").append(mData.getTestRsp().getF().getTestCode()).append(" ");

			}
			else {
				pageText.append(" ").append(primaryNpa).append(primaryNxx).append(lineNo).append(" VER ").append(mData.getTestRsp().getF().getTestCode()).append(" ");
				
			}
		}
		else {
			pageText.append(" ").append(primaryNpa).append(primaryNxx).append(lineNo).append(" VER ").append("   ");
			
		}
		return pageText;
	}
	
	
	public String getTestDescriptionText(String testResult) {
		StringBuilder pageText = new StringBuilder();
		String testResultNew = testResult.trim();
		List<String>testResultList=Arrays.asList(testResultNew.split(";"));
		
		for(int i=0;i<testResultList.size();i=i+2) {
			pageText.append("|");
        	pageText.append(testResultList.get(i));
		}
		
//		if(testResultList.size()>1) {
//			for(int i=1;i<testResultList.size();i=i+2) {
//				pageText.append("|");
//	        	pageText.append(testResultList.get(i));
//			}
//		}
		return pageText.toString();
		
	}
	
	
}
