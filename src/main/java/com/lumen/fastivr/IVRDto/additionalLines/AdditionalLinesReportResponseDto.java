package com.lumen.fastivr.IVRDto.additionalLines;

import java.util.List;

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
public class AdditionalLinesReportResponseDto extends BaseResponseDto {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@JsonProperty("ReturnDataSet")
	private List<String> returnDataSet;

}
