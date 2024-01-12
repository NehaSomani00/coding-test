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
public class AssignServiceOrderResponseReturnDataSetCandidatePairInfo {
	
	 @JsonProperty("CableId")
	 private String cableId;
	 
	 @JsonProperty("CableUnitId")
	 private String cableUnitId;
	 
	@JsonProperty("BindingPostColorCode")
	private String bindingPostColorCode;
	
	@JsonProperty("Origin")
	private String origin;
	
	//public NameValuePairT CandidatePairSupplementaryData;
	@JsonProperty("CandidatePairSupplementaryData")
	private String candidatePairSupplementaryData;	

}