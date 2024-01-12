package com.lumen.fastivr.IVRUtils;

import org.springframework.context.annotation.Description;

import com.lumen.fastivr.IVRDto.CurrentAssignmentResponseDto;

public class CurrentAssignmentUtils {
	
	public static Boolean isSEGNotEmpty(CurrentAssignmentResponseDto currentAssignmentResponseDto){
		Boolean isSEGEmpty = Boolean.FALSE;
		if (isLoopNotEmptyOrNull(currentAssignmentResponseDto)
				&& !currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG().isEmpty()) {
			isSEGEmpty = Boolean.TRUE;
		}
		return isSEGEmpty;
	}
	
	//If loop list within the currentAssignmentResponseDto -> ReturnDataSet is found empty then return false as it's empty
	public static Boolean isLoopNotEmptyOrNull(CurrentAssignmentResponseDto currentAssignmentResponseDto){
		
		Boolean isLoopEmptyOrNull = Boolean.FALSE;
		if(currentAssignmentResponseDto != null && currentAssignmentResponseDto.getReturnDataSet() != null
				&& currentAssignmentResponseDto.getReturnDataSet().getLoop() != null
				&& !currentAssignmentResponseDto.getReturnDataSet().getLoop().isEmpty()) {
			isLoopEmptyOrNull = Boolean.TRUE;
			
		}
		return isLoopEmptyOrNull;
	}

	@Description("Call isSEGNotEmpty(..) method before calling this method. This method will return Cable Name")
	public static String getCABySegmentNumber(CurrentAssignmentResponseDto currentAssignmentResponseDto,int segmentNumber) {
		return currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG().get(segmentNumber-1).getCA();
	}
	
	@Description("Call isSEGNotEmpty(..) method before calling this method. This method will return Cable Pair")
	public static String getCablePairBySegmentNumber(CurrentAssignmentResponseDto currentAssignmentResponseDto,int segmentNumber) {
		return currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG().get(segmentNumber-1).getPR();
	}
	
	@Description("Call isSEGNotEmpty(..) method before calling this method. This method will return BPCC value")
	public static String getBpCcBySegmentNumber(CurrentAssignmentResponseDto currentAssignmentResponseDto,int segmentNumber) {
		return currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG().get(segmentNumber-1).getBP();
	}
	
	@Description("Call isSEGNotEmpty(..) method before calling this method. This method will return TP value which is actually BPType")
	public static String getTpBySegmentNumber(CurrentAssignmentResponseDto currentAssignmentResponseDto,int segmentNumber) {
		return currentAssignmentResponseDto.getReturnDataSet().getLoop().get(0).getSEG().get(segmentNumber-1).getTP();
	}
}
