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
public class CablePairRange {

	@JsonProperty("LowPair")
	public int lowPair;
    
	@JsonProperty("LowPairSpecified")
	public boolean lowPairSpecified;

	@JsonProperty("HighPair")
	public int highPair;

	@JsonProperty("HighPairSpecified")
	public boolean highPairSpecified;
}
