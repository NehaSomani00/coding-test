package com.lumen.fastivr.IVRDto.multipleappearance;

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
public class MultipleAppearanceReturnDataSet {
	
	@JsonProperty("TerminalDetail")
	private List<TerminalDetail> terminalDetail;
	
	@JsonProperty("AdditionalTerminalsFlag")
    private boolean additionalTerminalsFlag;
	
}

