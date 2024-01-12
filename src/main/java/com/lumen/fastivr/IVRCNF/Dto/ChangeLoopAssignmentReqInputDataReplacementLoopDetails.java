package com.lumen.fastivr.IVRCNF.Dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@ToString
public class ChangeLoopAssignmentReqInputDataReplacementLoopDetails {
	
	@JsonProperty("CableId")
	private String cableId;

	@JsonProperty("CableUnitId")
	private String cableUnitId;

	//TODO New field 
	@JsonProperty("ReplacementTerminalId")
	private String replacementTerminalId;
	
	@JsonProperty("BindingPostColorCode")
	private String bindingPostColorCode;

}
