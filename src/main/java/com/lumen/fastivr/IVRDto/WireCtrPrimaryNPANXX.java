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
public class WireCtrPrimaryNPANXX {
	
	 @JsonProperty("NPA")
	 private String npa;

	 @JsonProperty("NXX")
	 private String nxx;


}
