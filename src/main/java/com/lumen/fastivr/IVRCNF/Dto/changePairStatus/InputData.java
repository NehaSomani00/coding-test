package com.lumen.fastivr.IVRCNF.Dto.changePairStatus;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lumen.fastivr.IVRDto.WireCtrPrimaryNPANXX;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@ToString
public class InputData {

	@JsonProperty("EMP")
	private String emp;
	
	@JsonProperty("WireCtrPrimaryNPANXX")
	private WireCtrPrimaryNPANXX wireCtrPrimaryNPANXX;
	
	@JsonProperty("CA")
	private String ca;
	
	@JsonProperty("PR")
	private String pr;
	
	@JsonProperty("DEFTP")
	private String defTP;
	
	/*
	 * @JsonProperty("DATE") private String date;
	 * 
	 * @JsonProperty("CTT") private String ctt;
	 */
}
