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
public class SUPL {
	
		@JsonProperty("STYP")
	 	private String STYP;
		
		@JsonProperty("SID")
	    private String SID;
		
		@JsonProperty("ETYP")
	    private String ETYP;
		
		@JsonProperty("EID")
	    private String EID;
		
		@JsonProperty("UTYP")
	    private String UTYP;
		
		@JsonProperty("UID")
	    private String UID;
	    

}
