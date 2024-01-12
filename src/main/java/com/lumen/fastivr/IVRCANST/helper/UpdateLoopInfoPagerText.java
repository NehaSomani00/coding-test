package com.lumen.fastivr.IVRCANST.helper;

import com.lumen.fastivr.IVRCANST.entity.IVRCanstEntity;
import com.lumen.fastivr.IVRLFACS.CSLPage;
import org.springframework.stereotype.Service;

@Service
public class UpdateLoopInfoPagerText {
	
	public String createUpdateLoopInfoFailurePage(String error, IVRCanstEntity canstSession) {
		// fetch the cktid from session
		String circuitId = "";
		CSLPage csLPage = new CSLPage("TERM", circuitId);
		return csLPage.fmtErr(error);
	}

	public String createUpdateLoopInfoSuccessPage(IVRCanstEntity canstSession) {
		// fetch the cktid from session
		String circuitId = "";
		String currentTerminalAddress = canstSession.getOldTea();
		String replacementTerminalAddress = canstSession.getNewTea();
		String successText = "";
		successText += "   ";
		successText += circuitId;
		successText += " TERM";
		successText += "|fr: ";
		successText += currentTerminalAddress;
		successText += "|to: ";
		successText += replacementTerminalAddress;
		successText += "|LFACS updated.";

		return successText;
	}

}
