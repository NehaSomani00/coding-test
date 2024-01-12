package com.lumen.fastivr.IVRDto.LOSDB;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TNInfoResponse {

	// @ApiModelProperty(value="ctlNetworkEntity", dataType="string")
	private String correlationId;
	private String ctlNetworkEntity;
	private String equipmentType;
	private String exchangeKey;
	private String horizontalCoord;
	private String hostSwitchCLLICode;
	private String lata;
	private String lataName;
	private String localityState;
	private String locationRoutingNumber;
	private String msaCode;
	private String msaName;
	private String npaState;
	private boolean ported;
	private String primaryNPA;
	private String primaryNXX;
	private boolean prismEligible;
	private String rateCenter;
	private String saga;
	private String serviceProviderId;
	private String serviceProviderName;
	private String serviceProviderTN;
	private String switchCLLICode;
	private String switchExchange;
	private String switchExchangeName;
	private String timeZone;
	private String tn;
	private String verticalCoord;
	private String wcAddress;
	private String wcCLLICode;
	private String wcLFACSCode;
	private String wcLFACSTypeCode;
	private String wcName;
	private String message;
}