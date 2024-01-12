package com.lumen.fastivr.IVRDto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@ToString
public class ADDR {
	
	
	@JsonProperty("ADDRNO")
	private String ADDRNO;
	
	@JsonProperty("BADR")
    private List<BADR> BADR;
  
	@JsonProperty("SUPL")
    private List<SUPL> SUPL;
	
	@JsonProperty("TEA")
    private String TEA;
	
	@JsonProperty("TP")
    private String TP;
	
	@JsonProperty("TEC")
    private String TEC;
	
	@JsonProperty("XRST")
    private String XRST;
	
	@JsonProperty("PTR")
    private String PTR;
	
	@JsonProperty("RT")
    private String RT;
	
	@JsonProperty("RZ")
    private String RZ;
	
	@JsonProperty("ICSW")
    private String ICSW;
	
	@JsonProperty("TYPE")
    private String TYPE;
	
	@JsonProperty("RSTTE")
    private String RSTTE;
	
	@JsonProperty("RSTLU")
    private String RSTLU;
	
	@JsonProperty("RMK0TE")
    private String RMK0TE;
	
	@JsonProperty("RMK0LU")
    private String RMK0LU;
	
	@JsonProperty("BSTE")
    private String BSTE;
	
	@JsonProperty("BSTE2")
    private String BSTE2;
	
	@JsonProperty("MISCLU")
    private String MISCLU;
	
	@JsonProperty("PNDLPS")
    private String PNDLPS;
	
	@JsonProperty("PNDORD")
    private List<PNDORD> PNDORD;

}
