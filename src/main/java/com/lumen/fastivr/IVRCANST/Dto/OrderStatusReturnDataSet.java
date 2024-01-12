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
public class OrderStatusReturnDataSet {

	@JsonProperty("LFACSMode")
	private String lfacsMode;

	@JsonProperty("WireCtrPrimaryNPANXX")
	private WireCtrPrimaryNPANXX wireCtrPrimaryNPANXX;

	@JsonProperty("LoopAssignmentStatus")
	private String loopAssignmentStatus;

	@JsonProperty("AssignmentSectionPendingFlag")
	private String assignmentSectionPendingFlag;
}
