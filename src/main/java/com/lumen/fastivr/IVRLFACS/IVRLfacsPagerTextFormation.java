/**
 * 
 */
package com.lumen.fastivr.IVRLFACS;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumen.fastivr.IVRDto.CurrentAssignmentResponseDto;
import com.lumen.fastivr.IVRDto.IVRParameter;
import com.lumen.fastivr.IVRDto.IVRWebHookResponseDto;
import com.lumen.fastivr.IVRDto.LOOP;
import com.lumen.fastivr.IVRDto.SEG;
import com.lumen.fastivr.IVRDto.LOSDB.CTelephone;
import com.lumen.fastivr.IVRDto.LOSDB.TNInfoResponse;
import com.lumen.fastivr.IVRDto.coe.CentralOfficeEquipmentResponseDto;
import com.lumen.fastivr.IVRDto.coe.CentralOfficeEquipmentReturnDataSet;
import com.lumen.fastivr.IVRDto.defectivepairs.DefectivePairsRequestDto;
import com.lumen.fastivr.IVRDto.defectivepairs.DefectivePairsResponseDto;
import com.lumen.fastivr.IVRDto.multipleappearance.MultipleAppearanceResponseDto;
import com.lumen.fastivr.IVRSessionManagement.IVRUserSession;
import com.lumen.fastivr.IVRUtils.CurrentAssignmentUtils;
import com.lumen.fastivr.IVRUtils.IVRConstants;

/**
 * 
 */
@Service
public class IVRLfacsPagerTextFormation {

	private final ObjectMapper objectMapper;
	
	private final IVRLfacsServiceHelper serviceHelper;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(IVRLfacsPagerTextFormation.class);
	
	public IVRLfacsPagerTextFormation(@Lazy IVRLfacsServiceHelper serviceHelper, @Lazy ObjectMapper objectMapper) {
		
		this.serviceHelper = serviceHelper;
		this.objectMapper = objectMapper;
	}
	
