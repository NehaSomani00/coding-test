package com.lumen.fastivr.IVRDto.defectivepairs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@ToString
public class DefectivePairDetailsT {

	@JsonProperty("PairId")
	public String pairId;
    
	@JsonProperty("DefectCode")
	public String defectCode;

	@JsonProperty("DefectStartDate")
	public String defectStartDate;

	@JsonProperty("PairStatus")
	public String pairStatus;
}
