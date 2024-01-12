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
public class AssignServiceOrderRequest {

	private static final long serialVersionUID = 1L;
	
	@JsonProperty("InputData")
	private AssignServiceOrderRequestInputData inputData;
}