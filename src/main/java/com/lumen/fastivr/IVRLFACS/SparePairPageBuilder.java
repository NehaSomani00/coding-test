package com.lumen.fastivr.IVRLFACS;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumen.fastivr.IVRCacheManagement.IVRCacheService;
import com.lumen.fastivr.IVRDto.CurrentAssignmentResponseDto;
import com.lumen.fastivr.IVRDto.RetrieveMaintenanceChangeTicket.CandidatePairInfo;
import com.lumen.fastivr.IVRDto.RetrieveMaintenanceChangeTicket.RetrieveMaintenanceChangeTicketResponse;
import com.lumen.fastivr.IVRDto.retrieveLoopAssignment.LoopAssignCandidatePairInfo;
import com.lumen.fastivr.IVRDto.retrieveLoopAssignment.RetrieveLoopAssignmentResponse;
import com.lumen.fastivr.IVRSessionManagement.IVRUserSession;
import com.lumen.fastivr.IVRUtils.IVRConstants;

@Component
public class SparePairPageBuilder {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SparePairPageBuilder.class);
	
	@Autowired
	private IVRCacheService cacheService;
	
	@Autowired
	private IVRLfacsServiceHelper ivrLfacsServiceHelper;
	
	@Autowired
	private CSLPage csLPage;
	
