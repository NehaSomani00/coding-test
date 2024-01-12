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
public class ChangeLoopAssignmentCurrentLoopDetails {

	
	@JsonProperty("CableId")
	private String cableId;

	@JsonProperty("CableUnitId")
	private String cableUnitId;

	@JsonProperty("TerminalId")
	private String terminalId;
	
//	@JsonProperty("StreetNumber")
//	private String streetNumber;
//
//	@JsonProperty("Unit")
//	private String unit;
//
//	@JsonProperty("CurrentStructure")
//	private String currentStructure;
//	
//	@JsonProperty("CurrentElevation")
//	private String currentElevation;
//
//	@JsonProperty("CurrentCommunity")
//	private String currentCommunity;
}
