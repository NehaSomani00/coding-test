package com.lumen.fastivr.IVRDto.retrieveLoopAssignment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@ToString
public class LoopAssignCandidatePairInfo {
	
	
	@JsonProperty("PairSelectionField")
	private String pairSelectionField;
    
	@JsonProperty("CandidatePairStatus")
	private String candidatePairStatus;
	
	 @JsonProperty("CableId")
	 private String cableId;

	 @JsonProperty("CableUnitId")
	 private String cableUnitId;

	@JsonProperty("Origin")
	private String origin;
    
	@JsonProperty("BindingPostColorCode")
	private String bindingPostColorCode;
	
	@JsonProperty("CandidatePairSupplementaryData")
	private String candidatePairSupplementaryData;


}
