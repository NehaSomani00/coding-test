package com.lumen.fastivr.IVRDto.additionalLines;

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
public class AdditionalLinesReportInputData {
	
	@JsonProperty("WireCtrPrimaryNPANXX")
	private WireCtrPrimaryNPANXX wireCtrPrimaryNPANXX;
	
	@JsonProperty("LFACSEmployeeCode")
	private String lFACSEmployeeCode;
	
	@JsonProperty("ServiceAddressGeneric")
	private String serviceAddressGeneric;

}
