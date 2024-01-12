package com.lumen.fastivr.IVRCANST.Dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@ToString
public class AssignServiceOrderResponseReturnDataSet {

	@JsonProperty("CircuitId")
	private String circuitId;
	
	@JsonProperty("AssignServiceOrderResponseReturnDataSetCandidatePairInfo")
	private List<String> assignServiceOrderResponseReturnDataSetCandidatePairInfo;	
	
	// TODO New field   ??
	@JsonProperty("RequestStatus")
	private String requestStatus;
}
