package com.lumen.fastivr.IVRCNF.utils;

import java.util.List;

import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.lumen.fastivr.IVRCNF.entity.IVRCnfEntity;
import com.lumen.fastivr.IVRDto.CurrentAssignmentResponseDto;
import com.lumen.fastivr.IVRDto.LOOP;
import com.lumen.fastivr.IVRDto.SEG;

@Service
public class IVRCnfUtilities {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(IVRCnfUtilities.class);
	
	public IVRCnfUtilities() {

	}
	
	
	/* ******************************************************************************************
	 * 	Below are the utility methods used in CNF or Can be used all across FAST-IVR
	 * 
	 ********************************************************************************************/

	public String getCablePairStatus(CurrentAssignmentResponseDto currentAssignmentResponse) {

		String cablePairStatus = "";

		if ((currentAssignmentResponse.getReturnDataSet() != null) &&
				(currentAssignmentResponse.getReturnDataSet().getLoop() != null) &&
				(currentAssignmentResponse.getReturnDataSet().getLoop().size() > 0) &&
				(currentAssignmentResponse.getReturnDataSet().getLoop().get(0).getSTAT() != null)) {

			cablePairStatus = currentAssignmentResponse.getReturnDataSet().getLoop().get(0).getSTAT();

		} else if ((currentAssignmentResponse.getReturnDataSet() != null) &&
				(currentAssignmentResponse.getReturnDataSet().getLoop() != null) &&
				(currentAssignmentResponse.getReturnDataSet().getLoop().size() > 0) &&
				(currentAssignmentResponse.getReturnDataSet().getLoop().get(0).getSEG() != null) &&
				(currentAssignmentResponse.getReturnDataSet().getLoop().get(0).getSEG().get(0) != null) &&
				(currentAssignmentResponse.getReturnDataSet().getLoop().get(0).getSEG().get(0).getLSTAT() != null)) {

			cablePairStatus = currentAssignmentResponse.getReturnDataSet().getLoop().get(0).getSEG().get(0).getLSTAT(); 
		}

		return cablePairStatus;
	} 

	public String getServiceOrder(List<LOOP> loopList) {

		if (loopList.get(0).getSO() != null && !loopList.get(0).getSO().isEmpty()
				&& StringUtils.isNotBlank(loopList.get(0).getSO().get(0).getORD())) {

			return loopList.get(0).getSO().get(0).getORD();
		}
		return "";
	}

	public String getCablePair(List<LOOP> loopList) {

		if (loopList.get(0).getSEG() != null && !loopList.get(0).getSEG().isEmpty()
				&& StringUtils.isNotBlank(loopList.get(0).getSEG().get(0).getPR())) {

			return loopList.get(0).getSEG().get(0).getPR();
		}
		return "";
	}	

	/**
	 * @param segList
	 */
	public String getCablePairFromSegment(List<SEG> segList) {
		
		String cablePair = null;
		if (segList != null && !segList.isEmpty()
				&& StringUtils.isNotBlank(segList.get(0).getPR())) {

			cablePair = segList.get(0).getPR();

		}
		return cablePair;
	}

	/**
	 * @param segList
	 */
	public String getCableFromSegment(List<SEG> segList) {
		
		String cableFromSegment = null;
		if (segList != null && !segList.isEmpty()
				&& StringUtils.isNotBlank(segList.get(0).getCA())) {

			cableFromSegment = segList.get(0).getCA();

		}
		return cableFromSegment;
	}	

	public int getSegmentNumber(IVRCnfEntity cnfSession) {
		
		String segmentRead = cnfSession.getSegmentRead();
		int segmentNumber = 0;
		if ("F1".equalsIgnoreCase(segmentRead)) {
			
		} else if ("F2".equalsIgnoreCase(segmentRead)) {
		
			segmentNumber = 1;
		} else if ("F3".equalsIgnoreCase(segmentRead)) {
		
			segmentNumber = 2;
		}
		return segmentNumber;
	}