	public String getPageCurrentAssignment(IVRWebHookResponseDto response, IVRUserSession session,
			CurrentAssignmentResponseDto currentAssignmentResponseDto) {

		String pagerText = "";

		List<LOOP> loopList = null;

		try {
			if (currentAssignmentResponseDto != null && currentAssignmentResponseDto.getReturnDataSet() != null
					&& currentAssignmentResponseDto.getReturnDataSet().getLoop() != null
					&& !currentAssignmentResponseDto.getReturnDataSet().getLoop().isEmpty()) {

				StringBuilder pageText = new StringBuilder();

				loopList = currentAssignmentResponseDto.getReturnDataSet().getLoop();

				TNInfoResponse tnInfoResponse = objectMapper.readValue(session.getLosDbResponse(),
						TNInfoResponse.class);

				String tn = tnInfoResponse != null ? tnInfoResponse.getTn() : "";

				if ((tnInfoResponse != null && StringUtils.isBlank(tnInfoResponse.getTn())) || tnInfoResponse == null
						|| (loopList.get(0).getSTAT() == "PCF")) {

					pageText.append(getPageNoCkt(session, loopList, tn));
					// TODO Send Notification
				} else if (isLineStateTransfer(loopList)) {

					String errorMsg = "This circuit is involved in a Line and Station Transfer which can not be handled by FAST.";
					String facsInqType = session.getFacsInqType();
					String serviceOrder = getServiceOrder(loopList);

					if (session != null && "TN".equalsIgnoreCase(facsInqType)) {

						CSLPage cslPage = new CSLPage("FASG", build12DigitTN(session, tnInfoResponse.getTn()), "", "", "", "", serviceOrder, "", errorMsg,
								facsInqType, "");

						pageText.append(cslPage.FormatError());
					} else {

						String cable = "";

						String pair = "";

						List<SEG> segList = loopList.get(0).getSEG();

						if (StringUtils.isEmpty(session.getSegmentRead()) && segList != null && segList.size() == 1) {

							cable = segList.get(0).getCA();

							pair = segList.get(0).getPR();
						} else if ("F1".equalsIgnoreCase(session.getSegmentRead()) && segList != null
								&& segList.size() == 2) {

							cable = segList.get(1).getCA();

							pair = segList.get(1).getPR();
						} else if ("F2".equalsIgnoreCase(session.getSegmentRead()) && segList != null
								&& segList.size() == 3) {

							cable = segList.get(2).getCA();

							pair = segList.get(2).getPR();
						}

						CSLPage cslPage = new CSLPage("FASG", "", cable, pair, "", "", serviceOrder, "", errorMsg,
								facsInqType, "");

						pageText.append(cslPage.FormatError());

						// TODO Send Notification
					}
				} else {

					String serviceOrder = getServiceOrder(loopList);
					
					List<IVRParameter> parameterList =  new ArrayList<IVRParameter>();
					
					serviceHelper.getCablePairStatus(currentAssignmentResponseDto, parameterList);
					
					String cablPairData = parameterList.isEmpty() ? null : parameterList.get(0).getData();
					
					if (serviceHelper.isSpecialCircuit(currentAssignmentResponseDto, null)
							&& !serviceHelper.isUdcCircuit(currentAssignmentResponseDto, null)) {

						pageText.append("special ").append("FASG");
					} else {

						if (tnInfoResponse != null && StringUtils.isNotBlank(tnInfoResponse.getTn())) {
							
							pageText.append(build12DigitTN(session, tnInfoResponse.getTn())).append(" ").append("FASG").append("|")
								.append(serviceHelper.getServiceAddress(currentAssignmentResponseDto));
						}
					}

					if (StringUtils.isNotBlank(serviceOrder)) {

						pageText.append(serviceOrder);
					}

					if ("CT".equalsIgnoreCase(cablPairData) || "CF".equalsIgnoreCase(cablPairData)
							|| "PCF".equalsIgnoreCase(cablPairData)) {

						pageText.append("|Status: ").append(cablPairData);
					}

					if ("P".equalsIgnoreCase(loopList.get(0).getSSC())) {

						pageText.append("|Warning: AML Circuit").append("|(Primary Pair)");
					} else if ("D".equalsIgnoreCase(loopList.get(0).getSSC())) {

						pageText.append("|Warning: AML Circuit").append("|(Derived Pair)");
					}

					if (isDPA(loopList)) {

						pageText.append("|Warning: DPA").append("|")
								.append(serviceHelper.getServiceAddress(currentAssignmentResponseDto)).append(" ");
					}

					pageText.append(segmentPageText(loopList));

					if (serviceHelper.isSpecialCircuit(currentAssignmentResponseDto, null)
							&& !serviceHelper.isUdcCircuit(currentAssignmentResponseDto, null)
							&& StringUtils.isNotBlank(loopList.get(0).getCKID())
							&& loopList.get(0).getCKID().indexOf("None") < 0) {

						pageText.append("|Full CKID name is: |").append(loopList.get(0).getCKID());
					}

					if (loopList.get(0).getSEG() != null && loopList.get(0).getSEG().size() > 3) {

						pageText.append(
								"Warning: Loop has more than 3 cable segments. Can't cut or get spares using FAST.");
					}
				}
//				List<IVRParameter> parameterList = new ArrayList<IVRParameter>();
//				
//				IVRParameter parameter = new IVRParameter();
//				
//				parameter.setData(pageText.toString());
//				
//				parameterList.add(parameter);
//				
//				response.setParameters(parameterList);
				pagerText = pageText.toString();
			}
		} catch (JsonProcessingException e) {

			pagerText = IVRConstants.FAILURE;
		} catch (Exception e) {

			pagerText = IVRConstants.FAILURE;
		}

		return pagerText;
	}

	private boolean isLineStateTransfer(List<LOOP> loopList) {

		if (loopList.get(0).getSO() != null && !loopList.get(0).getSO().isEmpty()
				&& loopList.get(0).getSO().get(0).getLSTFN() != null) {

			return Boolean.TRUE;
		}

		return Boolean.FALSE;
	}

