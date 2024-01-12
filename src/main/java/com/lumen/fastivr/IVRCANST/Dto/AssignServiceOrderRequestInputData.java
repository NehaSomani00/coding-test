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
public class AssignServiceOrderRequestInputData {

	@JsonProperty("LFACSEmployeeCode")
	private String lFACSEmployeeCode;
	
	@JsonProperty("LFACSEntityCode")
	private String lfacsEntityCode;	
	
	@JsonProperty("WireCtrPrimaryNPANXX")
	private WireCtrPrimaryNPANXX wireCtrPrimaryNPANXX;
	
	@JsonProperty("ServiceOrderNumber")
	private String serviceOrderNumber;
	
	@JsonProperty("CircuitId")
	private String circuitId;
	
	 @JsonProperty("TerminalId")
	 private String terminalId;	
}
