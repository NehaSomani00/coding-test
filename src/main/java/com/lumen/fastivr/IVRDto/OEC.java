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
public class OEC {
	
	
	@JsonProperty("GRD")
	private String GRD;
	
	@JsonProperty("CLS")
	private String CLS;
	
	@JsonProperty("WIRES")
	private String WIRES;
	
	@JsonProperty("PGI")
	private String PGI;
	
	@JsonProperty("NLI")
	private String NLI;
	
	@JsonProperty("MI")
	private String MI;
	
	@JsonProperty("CTG")
	private String CTG;
	
	@JsonProperty("COTE")
	private String COTE;
	
	@JsonProperty("QUAL")
	private String QUAL;
	
	@JsonProperty("SIG")
	private String SIG;
	
	@JsonProperty("MET")
	private String MET;
	
	@JsonProperty("DDR")
	private String DDR;
	
	@JsonProperty("LATY")
	private String LATY;
	
	@JsonProperty("DSP")
	private String DSP;

}
