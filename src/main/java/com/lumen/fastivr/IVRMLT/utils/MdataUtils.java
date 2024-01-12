package com.lumen.fastivr.IVRMLT.utils;

import tollgrade.loopcare.testrequestapi.ACSIG;
import tollgrade.loopcare.testrequestapi.DCSIGCRAFT;
import tollgrade.loopcare.testrequestapi.FAULTINFO1;
import tollgrade.loopcare.testrequestapi.MLTTESTRSPCIDFMTP;
import tollgrade.loopcare.testrequestapi.Mdata;
import tollgrade.loopcare.testrequestapi.POTSRESULTS1;
import tollgrade.loopcare.testrequestapi.POTSRESULTS2;

public class MdataUtils {
	
	public static DCSIGCRAFT getDcCraft(Mdata mData) {
		DCSIGCRAFT dcSigCraft = null;
		if(mData != null && mData.getTestRsp() != null && !mData.getTestRsp().getD().getItem().isEmpty()
					&& mData.getTestRsp().getD().getItem().get(0).getPotsResults1() != null
					&& mData.getTestRsp().getD().getItem().get(0).getPotsResults1().getDcCraft() != null){
			dcSigCraft = mData.getTestRsp().getD().getItem().get(0).getPotsResults1().getDcCraft();
		}
		return dcSigCraft;
		
	}
	public static ACSIG getAcSig(Mdata mData) {
		ACSIG acSig = null;
		if(mData != null && mData.getTestRsp() != null && !mData.getTestRsp().getD().getItem().isEmpty()
				&& mData.getTestRsp().getD().getItem().get(0).getPotsResults1() != null
				&& mData.getTestRsp().getD().getItem().get(0).getPotsResults1().getAc() != null){
			acSig = mData.getTestRsp().getD().getItem().get(0).getPotsResults1().getAc();
		}
		return acSig;
		
	}
	
	public static FAULTINFO1 getFAULTINFO1(Mdata mData) {
		FAULTINFO1 f = null;
		if(mData!=null && mData.getTestRsp()!=null && mData.getTestRsp().getF()!=null){
			f =  mData.getTestRsp().getF();
		}
		return f;
		
	}
	
	public static MLTTESTRSPCIDFMTP getMLTTESTRSPCIDFMTP(Mdata mData) {
		MLTTESTRSPCIDFMTP fmtp = null;
		if(mData!=null && mData.getTestRsp()!=null && mData.getTestRsp().getKey()!=null 
				&& mData.getTestRsp().getKey().getCid()!=null 
				&& mData.getTestRsp().getKey().getCid().getFmtP()!=null){
			fmtp =  mData.getTestRsp().getKey().getCid().getFmtP();
		}
		return fmtp;
		
	}
	
	public static POTSRESULTS1 getPOTSRESULTS1(Mdata mData) {
		POTSRESULTS1 pResult1 = null;
		if(mData.getTestRsp()!=null &&
				mData.getTestRsp().getD()!=null && 
				!mData.getTestRsp().getD().getItem().isEmpty() && 
				mData.getTestRsp().getD().getItem().get(0).getPotsResults1()!=null ){
			pResult1 =  mData.getTestRsp().getD().getItem().get(0).getPotsResults1();
		}
		return pResult1;
		
	}
	
	public static POTSRESULTS2 getPOTSRESULTS2(Mdata mData) {
		POTSRESULTS2 pResult2 = null;
		if(mData.getTestRsp()!=null &&
				mData.getTestRsp().getD()!=null && 
				!mData.getTestRsp().getD().getItem().isEmpty() && 
				mData.getTestRsp().getD().getItem().get(0).getPotsResults2()!=null){
			pResult2 =  mData.getTestRsp().getD().getItem().get(0).getPotsResults2();
		}
		return pResult2;
		
	}

	
}
