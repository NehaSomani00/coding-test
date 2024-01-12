package com.lumen.fastivr.IVRDto.multipleappearance;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@ToString
public class MultipleAppearanceRequestDto {


    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@JsonProperty("InputData")
    private MultipleAppearanceInputData inputData;
	
}
