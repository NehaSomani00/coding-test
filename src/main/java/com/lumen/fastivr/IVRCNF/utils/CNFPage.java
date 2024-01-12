package com.lumen.fastivr.IVRCNF.utils;


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
import com.lumen.fastivr.IVRDto.CurrentAssignmentResponseDto;
import com.lumen.fastivr.IVRDto.LOOP;
import com.lumen.fastivr.IVRDto.SEG;
import com.lumen.fastivr.IVRDto.RetrieveMaintenanceChangeTicket.CandidatePairInfo;
import com.lumen.fastivr.IVRDto.RetrieveMaintenanceChangeTicket.RetrieveMaintenanceChangeTicketResponse;
import com.lumen.fastivr.IVRDto.additionalLines.AdditionalLinesReportResponseDto;
import com.lumen.fastivr.IVRDto.retrieveLoopAssignment.LoopAssignCandidatePairInfo;
import com.lumen.fastivr.IVRDto.retrieveLoopAssignment.RetrieveLoopAssignmentResponse;
import com.lumen.fastivr.IVRSessionManagement.IVRUserSession;
import com.lumen.fastivr.IVRUtils.FormatUtilities;

@Service
public class CNFPage {
	
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CNFPage.class);

	private String reqType;
	
	private String reqCircuit;
	
	private String reqCable;
	
	private String reqPair;
	
	private String reqNewCable;
	
	private String reqNewPair;
	
	private String reqOrder;
	
	private String reqTea;
	
	private String errorText;
	
	private String transType;
	
	private String sendToId;
	
	//
	//private String circuitId; //reqCircuit
	private String currentCableId;
	
	private String currentCableUnitId;
	
	private String replacementCableId;
	
	private String replacementCableUnitId;
	
	private String serviceOrderNumber;
	
	private ObjectMapper objectMapper;
	
	public CNFPage() { }
	
	@Autowired
	public CNFPage(ObjectMapper objectMapper) { 
		this.objectMapper = objectMapper;
	}
	
	public CNFPage(String rType, String ckid) {
		this.reqType = rType;
		this.reqCircuit = ckid;
	}
	
	public void setInitValues(String rType, String ckid) {
		this.reqType = rType;
		this.reqCircuit = ckid;
	}
	
	//
	public CNFPage(String reqType, String reqCircuit, String currentCableId, String currentCableUnitId, String replacementCableId,
			String replacementCableUnitId, String serviceOrderNumber, String transType, String sendToId) {
		this.reqType = reqType;
		this.reqCircuit = reqCircuit;
		this.currentCableId = currentCableId;
		this.currentCableUnitId = currentCableUnitId;
		this.replacementCableId = replacementCableId;
		this.replacementCableUnitId = replacementCableUnitId;
		this.serviceOrderNumber = serviceOrderNumber;
		this.transType = transType;
		this.sendToId = sendToId;
	}
	
	public CNFPage(String reqType, String reqCircuit, String reqCable, String reqPair, String reqNewCable,
			String reqNewPair, String reqOrder, String reqTea, String errorText, String transType, String sendToId) {
		this.reqType = reqType;
		this.reqCircuit = reqCircuit;
		this.reqCable = reqCable;
		this.reqPair = reqPair;
		this.reqNewCable = reqNewCable;
		this.reqNewPair = reqNewPair;
		this.reqOrder = reqOrder;
		this.reqTea = reqTea;
		this.errorText = errorText;
		this.transType = transType;
		this.sendToId = sendToId;
	}
	
	// 4 param
	public CNFPage(String reqType, String reqCircuit, String transType, String sendToId) {
		this.reqType = reqType;
		this.reqCircuit = reqCircuit;
		this.transType = transType;
		this.sendToId = sendToId;
	}	
	
	// 7 param
	public CNFPage(String reqType, String reqCircuit, String replacementCableId, String replacementCableUnitId,
			String serviceOrderNumber, String transType, String sendToId) {
		this.reqType = reqType;
		this.reqCircuit = reqCircuit;
		this.replacementCableId = replacementCableId;
		this.replacementCableUnitId = replacementCableUnitId;
		this.serviceOrderNumber = serviceOrderNumber;
		this.transType = transType;
		this.sendToId = sendToId;
	}

	public String FormatError() {
		
		StringBuilder pageText = new StringBuilder();
		
		pageText = pageText.append(" *Error* ").append(reqType);
		
		if(StringUtils.isNotBlank(reqCircuit)) {
			
			pageText = pageText.append("|Tel Nbr = ").append(reqCircuit);
		}
		
		if(StringUtils.isNotBlank(reqCable)) {
			
			if(StringUtils.isNotBlank(reqNewCable)) {
				
				pageText = pageText.append("|fr ca/pr: ").append(reqCable).append("/").append(reqPair).append("|to ca/pr: ").append(reqNewCable)
					.append("/").append(reqNewPair).append("|Cut failed");
			} else {
				
				pageText = pageText.append("|ca: ").append(reqCable);
				
				if("DPR".equalsIgnoreCase(reqType)) {
					
					pageText = pageText.append(" fr: ").append(reqPair);
				} else {
					
					pageText = pageText.append(" pr: ").append(reqPair);
				}
			}
		}
		
		if(StringUtils.isNotBlank(reqOrder)) {
			
			pageText = pageText.append("|").append(reqOrder);
		}
		
		if(StringUtils.isNotBlank(reqTea)) {
			
			pageText = pageText.append("|").append(reqTea);
		}
		
		if("TERM".equals(pageText.toString())) {
			
			pageText = pageText.append("|LFACS update failed: ").append(errorText).append(".|Pls call assignment.");
		} else {
			
			pageText = pageText.append("|Text: ").append(errorText);
		}
		
		return pageText.toString();
	}

	/**
	 * Processing Success pager message for Additional Lines 
	 * @param response
	 * @param countADL
	 * @param sessionId
	 * @return
	 */
	public String formatAddlLines(AdditionalLinesReportResponseDto response, int countADL, String sessionId) {
		String temp = " ";
		String pageText = " ";
		List<String> addlLines = new ArrayList<>();

		LOGGER.info("Session: " + sessionId + " formatAddlLines reqCircuit = " + reqCircuit);
		if (FormatUtilities.FormatTelephoneNNNXXXX(reqCircuit) != "") {
			pageText += reqCircuit;
		}

		pageText += " ";
		pageText += reqType;

		String countStr = String.valueOf(countADL);
		String termStr = "";

		if (countADL == 0) {
			countStr = "No";
			termStr = "s at this address";
		} else if (countADL == 1) {
			termStr = ":";
		} else {
			termStr = "s:";
		}
		pageText += " | " + countStr + " additional line" + termStr;

		for (int i = 0; i < response.getReturnDataSet().size(); i++) {
			if (response.getReturnDataSet().get(i) != null) {
				temp = FormatUtilities.FormatTelephoneNNNXXXX(response.getReturnDataSet().get(i));
				if (!temp.equalsIgnoreCase(reqCircuit)) {
					pageText += "|";
					pageText += response.getReturnDataSet().get(i);
					addlLines.add(response.getReturnDataSet().get(i));
				}
			}
		}
		
		response.setReturnDataSet(addlLines);
		LOGGER.info("Session: " + sessionId + " Additional Lines PAGE Text: " + pageText);
		return pageText;
	}
	
	/**
	 * Processing Error Pager text in case of API Inquiry Failure
	 * @param errorText
	 * @return
	 */
	public String fmtErr(String errorText) {
		
		LOGGER.info("CNFPage FmtErr Here 1");
		String temp;
		StringBuilder pageText = new StringBuilder();

		pageText.append("  *ERROR*    ");
		pageText.append(reqType);
		LOGGER.info("CNFPage FmtErr Here 2");

		if ((reqCircuit != null) && (reqCircuit != "")) {
			pageText.append("|Tel Nbr = ");
			temp = FormatUtilities.FormatTelephoneNNNXXXX(reqCircuit);
			pageText.append(temp);
		}

		LOGGER.info("CNFPage FmtErr Here 3.");
		if ((reqCable != null) && (reqCable != "")) {
			LOGGER.info("CNFPage FmtErr Here 4. reqCable=" + reqCable);
			if ((reqNewCable != null) && (reqNewCable != "")) {
				LOGGER.info("CNFPage FmtErr Here 5. reqNewCable=" + reqNewCable);
				//
				// In this case, and attempt to perform some sort
				// of Cut has failed.
				//

				pageText.append("|fr ca/pr: ");
				pageText.append(reqCable);
				pageText.append("/");
				pageText.append(reqPair);

				pageText.append("|to ca/pr: ");
				pageText.append(reqNewCable);
				pageText.append("/");
				pageText.append(reqNewPair);
				pageText.append("|Cut failed: ");
			} else {
				pageText.append("|ca: ");
				pageText.append(reqCable);

				//
				// In the case of a DPR request, the pair is really the
				// start of a range.
				//
				if (reqType == "DPR") {
					pageText.append(" fr: ");
				} else {
					pageText.append(" pr: ");
				}
				pageText.append(reqPair);
			}
		}
		LOGGER.info("CNFPage FmtErr Here 6");
		if ((reqOrder != null) && (reqOrder != "")) {
			LOGGER.info("CNFPage FmtErr Here 7. reqOrder=" + reqOrder);
			pageText.append("|");
			pageText.append(reqOrder);
		}

		if ((reqTea != null) && (reqTea != "")) {
			LOGGER.info("CNFPage FmtErr Here 8. reqTea=" + reqTea);
			pageText.append("|");
			pageText.append(reqTea);
		}

		LOGGER.info("CNFPage FmtErr Here 9. reqTea=" + reqTea);
		temp = errorText.trim();

		if (pageText.toString() == "TERM") {
			LOGGER.info("CNFPage FmtErr Here 10");
			pageText.append("|LFACS update failed: ");
			pageText.append(temp);
			pageText.append(".|Pls call assignment.");
			
		} else {
			LOGGER.info("CNFPage.cs FmtErr Here temp=" + temp);
			pageText.append("|Text: " + temp);
		}

		return pageText.toString();
	}
	
	public String formatSparePairsPage(IVRUserSession session, int segNmbr, boolean candPairsAvailable, int cntTotal,
			int cntCt, int cntUnd, RetrieveLoopAssignmentResponse retrieveLoopAssignmentResponse,
			RetrieveMaintenanceChangeTicketResponse retrieveMaintenanceChangeTicketResponse)
			throws JsonMappingException, JsonProcessingException {
		
		//IVRUserSession session = cacheService.getBySessionId(sessionId);
		String currentAssignmentResponse = session.getCurrentAssignmentResponse();
		CurrentAssignmentResponseDto currResp = objectMapper.readValue(currentAssignmentResponse,
				CurrentAssignmentResponseDto.class);
		
		String tea ="",tid ="",ord ="",addr ="",cable ="",pair ="",bpcc ="",ckId ="",pageText ="",temp ="",dpaFlag="";
		ckId = reqCircuit;
		LOOP loop = currResp.getReturnDataSet().getLoop().get(0);
		if(loop != null) {
		tid = loop.getTID();
		
		 if(loop.getSO() != null && loop.getSO().size() > 0) {
			 ord = loop.getSO().get(0).getORD();
		 }
		 
		 if(loop.getADDR() != null && loop.getADDR().size() > 0) {
			 addr = loop.getADDR().get(0).getADDRNO();
		 }
		
			if (loop.getSEG() != null && loop.getSEG().size() > 0 && loop.getSEG().get(segNmbr - 1) != null) {
				SEG seg = loop.getSEG().get(segNmbr - 1);
				tea = seg.getTEA();
				cable = seg.getCA();
				pair = seg.getPR();
				bpcc = seg.getBP();
			}
		
		dpaFlag = tid !=null && tid.contains("DPA") ? "D" : "";
		}
		
		LOGGER.info("GetPageCandPrs() 1");

		if ((ckId != null) && (ckId != ""))
			pageText += FormatUtilities.FormatTelephoneNNNXXXX(ckId);
		pageText += " SPR";
		pageText += "|f";
		LOGGER.info("GetPageCandPrs() 2");
		pageText += segNmbr;
		LOGGER.info("GetPageCandPrs() 3");
		pageText += " ";
		if ((cable != null) && (cable != ""))
			pageText += cable.trim();
		pageText += "/";
		LOGGER.info("GetPageCandPrs() 3.1");
		if ((pair != null) && (pair != ""))
			pageText += pair.trim();
		LOGGER.info("GetPageCandPrs() 3.3");
		if ((bpcc != null) && (!bpcc.isEmpty()) && (bpcc.length() > 0) && (FormatUtilities.isBP(bpcc.charAt(0)))) {
			LOGGER.info("GetPageCandPrs() 3.4");
			pageText += " BP ";
		}
		LOGGER.info("GetPageCandPrs() 3.5");
		if (bpcc != null)
			pageText += bpcc.trim();

		if (!candPairsAvailable) {
			pageText += "|";
			LOGGER.info("GetPageCandPrs() 4");
			pageText += "NO CANDIDATE PAIRS ARE AVAILABLE";
		} else {
			LOGGER.info("GetPageCandPrs() 4.1");
			if (dpaFlag.equalsIgnoreCase("D"))
				pageText += "|Warning: DPA";
			pageText += "|";
			LOGGER.info("GetPageCandPrs() 5");

			// Original code had the following comment:
			// "ONE OF THESE IS PROABABLY WRONG, BUT THIS IS THE WAY THE CODE EXISTED."
			LOGGER.info("addr=" + addr);
			LOGGER.info("tea=" + tea);
			if (retrieveLoopAssignmentResponse != null) {
				if (addr != null)
					temp = addr;
				LOGGER.info("GetPageCandPrs() 6");
			} else if (retrieveMaintenanceChangeTicketResponse != null) {
				if (tea != null)
					temp = tea;
				LOGGER.info("GetPageCandPrs() 7");
			}
			pageText += temp;
			if ((ord != null) && (!ord.isEmpty())) {
				pageText += "|S.Ord ";
				pageText += ord.trim() + " ";
				LOGGER.info("GetPageCandPrs() 8");
			}
			if (cntTotal == 0)
				pageText += "There are no usable spare pairs";
			else if (cntTotal == cntCt)
				pageText += "All spare pairs have status of <CT>";
			else {
				cntUnd += cntCt;
				LOGGER.info("GetPageCandPrs() 9");

				if (cntTotal == cntUnd) {
					pageText += "|No usable spare pairs are available.";
					pageText += "|UNDESIRABLES: ";
					LOGGER.info("GetPageCandPrs() 10");
				}
				if (retrieveLoopAssignmentResponse != null) {
					
					//for loop assignment requests
					List<LoopAssignCandidatePairInfo> candPairs = retrieveLoopAssignmentResponse.getReturnDataSet().getCandidatePairInfo();
					if (candPairs != null) {
						
						for (int i = 0; i < candPairs.size(); i++) {

							LoopAssignCandidatePairInfo candPair = candPairs.get(i);
							String cableId = candPair.getCableId();
							String pairId = candPair.getCableUnitId();
							String cndPairStatus = candPair.getCandidatePairStatus();
							String prSelField = candPair.getPairSelectionField();
							
							if ((cableId != null)
									&& (!cableId.isEmpty())
									&& (pairId != null)
									&& (!pairId.isEmpty())) {
								
								if (cndPairStatus !=null && (
										 ((!cndPairStatus.toUpperCase().equalsIgnoreCase("CT"))
										&& (cntTotal == cntUnd)
										&& (prSelField != null)
										&& (!prSelField.isEmpty()))
										|| ((cntTotal != cntUnd)
												&& (!cndPairStatus.toUpperCase().equalsIgnoreCase("CT"))
												&& ((prSelField == null)
														|| (prSelField.isEmpty()))))) {
									
									LOGGER.info("GetPageCandPrs() 11");
									pageText += "|";
									pageText += cndPairStatus;
									pageText += " ";
									pageText += cableId;
									pageText += "/";
									pageText += pairId;
									pageText += " ";

									String bpCC = candPair.getBindingPostColorCode();
									
									if ((bpCC != null)
											&& (!bpCC.isEmpty())
											&& (bpCC.length() > 0)
											&& (FormatUtilities.isBP(bpCC.charAt(0)))) {
										
										pageText += "BP " + bpCC;
									}
									else if ((bpCC != null)
											&& (!bpCC.isEmpty())
											&& (bpCC.length() > 0)
											&& (FormatUtilities.isCC(bpCC.charAt(0)))) {
										
										pageText += bpCC.replaceAll("-", "");
									}

								}
							}
						} // end of for
					} // end of if
				} else if(retrieveMaintenanceChangeTicketResponse != null){
					
					//for maintenance api request 
					List<CandidatePairInfo> candidatePairs = retrieveMaintenanceChangeTicketResponse.getReturnDataSet().getCandidatePairInfo();
					if (candidatePairs != null) {
						
						for (int i = 0; i < candidatePairs.size(); i++) {
							LOGGER.info("GetPageCandPrs() 12");
							CandidatePairInfo candidatePairInfo = candidatePairs.get(i);
							String cableId = candidatePairInfo.getCableId();
							String pairId = candidatePairInfo.getCableUnitId();
							
							if ((cableId != null) && (!cableId.isEmpty())
									
									&& (pairId != null)
									&& (!pairId.isEmpty())) {
								
								LOGGER.info("GetPageCandPrs() 13");

								String pairStatus = candidatePairInfo.getPairStatus();
								String pairSelectionInfo = candidatePairInfo.getPairSelectionInfo();
								
								if (pairStatus!=null
										&& (((!pairStatus.toUpperCase().equalsIgnoreCase("CT"))
										&& (cntTotal == cntUnd)
										&& (pairSelectionInfo != null)
										&& (!pairSelectionInfo.isEmpty()))
										|| ((cntTotal != cntUnd)
												&& (!pairStatus.toUpperCase()
														.equalsIgnoreCase("CT"))
												&& ((pairSelectionInfo == null)
														|| (pairSelectionInfo.isEmpty()))))) {
									
									LOGGER.info("GetPageCandPrs() 14");
									pageText += "|";
									pageText += pairStatus;
									pageText += " ";
									pageText += cableId;
									pageText += "/";
									pageText += pairId;
									pageText += " ";
									LOGGER.info("GetPageCandPrs() 15");

									String bpCC = candidatePairInfo.getBindingPostColorCode();
									
									if ((bpCC != null)
											&& (!bpCC.isEmpty())
											&& (bpCC.length() > 0)
											&& (FormatUtilities.isBP(bpCC.charAt(0)))) {
										
										pageText += "BP " + bpCC;
									}
									else if ((bpCC != null)
											&& (!bpCC.isEmpty())
											&& (bpCC.length() > 0)
											&& (FormatUtilities.isCC(bpCC.charAt(0)))) {
										
										pageText += bpCC.replaceAll("-", "");
									}
									LOGGER.info("GetPageCandPrs() 16.1");
								}
							}
						} // end of for
					}
				}
			}
		}
		return pageText;
	}

	public String getReqType() {
		return reqType;
	}

	public void setReqType(String reqType) {
		this.reqType = reqType;
	}

	public String getReqCircuit() {
		return reqCircuit;
	}

	public void setReqCircuit(String reqCircuit) {
		this.reqCircuit = reqCircuit;
	}

	public String getReqCable() {
		return reqCable;
	}

	public void setReqCable(String reqCable) {
		this.reqCable = reqCable;
	}

	public String getReqPair() {
		return reqPair;
	}

	public void setReqPair(String reqPair) {
		this.reqPair = reqPair;
	}

	public String getReqNewCable() {
		return reqNewCable;
	}

	public void setReqNewCable(String reqNewCable) {
		this.reqNewCable = reqNewCable;
	}

	public String getReqNewPair() {
		return reqNewPair;
	}

	public void setReqNewPair(String reqNewPair) {
		this.reqNewPair = reqNewPair;
	}

	public String getReqOrder() {
		return reqOrder;
	}

	public void setReqOrder(String reqOrder) {
		this.reqOrder = reqOrder;
	}

	public String getReqTea() {
		return reqTea;
	}

	public void setReqTea(String reqTea) {
		this.reqTea = reqTea;
	}

	public String getErrorText() {
		return errorText;
	}

	public void setErrorText(String errorText) {
		this.errorText = errorText;
	}

	public String getTransType() {
		return transType;
	}

	public void setTransType(String transType) {
		this.transType = transType;
	}

	public String getSendToId() {
		return sendToId;
	}

	public void setSendToId(String sendToId) {
		this.sendToId = sendToId;
	}
	
	public String getCurrentCableId() {
		return currentCableId;
	}

	public void setCurrentCableId(String currentCableId) {
		this.currentCableId = currentCableId;
	}

	public String getCurrentCableUnitId() {
		return currentCableUnitId;
	}

	public void setCurrentCableUnitId(String currentCableUnitId) {
		this.currentCableUnitId = currentCableUnitId;
	}

	public String getReplacementCableId() {
		return replacementCableId;
	}

	public void setReplacementCableId(String replacementCableId) {
		this.replacementCableId = replacementCableId;
	}

	public String getReplacementCableUnitId() {
		return replacementCableUnitId;
	}

	public void setReplacementCableUnitId(String replacementCableUnitId) {
		this.replacementCableUnitId = replacementCableUnitId;
	}

	public String getServiceOrderNumber() {
		return serviceOrderNumber;
	}

	public void setServiceOrderNumber(String serviceOrderNumber) {
		this.serviceOrderNumber = serviceOrderNumber;
	}

	
}

