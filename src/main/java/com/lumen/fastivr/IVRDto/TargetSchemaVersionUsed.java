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
public class TargetSchemaVersionUsed {
	
	@JsonProperty("majorVersionNumber")
    private double majorVersionNumber;
	
	@JsonProperty("minorVersionNumber")
    private double minorVersionNumber;

}