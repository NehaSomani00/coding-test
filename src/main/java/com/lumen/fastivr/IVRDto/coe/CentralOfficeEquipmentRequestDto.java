package com.lumen.fastivr.IVRDto.coe;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@ToString
public class CentralOfficeEquipmentRequestDto {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@JsonProperty("InputData")
    private CentralOfficeEquipmentInputData inputData;
}
