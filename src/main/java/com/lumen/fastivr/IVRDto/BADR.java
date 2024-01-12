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
public class BADR {
	
	
	  @JsonProperty("BAD")
	  private String BAD;
	  
	  @JsonProperty("STR")
      private String STR;
	  
	  @JsonProperty("CNA")
      private String CNA;
	  
	  @JsonProperty("STN")
      private String STN;
  
     

}
