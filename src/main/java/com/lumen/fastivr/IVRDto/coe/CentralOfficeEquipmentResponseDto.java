package com.lumen.fastivr.IVRDto.coe;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lumen.fastivr.IVRDto.common.BaseResponseDto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@ToString
public class CentralOfficeEquipmentResponseDto extends BaseResponseDto{/**
	 * 
	 */
	private static final long serialVersionUID = -5376745031679229895L;
	
	@JsonProperty("ReturnDataSet")
	private CentralOfficeEquipmentReturnDataSet returnDataSet;

}
