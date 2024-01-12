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
public class SvcAddress {

	@JsonProperty("Item")
	private String item;
	
	@JsonProperty("ItemElementName")
	private String itemElementName;
	
	@JsonProperty("Unit")
	private Unit unit;
	
	@JsonProperty("Elevation")
	private Elevation elevation;
	
	@JsonProperty("Structure")
	private Structure structure;
	
	@JsonProperty("City")
	private String city;
	
	@JsonProperty("StateProvince")
	private String stateProvince;
	
	@JsonProperty("StateProvinceSpecified")
	private String stateProvinceSpecified;
	
	@JsonProperty("WireCtrCLLICode")
	private String wireCtrCLLICode;
}
