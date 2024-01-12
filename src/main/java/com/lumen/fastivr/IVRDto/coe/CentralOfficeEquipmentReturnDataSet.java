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
public class CentralOfficeEquipmentReturnDataSet {

	@JsonProperty("SWITCHNetworkUnitId")
	private String switchNetworkUnitId;
	
	@JsonProperty("CallReferenceValue")
	private String callReferenceValue;
	
	@JsonProperty("SWITCHCentralOfficeDLCControllerId")
	private String switchCentralOfficeDLCControllerId;
	
	@JsonProperty("SWITCHRemoteTerminalDLCControllerId")
	private String switchRemoteTerminalDLCControllerId;
	
	@JsonProperty("Channel")
	private String channel;
	
	@JsonProperty("CarrierControllerPort")
	private String carrierControllerPort;
	
	@JsonProperty("AccessId")
	private String accessId;
}