	private String getPageNoCkt(IVRUserSession session, List<LOOP> loopList, String tn) {

		StringBuilder pageText = new StringBuilder();

		pageText.append("NONE").append(" FASG");

		if (session != null && "CP".equalsIgnoreCase(session.getFacsInqType())) {

			if (!loopList.get(0).getSEG().isEmpty()
					&& StringUtils.isNotBlank(loopList.get(0).getSEG().get(0).getCA())) {
				
				pageText.append("|ca: ").append(loopList.get(0).getSEG().get(0).getCA()).append(" pr: ").append(loopList.get(0).getSEG().get(0).getPR());
			}
		}

		pageText.append("|Text: ").append("|").append("|").append("|");

		return pageText.toString();
	}

	private String getServiceOrder(List<LOOP> loopList) {

		if (loopList.get(0).getSO() != null && !loopList.get(0).getSO().isEmpty()
				&& StringUtils.isNotBlank(loopList.get(0).getSO().get(0).getORD())) {

			return loopList.get(0).getSO().get(0).getORD();
		}
		return "";
	}

	private boolean isDPA(List<LOOP> loopList) {

		if (StringUtils.isNotBlank(loopList.get(0).getTID())
				&& loopList.get(0).getTID().toUpperCase().startsWith("DPA")) {

			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	private String segmentPageText(List<LOOP> loopList) {

		StringBuilder pageText = new StringBuilder();

		if (loopList.get(0).getSEG() != null && !loopList.get(0).getSEG().isEmpty()) {

			int segSize = loopList.get(0).getSEG().size();

			List<SEG> segList = loopList.get(0).getSEG();

			for (int i = 0; i < segSize; i++) {

				pageText.append("|f");

				if (i == 0) {

					pageText.append("1");
				} else if (i == 1) {

					pageText.append("2");
				} else if (i == 2) {

					pageText.append("3");
				}

				pageText.append(" ");

				SEG seg = segList.get(i);

				if (StringUtils.isNotBlank(seg.getCA())) {

					pageText.append(seg.getCA());
				}

				pageText.append("/");

				if (StringUtils.isNotBlank(seg.getPR())) {

					pageText.append(seg.getPR());
				}
				pageText.append(" ");

				if ((StringUtils.isNotBlank(seg.getTP()) || "BP".equalsIgnoreCase(seg.getTP())) && StringUtils.isNotBlank(seg.getBP()) && !"0".equalsIgnoreCase(seg.getBP())) {

					pageText.append("BP ").append(seg.getBP());
				} else if ((StringUtils.isNotBlank(seg.getTP()) || "CC".equalsIgnoreCase(seg.getTP())) && StringUtils.isNotBlank(seg.getBP()) && !"0".equalsIgnoreCase(seg.getBP())) {

					pageText.append(seg.getBP().replace("-", ""));
				}

				if (StringUtils.isNotBlank(seg.getOBP()) && !seg.getOBP().equalsIgnoreCase(seg.getPR())
						&& !seg.getOBP().equalsIgnoreCase(seg.getBP())) {

					pageText.append(" opc ");

					if (isCC(seg.getOBP().charAt(0))) {

						pageText.append(seg.getOBP().replace("-", ""));
					} else {

						pageText.append(seg.getBP());
					}
				}

				pageText.append("|").append(seg.getTEA());
			}
		}

		return pageText.toString();
	}

	public boolean isCC(char val)

	{
		if ((val >= 'A' && val <= 'Z') || (val == ',') || (val == '+') || (val == '-')) {

			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	
	public String build12DigitTN(IVRUserSession session, String tn) {
		
		CTelephone telephone = CTelephoneBuilder.newBuilder(session).setTelephone(tn).build();
		
		if(telephone != null) {
			
			StringBuilder _12DigitTN = new StringBuilder();
			
			return _12DigitTN.append(telephone.getNpa()).append(" ").append(telephone.getNxx()).append("-").append(telephone.getLineNumber()).toString();
		}
		
		return null;
	}
	
	public String getCentralOfficePageText(IVRWebHookResponseDto response, IVRUserSession session,
			CentralOfficeEquipmentResponseDto centralOffficeEquipmentResponse) throws JsonMappingException, JsonProcessingException {
		
		StringBuilder pageText = new StringBuilder();
		
		TNInfoResponse losDbResponse = serviceHelper
				.extractTNInfoFromLosDBResponse(session.getLosDbResponse());
		
		if(losDbResponse != null && losDbResponse.getTn() != null) {
			
			String teleNo = losDbResponse.getTn();
			
			CTelephone tn = CTelephoneBuilder.newBuilder(session).setTelephone(teleNo).build();
			
			pageText.append(tn.getNpa()).append(" ").append(tn.getNxx()).append("-").append(tn.getLineNumber());
		}
		
		if(centralOffficeEquipmentResponse != null && centralOffficeEquipmentResponse.getMessageStatus() != null && StringUtils.isNotBlank(centralOffficeEquipmentResponse.getMessageStatus().getErrorMessage())) {
			
			pageText.append(" OE |Cannot find OE: Message:");
			
			if(centralOffficeEquipmentResponse.getMessageStatus().getHostErrorList() != null && !centralOffficeEquipmentResponse.getMessageStatus().getHostErrorList().isEmpty()) {
				
				pageText.append(centralOffficeEquipmentResponse.getMessageStatus().getHostErrorList().get(0).getId());
			}
			
			return pageText.toString();
		}

		pageText.append(" OE | ");
		
		if(centralOffficeEquipmentResponse != null & centralOffficeEquipmentResponse.getReturnDataSet() != null) {
			
			CentralOfficeEquipmentReturnDataSet returnDataSet = centralOffficeEquipmentResponse.getReturnDataSet();
			
			if(StringUtils.isBlank(returnDataSet.getSwitchNetworkUnitId())) {
				
				pageText.append("Cannot find OE");
			} else {
				
				pageText.append(returnDataSet.getSwitchNetworkUnitId());
			}
			
			if(StringUtils.isNotBlank(returnDataSet.getCallReferenceValue())) {
				
				pageText.append("|").append(" CRV: ").append(returnDataSet.getCallReferenceValue().trim()) ;
			}
			
			if(StringUtils.isNotBlank(returnDataSet.getSwitchCentralOfficeDLCControllerId())) {
				
				pageText.append("|").append(returnDataSet.getSwitchCentralOfficeDLCControllerId().trim()) ;
			}
			
			if(StringUtils.isNotBlank(returnDataSet.getSwitchRemoteTerminalDLCControllerId())) {
				
				pageText.append("|").append(returnDataSet.getSwitchRemoteTerminalDLCControllerId().trim()) ;
			}
			
			if(StringUtils.isNotBlank(returnDataSet.getChannel())) {
				
				pageText.append("|").append(" CHNL: ").append(returnDataSet.getChannel().trim());
			}
			
			if(StringUtils.isNotBlank(returnDataSet.getCarrierControllerPort())) {
				
				pageText.append("|").append(" CCPT: ").append(returnDataSet.getCarrierControllerPort().trim());
			}
			
			if(StringUtils.isNotBlank(returnDataSet.getAccessId())) {
				
				pageText.append("|").append(" AID: ").append(returnDataSet.getAccessId().trim());
			}
		}
		return pageText.toString();
	}
	
	public String getPageMultipleAppearences(IVRUserSession session,
			CurrentAssignmentResponseDto currentAssignmentResponseDto, MultipleAppearanceResponseDto multipleAppearanceResponseDto, int segmentNumber ) throws JsonMappingException, JsonProcessingException {
		
		LOGGER.info("Constructing getPageMultipleAppearences...");
		StringBuilder pageText = new StringBuilder();
		String bpType = null;
		String bpcc = null;
		
		List<LOOP> loopList = currentAssignmentResponseDto.getReturnDataSet().getLoop();
		String serviceOrder = getServiceOrder(loopList);
		
		String stat = loopList.get(0).getSTAT();

		TNInfoResponse losDbResponse = objectMapper.readValue(session.getLosDbResponse(),
				TNInfoResponse.class);
		
		if(losDbResponse != null && losDbResponse.getTn() != null) {
			String teleNo = losDbResponse != null ? losDbResponse.getTn() : "";
			CTelephone tn = CTelephoneBuilder.newBuilder(session).setTelephone(teleNo).build();
			pageText.append(tn.getNpa()).append(" ").append(tn.getNxx()).append("-").append(tn.getLineNumber());
		} else {
			pageText.append("ca/pr   ");
		}
		
		pageText.append(" APP");
		
		if ((serviceOrder != null) && (!serviceOrder.equals("") )) {
			pageText.append("|");
			pageText.append(serviceOrder);
		}
		
		if ((stat != null) && (stat.equals("CT") || stat.equals("CF") || stat.equals("PCF")) ) {
			pageText.append("|Status: ");
			pageText.append(stat);
		}
	    
	    pageText.append("|");
	    
	    if (segmentNumber==1 || segmentNumber==2 || segmentNumber==3) {
	        pageText.append("f").append(segmentNumber).append(" ");
	    }	    
	    
		String cable = null;
		String cableUnitId = null;
		
		if (segmentNumber==0) {
			cable = session.getCable();
			cableUnitId = session.getPair();
		} else if (CurrentAssignmentUtils.isSEGNotEmpty(currentAssignmentResponseDto)) {

			// set cable name and cable units here
			cable = CurrentAssignmentUtils.getCABySegmentNumber(currentAssignmentResponseDto, segmentNumber);
			cableUnitId = CurrentAssignmentUtils.getCablePairBySegmentNumber(currentAssignmentResponseDto,
					segmentNumber);
			bpcc = CurrentAssignmentUtils.getBpCcBySegmentNumber(currentAssignmentResponseDto, segmentNumber);
			bpType = CurrentAssignmentUtils.getTpBySegmentNumber(currentAssignmentResponseDto, segmentNumber);
			

		}
		pageText.append(cable).append("/").append(cableUnitId);
		
		// segmentNumber is 0 this condition will not satisfy
			if (stat != null && !stat.equals("PCF") && bpType != null) {
				if (bpType.equals("BP")) {
					pageText.append(" BP ").append(bpcc);
				} else if (bpType.equals("CC")) {
					pageText.append(" ").append(bpcc.replace("-", ""));
				}
			}
	    
		if (isDPA(loopList)) {

			pageText.append("|Warning: DPA");
		}
		
		LOGGER.info("**********LOOP ******");
		
		if ((multipleAppearanceResponseDto.getReturnDataSet() != null) && (multipleAppearanceResponseDto.getReturnDataSet().getTerminalDetail() != null)) {
			for (int i = 0; i < multipleAppearanceResponseDto.getReturnDataSet().getTerminalDetail().size(); i++) {
			    if ((multipleAppearanceResponseDto.getReturnDataSet().getTerminalDetail().get(i).getTerminalAddress() != null) &&
			            (multipleAppearanceResponseDto.getReturnDataSet().getTerminalDetail().get(i).getTerminalType() != null)) 
			    {
			        pageText .append("|" + multipleAppearanceResponseDto.getReturnDataSet().getTerminalDetail().get(i).getTerminalAddress().trim());

			        pageText .append("|").append(multipleAppearanceResponseDto.getReturnDataSet().getTerminalDetail().get(i).getTerminalType().trim());
			        if (multipleAppearanceResponseDto.getReturnDataSet().getTerminalDetail().get(i).getCandidatePairStatus() != null)
			        	pageText .append("   ").append(multipleAppearanceResponseDto.getReturnDataSet().getTerminalDetail().get(i).getCandidatePairStatus());
			        if (multipleAppearanceResponseDto.getReturnDataSet().getTerminalDetail().get(i).getBindingPostColorCode() != null)
			        	pageText .append("   ").append(multipleAppearanceResponseDto.getReturnDataSet().getTerminalDetail().get(i).getBindingPostColorCode());
					}
				}
		}
		return pageText.toString();
	}
	
	

	public String formatDefectivePairsPagerTextForMobile(DefectivePairsResponseDto defectivePairsResponse, String reqCable, String reqPair) {
		StringBuilder pageText = new StringBuilder("DPR");
		pageText.append(" ca: ").append(reqCable).append(" pr: ").append(reqPair).append("|");

		Optional.ofNullable(defectivePairsResponse.getReturnDataSet().getDefectivePairDetails())
				.ifPresentOrElse(defectivePairDetailsList -> defectivePairDetailsList.stream().filter(Objects::nonNull)
						.forEach(defectivePairDetails -> {
							Optional.ofNullable(defectivePairDetails.getPairId())
									.ifPresent(pairId -> pageText.append(" ").append(pairId));

							Optional.ofNullable(defectivePairDetails.getDefectCode())
									.ifPresent(defectCode -> pageText.append(" ").append(defectCode));

							Optional.ofNullable(defectivePairDetails.getDefectStartDate())
									.ifPresent(defectStartDate -> pageText.append(" ").append(defectStartDate));

							Optional.ofNullable(defectivePairDetails.getPairStatus())
									.ifPresent(pairStatus -> pageText.append(" ").append(pairStatus));
							
							pageText.append(" | ");

						}), () -> pageText.append(" | No defective pairs in range"));

		pageText.append(defectivePairsResponse.getReturnDataSet().isAdditionalDefectivePairsFlag() ? " | More def pairs"
				: " No more pairs");
		return pageText.toString();

	}

	

	public String formatDefectivePairsPagerTextForEmail(DefectivePairsResponseDto defectivePairsReportResponse,
			DefectivePairsRequestDto defectivePairsReportRequest, String lastName) {
		StringBuilder pageText = new StringBuilder();
		//added upto 46 space, deduce the lastname length.
		
		pageText.append("Up To 14 Immediate Defective Pairs");

		pageText.append(" For Cable : ")
				.append(defectivePairsReportRequest.getInputData().getCableId()).append(" Pair : ")
				.append(defectivePairsReportRequest.getInputData().getCablePairRange().getLowPair()).append("\n");

		pageText.append("-------------------------------------------------------------------------------\n");
		pageText.append("     Pair     Defect Type     Date     Status\n");
		pageText.append("-------------------------------------------------------------------------------\n");

		int limit = Math.min(defectivePairsReportResponse.getReturnDataSet().getDefectivePairDetails().size(), 14);
		// Only print out a maximum of 14 defective pairs

		Optional.ofNullable(defectivePairsReportResponse.getReturnDataSet().getDefectivePairDetails())
				.ifPresentOrElse(defectivePairDetailsList -> defectivePairDetailsList.stream().limit(limit)
						.filter(Objects::nonNull).forEach(defectivePair -> {
							pageText.append("     ");
							Optional.ofNullable(defectivePair.getPairId()).ifPresentOrElse(
									pairId -> pageText.append(String.format("%-14s", pairId)),
									() -> pageText.append(String.format("%-14s", "")));

							Optional.ofNullable(defectivePair.getDefectCode())
									.ifPresent(defectCode -> pageText.append(defectCode));
							pageText.append("       ");
							Optional.ofNullable(defectivePair.getDefectStartDate())
									.ifPresent(defectStartDate -> pageText.append(defectStartDate));
							pageText.append("     ");
							Optional.ofNullable(defectivePair.getPairStatus())
									.ifPresent(pairStatus -> pageText.append(pairStatus));
							pageText.append("\n");

						}), () -> pageText.append("\nNo defective pairs available in this 100 count"));
		// Check if more defective pairs in the system or if there were more than 14
		// pairs returned
		if (defectivePairsReportResponse.getReturnDataSet().isAdditionalDefectivePairsFlag()
				|| defectivePairsReportResponse.getReturnDataSet().getDefectivePairDetails().size() > 14) {
			pageText.append("\nMore defective pairs available in this 100 count");
		} else {
			pageText.append("\nNo more defective pairs available in this 100 count");
		}
		return pageText.toString();
	}
	
	public String formatPage(String tn) {
		
		StringBuilder pageText = new StringBuilder();
		
		return pageText.append(tn).append(" ").append("Read This if you do a CUT you Must update the LFACS database either using options 3 on FAST -OR  by calling the Assigner").toString(); 
	}
}
