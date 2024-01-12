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
public class CurrentAssignmentInfo {
	
	 @JsonProperty("TN")
	 private TN tn;

	 @JsonProperty("CablePair")
	 private CablePair cablePair;
}
