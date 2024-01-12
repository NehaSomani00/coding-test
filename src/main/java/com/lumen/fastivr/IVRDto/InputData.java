package com.lumen.fastivr.IVRDto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

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

	 @JsonProperty("EMP")
	 private int emp;

	 @JsonProperty("CurrentAssignmentInfo")
	 private CurrentAssignmentInfo currentAssignmentInfo;


}
