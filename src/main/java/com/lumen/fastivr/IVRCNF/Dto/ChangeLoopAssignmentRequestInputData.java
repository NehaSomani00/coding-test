package com.lumen.fastivr.IVRCNF.Dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lumen.fastivr.IVRDto.WireCtrPrimaryNPANXX;
import com.lumen.fastivr.IVRDto.defectivepairs.CablePairRange;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@ToString
public class ChangeLoopAssignmentRequestInputData {
	
	
	@JsonProperty("LFACSEmployeeCode")
	private String lFACSEmployeeCode;
	
	@JsonProperty("LFACSEntity")
	private String lfacsEntity;	
	
	@JsonProperty("WireCtrPrimaryNPANXX")
	private WireCtrPrimaryNPANXX wireCtrPrimaryNPANXX;
	
	@JsonProperty("ServiceOrderNumber")
	private String serviceOrderNumber;
	
	@JsonProperty("CircuitId")
	private String circuitId;
	
	@JsonProperty("CurrentLoopDetails")
	private ChangeLoopAssignmentReqInputDataCurrentLoopDetails changeLoopAssignmentReqInputDataCurrentLoopDetails;	
	
	@JsonProperty("ReplacementLoopDetails")
	private ChangeLoopAssignmentReqInputDataReplacementLoopDetails changeLoopAssignmentReqInputDataReplacementLoopDetails;	
	
	@JsonProperty("FacilityChangeReasonCode")
	private String facilityChangeReasonCode;
	
//	@JsonProperty("ChangeLoopAssignmentReqInputDataSegmentNumber")
//	private ChangeLoopAssignmentReqInputDataSegmentNumber changeLoopAssignmentReqInputDataSegmentNumber;
	@JsonProperty("SegNumber")
	private String SegNumber;
	
	@JsonProperty("SegmentNumberSpecified")
	private boolean segmentNumberSpecified;
   
	
	//TODO:  Below  4 new fields
	
	@JsonProperty("ChangeActionCode")
	private String changeActionCode;	

	@JsonProperty("CableTroubleTicketIdentifier")
	private String cableTroubleTicketIdentifier;	

	@JsonProperty("WiredOutOfLimit")
	private String wiredOutOfLimit;
	
	@JsonProperty("AutoSelectionFlag")
	private boolean autoSelectionFlag;
	
}