//	@Autowired
//	private ObjectMapper objectMapper;
	
	/**
	 * Service order is present 
	 * @param retrieveLoopAssignmentResponse
	 * @param sessionId
	 * @param segNum
	 * @param device
	 * @param ckId
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	public void pageForRetrieveLoopAssignment(RetrieveLoopAssignmentResponse retrieveLoopAssignmentResponse,
			String sessionId, int segNum, String device, String ckId) throws JsonMappingException, JsonProcessingException {
		
		IVRUserSession session = cacheService.getBySessionId(sessionId);
		csLPage.setInitValues("SPR", ckId);
		if (retrieveLoopAssignmentResponse.getMessageStatus().getErrorMessage().isEmpty())
		{
			int cntTotal = 0;
			int cntCt = 0;
			int cntUnd = 0;
			boolean candPairsAvailable = true;

			// Check that no pairs are available
			List<LoopAssignCandidatePairInfo> candPairs = retrieveLoopAssignmentResponse.getReturnDataSet().getCandidatePairInfo();
			if ((candPairs == null) || (candPairs.size() == 0))
			{
				LOGGER.info("GetRetrieveLoopAssignmentAsyncStart 7");
				candPairsAvailable = false;
			}
			else
			{
				LOGGER.info("GetRetrieveLoopAssignmentAsyncStart 8");
				for (int i=0; i<candPairs.size(); i++)
				{
					LoopAssignCandidatePairInfo loopCandidatePairInfo = retrieveLoopAssignmentResponse.getReturnDataSet().getCandidatePairInfo().get(i);
					
					if (((loopCandidatePairInfo.getCableId() == null) || (loopCandidatePairInfo.getCableId().isEmpty())) 
							&&
						((loopCandidatePairInfo.getCableUnitId() == null) || (loopCandidatePairInfo.getCableUnitId().isEmpty())))
					{
						LOGGER.info("GetRetrieveLoopAssignmentAsyncStart 8");
						candPairsAvailable = false;
					}
				}
			}
			if (candPairsAvailable)
			{
				for (int i=0; i<retrieveLoopAssignmentResponse.getReturnDataSet().getCandidatePairInfo().size(); i++)
				{
					// Original code looked for "RST" or "CLASH" bpType here as well as stat but no indication
					// that there bpTypes were ever used
					
					String candPrStatus = retrieveLoopAssignmentResponse.getReturnDataSet().getCandidatePairInfo()
							.get(i).getCandidatePairStatus().toUpperCase();
					
					if ((candPrStatus.equalsIgnoreCase("RSV")) ||
						(candPrStatus.equalsIgnoreCase("WKG")) ||
						(candPrStatus.equalsIgnoreCase("*WKG")))
					{
						LOGGER.info("GetRetrieveLoopAssignmentAsyncStart 9");
						retrieveLoopAssignmentResponse.getReturnDataSet().getCandidatePairInfo().get(i).setCableId("");
						retrieveLoopAssignmentResponse.getReturnDataSet().getCandidatePairInfo().get(i).setCableUnitId("");
					}
				}
				for (int i=0; i<retrieveLoopAssignmentResponse.getReturnDataSet().getCandidatePairInfo().size(); i++)
				{				
					LoopAssignCandidatePairInfo candidatePairInfo = retrieveLoopAssignmentResponse.getReturnDataSet()
							.getCandidatePairInfo().get(i);
					String pair = candidatePairInfo.getCableUnitId();
					
					if ((pair != null) && (!pair.isEmpty()))
					{
						cntTotal++;
						LOGGER.info("cntTotal is now " + cntTotal);
						String pairStatus = candidatePairInfo.getCandidatePairStatus();
						
						if (pairStatus.equalsIgnoreCase("CT"))
						{
							cntCt++;
							LOGGER.info("cntCt is now " + cntCt);
						}
						else if ((candidatePairInfo.getPairSelectionField() != null) && (!candidatePairInfo.getPairSelectionField().isEmpty()))
						{
							LOGGER.info("GetRetrieveLoopAssignmentAsyncStart 10");
							cntUnd++;
							LOGGER.info("cntUnd is now " + cntUnd);
						}
					}
				}

			}
			// Now page the available pairs
				LOGGER.info("candPairsAvailable true condition page: segNum ="+ segNum +", candPairsAvailable: "+ candPairsAvailable+ 
						", cntTotal: "+cntTotal+
                		", cntCt:"+ cntCt+ ", cntUnd: "+ cntUnd);
				//Common.Utilities.SendResults.SendItAsync(requestedRspType, GetPageCandPrs(), sendToId, mCurrentTrans);
				String pageMsg = csLPage.formatSparePairsPage(session, segNum, candPairsAvailable, cntTotal, cntCt,
						cntUnd, retrieveLoopAssignmentResponse, null);
				//send to NET API
				ivrLfacsServiceHelper.sendTestResultToTech(IVRConstants.NETPAGE_SUBJECT_SPARE_PAIR, pageMsg, device,
						session);
				LOGGER.info("After Send out mobile village GetPageCandPrs");

		}
		else // Error message found
		{
				LOGGER.info("GetRetrieveLoopAssignmentAsyncStart 12");
				//CSLPage csLPage = new CSLPage("SPR", ckId);
				String pageMsg = csLPage.fmtErr(retrieveLoopAssignmentResponse.getMessageStatus().getErrorMessage());
				//send to NET API
				ivrLfacsServiceHelper.sendTestResultToTech(IVRConstants.NETPAGE_SUBJECT_SPARE_PAIR, pageMsg, device,
						session);
				LOGGER.info("After Send out mobile village CSLPage");

		}
		
	}

	/**
	 * no service order case
	 * @param retrieveMaintenanceChangeTicketResponse
	 * @param sessionId
	 * @param segNum
	 * @param device
	 * @param ckId
	 */
	public void pageForRetrieveMaintenanceChangeTicket(RetrieveMaintenanceChangeTicketResponse retrieveMaintenanceChangeTicketResponse, 
			String sessionId, int segNum, String device, String ckId) { 
		
		IVRUserSession session = cacheService.getBySessionId(sessionId);
		csLPage.setInitValues("SPR", ckId);
		String pageText = "";
		boolean candPairsAvailable = true;
		try {
		    if (retrieveMaintenanceChangeTicketResponse.getMessageStatus().getErrorMessage().equalsIgnoreCase("4-WIRE MCT")) {

		    	pageText = csLPage.fmtErr("This is a special circuit - which cannot be handled here. Pls call for manual assistance.");
		    } else {
		        LOGGER.info("4-WIRE MCT message did not exist");
		        if (retrieveMaintenanceChangeTicketResponse.getMessageStatus().getErrorMessage().isEmpty()) {
		        	//success scenario - no error message found
		        	
		        	List<CandidatePairInfo> candidatePairInfoList = retrieveMaintenanceChangeTicketResponse.getReturnDataSet().getCandidatePairInfo();
		            
		        	if (candidatePairInfoList == null || candidatePairInfoList.size() == 0) {
		                LOGGER.info("No pairs are available");
		                candPairsAvailable = false;
		                
		            } else {
		                for (int i = 0; i < retrieveMaintenanceChangeTicketResponse.getReturnDataSet().getCandidatePairInfo().size(); i++) {
		                	
		                	CandidatePairInfo candidatePairInfo = retrieveMaintenanceChangeTicketResponse.getReturnDataSet().getCandidatePairInfo().get(i);
		                    String cable = candidatePairInfo.getCableId();
		                    String pair = candidatePairInfo.getCableUnitId();
		                	
		                    if ((cable == null || cable.equals("")) &&
		                            (pair == null || pair.equals(""))) {
		                    	
		                        LOGGER.info("Setting candPairsAvailable to false, not all cable/pair data is populated");
		                        candPairsAvailable = false;
		                    }
		                }
		            }
		            if (candPairsAvailable) {
		                for (int i = 0; i < retrieveMaintenanceChangeTicketResponse.getReturnDataSet().getCandidatePairInfo().size(); i++) {
		                	
		                	CandidatePairInfo candidatePairInfo = retrieveMaintenanceChangeTicketResponse.getReturnDataSet().getCandidatePairInfo().get(i);
		                	String pairStatus = candidatePairInfo.getPairStatus();
		                	
		                	if ((pairStatus != null) &&
		                            ((pairStatus.toUpperCase().equals("RSV")) ||
		                            (pairStatus.toUpperCase().equals("WKG")) ||
		                            (pairStatus.toUpperCase().equals("*WKG")))) {
		                    	
		                    	//true
		                		candidatePairInfo.setCableId("");
		                		candidatePairInfo.setCableUnitId("");
		                        
		                    }
		                }

		                int cntTotal = 0, cntCt = 0, cntUnd = 0;
		                for (int i = 0; i < retrieveMaintenanceChangeTicketResponse.getReturnDataSet().getCandidatePairInfo().size(); i++) {
		                    
		                	CandidatePairInfo candidatePairInfo = retrieveMaintenanceChangeTicketResponse.getReturnDataSet().getCandidatePairInfo().get(i);
		                	String pair  = candidatePairInfo.getCableUnitId() ;
		                	String pairStatus = candidatePairInfo.getPairStatus();
		                	String pairSelectionInfo = candidatePairInfo.getPairSelectionInfo();
		                	
		                	if ((pair != null) && (!pair.equals(""))) {
		                    	cntTotal++;
		                    	
		                        if (pairStatus.equals("CT")) {
		                            cntCt++;
		                            
		                        } else if ((pairSelectionInfo != null) && (!pairSelectionInfo.equals(""))) {
		                            cntUnd++;
		                        }
		                    }
		                }
		                LOGGER.info("candPairsAvailable true condition page: segNum ="+ segNum +", candPairsAvailable: "+ candPairsAvailable+ ", cntTotal: "+cntTotal+
		                		", cntCt:"+ cntCt+ ", cntUnd: "+ cntUnd);
		                pageText = csLPage.formatSparePairsPage(session, segNum,
		                		candPairsAvailable, cntTotal, cntCt, cntUnd, null, retrieveMaintenanceChangeTicketResponse);
		                
		                
		            } else {
		            	LOGGER.info("candPairsAvailable false condition page");
		                pageText = csLPage.formatSparePairsPage(session, segNum,
		                		candPairsAvailable, 0, 0, 0, null, retrieveMaintenanceChangeTicketResponse);
		               
		            }
		        } else {
		        	//error scenario - error message found
		        	
		            LOGGER.info("Error message found, sending page");

		                LOGGER.info("Before sending CSLPage");
		                LOGGER.info("CSCandPrTran.cs ckId=" + ckId + " send to id: "+ session.getCuid() );
		                pageText = csLPage.fmtErr(retrieveMaintenanceChangeTicketResponse.getMessageStatus().getErrorMessage());
		                LOGGER.info("After sending CSLPage");
		        }
		        
		    }
		    
		    //Now send the page text to the device 
	        ivrLfacsServiceHelper.sendTestResultToTech(IVRConstants.NETPAGE_SUBJECT_SPARE_PAIR, pageText, device, session);
	        
		} catch (Exception ex) {
		    LOGGER.info("caught exception in GetRetrieveMaintenanceChangeTicketAsyncStart");
		    LOGGER.info("Error is: " + ex.getMessage());
		}

		
	}
	
}
