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
public class LSTFN {

	@JsonProperty("LST")
	private String LST;
	
	@JsonProperty("ITM")
	private String ITM;
	
	@JsonProperty("CKID")
	private String CKID;
	
	@JsonProperty("FRCA")
	private String FRCA;
	
	@JsonProperty("FRPR")
	private String FRPR;
	
	@JsonProperty("FBP")
	private String FBP;
	
	@JsonProperty("TOCA")
	private String TOCA;
	
	@JsonProperty("TOPR")
	private String TOPR;
	
	@JsonProperty("TBP")
	private String TBP;
	
	@JsonProperty("FRLSTTE")
	private String FRLSTTE;
	
	@JsonProperty("TOLSTTE")
	private String TOLSTTE;
}
