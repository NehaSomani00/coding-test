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
public class MISCLU {
	
		@JsonProperty("CANSO")
		private String CANSO;
		
		@JsonProperty("CANLI")
	    private String CANLI;
		
		@JsonProperty("LURSV")
	    private String LURSV;
		
		@JsonProperty("PNDELU")
	    private String PNDELU;
		
		@JsonProperty("RULE")
	    private String RULE;

}
