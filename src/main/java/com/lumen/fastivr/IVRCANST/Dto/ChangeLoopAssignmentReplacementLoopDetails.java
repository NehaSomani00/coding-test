package com.lumen.fastivr.IVRCANST.Dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@ToString
public class ChangeLoopAssignmentReplacementLoopDetails {

	
	@JsonProperty("CableId")
	private String cableId;

	@JsonProperty("CableUnitId")
	private String cableUnitId;

	@JsonProperty("ReplacementTerminalId")
	private String replacementTerminalId;
	
	@JsonProperty("BindingPostColorCode")
	private String bindingPostColorCode;
	
//	@JsonProperty("ReplacementCablePairStatus")
//	private String replacementCablePairStatus;
//	
//	@JsonProperty("ReplacementBindingPostType")
//	private String replacementBindingPostType;
//	
//	@JsonProperty("ReplacementServiceAddress")
//	private String replacementServiceAddress;
//	
//	@JsonProperty("ReplacementUnit")
//	private String replacementUnit;
//	
//	@JsonProperty("ReplacementStructure")
//	private String replacementStructure;
//	
//	@JsonProperty("ReplacementElevation")
//	private String replacementElevation;
//
//	@JsonProperty("ReplacementCommunity")
//	private String replacementCommunity;
}
