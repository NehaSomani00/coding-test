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
public class AddressDesignator {

	 @JsonProperty("Name")
	 private String name;
	 
	 @JsonProperty("NameSpecified")
	 private boolean nameSpecified;
	 
	 @JsonProperty("Value")
	 private String value;
}
