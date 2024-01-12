package com.lumen.fastivr.IVRCNF.Dto;

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
public class InputData {

	 @JsonProperty("WireCtrPrimaryNPANXX")
	 private WireCtrPrimaryNPANXX wireCtrPrimaryNPANXX;
	 
	 @JsonProperty("CircuitId")
	 private String circuitId;
	 
	 @JsonProperty("CableId")
	 private String cableId;
	 
	 @JsonProperty("CableUnitId")
	 private String cableUnitId;
}
