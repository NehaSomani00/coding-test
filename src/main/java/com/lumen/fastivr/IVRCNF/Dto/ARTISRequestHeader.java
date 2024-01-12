package com.lumen.fastivr.IVRCNF.Dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@ToString
public class ARTISRequestHeader {

	@JsonProperty("ARTISCorrelationId")
	private String artisCorrelationId;
	
	@JsonProperty("HierarchyCalloutFlag")
	private boolean hierarchyCalloutFlag;
}
