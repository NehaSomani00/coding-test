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
public class ChangeLoopAssignmentReturnDataSet {

	
	@JsonProperty("CircuitId")
	private String circuitId;
	
	@JsonProperty("TerminationId")
	private String terminationId;
	
	@JsonProperty("RequestStatus")
	private String requestStatus;
	
	@JsonProperty("TerminalId")
	private String terminalId;
	
	@JsonProperty("CandidatePairInfo")
	private List<ChangeLoopAssignmentCandidatePairInfo> changeLoopAssignmentCandidatePairInfo;	

}
