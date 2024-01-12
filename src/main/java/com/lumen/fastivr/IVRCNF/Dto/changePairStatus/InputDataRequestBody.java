package com.lumen.fastivr.IVRCNF.Dto.changePairStatus;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@ToString
public class InputDataRequestBody {

	@JsonProperty("InputData")
	private InputData inputData;
	
}
