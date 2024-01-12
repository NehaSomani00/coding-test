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
public class ServiceLocation {
	
	@JsonProperty("BasicAddress")
	private BasicAddress basicAddress;

}
