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
public class MainChangeTicketLoopInputRequest {
	
	 @JsonProperty("EmployeeId")
	 private String EmployeeId;
	
	 @JsonProperty("WireCtrPrimaryNPANXX")
	 private WireCtrPrimaryNPANXX wireCtrPrimaryNPANXX;
	 
	 @JsonProperty("SegNumber")
	 private String SegNumber;
	 
	 @JsonProperty("CurrentLoopDetails")
	 private CurrentLoopDetails CurrentLoopDetails;
	 
	 @JsonProperty("ReplacementLoopDetails")
	 private ReplacementLoopDetails ReplacementLoopDetails;
}

