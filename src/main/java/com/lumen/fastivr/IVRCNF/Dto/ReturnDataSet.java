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
public class ReturnDataSet {

	@JsonProperty("MatchFoundFlag")
	private boolean matchFoundFlag;
	
	@JsonProperty("SWITCHNetworkUnitId")
	private String swithNetworkUnitId;
}
