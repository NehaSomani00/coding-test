package com.lumen.fastivr.IVRDto.multipleappearance;

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
public class MultipleAppearanceInputData {
	
	@JsonProperty("EmployeeId")
	private String employeeId;
	
	@JsonProperty("LFACSEntity")
	private String lfacsEntity;
	
	@JsonProperty("CableId")
	private String cableId;
	
	@JsonProperty("CableUnitId")
	private String cableUnitId;
	
	@JsonProperty("WireCtrPrimaryNPANXX")
	private WireCtrPrimaryNPANXX wireCtrPrimaryNPANXX;

}