	public List<SEG> getSegmentList(CurrentAssignmentResponseDto currentAssignmentResponseDto) {
		
		List<SEG> segList = null;
		
		if (currentAssignmentResponseDto != null && currentAssignmentResponseDto.getReturnDataSet() != null
				&& currentAssignmentResponseDto.getReturnDataSet().getLoop() != null
				&& !currentAssignmentResponseDto.getReturnDataSet().getLoop().isEmpty()) {

			if(currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG() != null && !currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG().isEmpty()) {
				
				segList = currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG();
			} else  if(currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO() != null && !currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().isEmpty() 
					&& currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().get(0).getSEG() != null && !currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().get(0).getSEG().isEmpty()) {
				
				segList = currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSO().get(0).getSEG();
			}
			return segList;
		}
		return null;
	}	

	public String getServiceAddress(CurrentAssignmentResponseDto currentAssignmentResponseDto) {

		String serviceAddress = "";

		// Get the street number and street name
		if ((currentAssignmentResponseDto.getReturnDataSet().getLoop().size() > 0) &&
				(currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getADDR() != null) &&
				(currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getADDR().size() > 0) &&
				(currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getADDR().get(0).getBADR() != null) &&
				(currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getADDR().get(0).getBADR().size() > 0) &&
				(currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getADDR().get(0).getBADR().get(0) != null)) {

			if (currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getADDR().get(0).getBADR().get(0).getBAD() != null) {

				serviceAddress += currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getADDR().get(0).getBADR().get(0).getBAD();
			}

			if (currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getADDR().get(0).getBADR().get(0).getSTR() != null) {

				serviceAddress += " " + currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getADDR().get(0).getBADR().get(0).getSTR();
			}

			if (currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getADDR().get(0).getSUPL() != null) {

				if ((currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getADDR().get(0).getSUPL().get(0).getUTYP() != null) &&
						(!currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getADDR().get(0).getSUPL().get(0).getUTYP().equals(""))) {

					serviceAddress += " " + currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getADDR().get(0).getSUPL().get(0).getUTYP();
				}

				if ((currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getADDR().get(0).getSUPL().get(0).getUID() != null) &&
						(!currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getADDR().get(0).getSUPL().get(0).getUID().equals(""))) {

					serviceAddress += " " + currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getADDR().get(0).getSUPL().get(0).getUID();
				}

				if ((currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getADDR().get(0).getSUPL().get(0).getSTYP() != null) &&
						(!currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getADDR().get(0).getSUPL().get(0).getSTYP().equals(""))) {

					serviceAddress += " " + currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getADDR().get(0).getSUPL().get(0).getSTYP();
				}

				if ((currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getADDR().get(0).getSUPL().get(0).getSID() != null) &&
						(!currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getADDR().get(0).getSUPL().get(0).getSID().equals(""))) {

					serviceAddress += " " + currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getADDR().get(0).getSUPL().get(0).getSID();
				}

				if ((currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getADDR().get(0).getSUPL().get(0).getETYP() != null) &&
						(!currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getADDR().get(0).getSUPL().get(0).getETYP().equals(""))) {

					serviceAddress += " " + currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getADDR().get(0).getSUPL().get(0).getETYP();
				}

				if ((currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getADDR().get(0).getSUPL().get(0).getEID() != null) &&
						(!currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getADDR().get(0).getSUPL().get(0).getEID().equals(""))) {

					serviceAddress += " " + currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getADDR().get(0).getSUPL().get(0).getEID();
				}
			}
		}

		return serviceAddress;
	}	

	public String getBpType(int segment, CurrentAssignmentResponseDto currentAssignmentResponse)
	{
		LOGGER.info("GetBpType: Segment = " + segment);
		String bpType = "";

		if ((currentAssignmentResponse.getReturnDataSet().getLoop() != null) &&
				(currentAssignmentResponse.getReturnDataSet().getLoop().get(0).getSEG() != null) &&
				(currentAssignmentResponse.getReturnDataSet().getLoop().get(0).getSEG().get(segment-1) != null) &&
				(currentAssignmentResponse.getReturnDataSet().getLoop().get(0).getSEG().get(segment-1).getTP() != null)) {

			bpType = currentAssignmentResponse.getReturnDataSet().getLoop().get(0).getSEG().get(segment-1).getTP();

		} 

		LOGGER.info("Current bpcc type = -" + bpType + "-");

		return bpType;
	}
}
