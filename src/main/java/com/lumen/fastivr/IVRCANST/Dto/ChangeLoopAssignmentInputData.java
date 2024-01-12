package com.lumen.fastivr.IVRCANST.Dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lumen.fastivr.IVRDto.WireCtrPrimaryNPANXX;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@ToString
public class ChangeLoopAssignmentInputData {

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
	private ChangeLoopAssignmentCurrentLoopDetails currentLoopDetails;	
	
	@JsonProperty("ReplacementLoopDetails")
	private ChangeLoopAssignmentReplacementLoopDetails replacementLoopDetails;	
	
	@JsonProperty("FacilityChangeReasonCode")
	private String facilityChangeReasonCode;
	
	@JsonProperty("SegmentNumber")
	private String SegNumber;
	
	@JsonProperty("SegmentNumberSpecified")
	private boolean segmentNumberSpecified;
	
	@JsonProperty("ChangeActionCode")
	private String changeActionCode;	

	@JsonProperty("CableTroubleTicketIdentifier")
	private String cableTroubleTicketIdentifier;	

	@JsonProperty("WiredOutOfLimit")
	private String wiredOutOfLimit;
	
	@JsonProperty("AutoSelectionFlag")
	private boolean autoSelectionFlag;
}
