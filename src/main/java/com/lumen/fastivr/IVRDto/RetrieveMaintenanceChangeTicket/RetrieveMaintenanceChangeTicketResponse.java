package com.lumen.fastivr.IVRDto.RetrieveMaintenanceChangeTicket;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lumen.fastivr.IVRDto.ARTISInformation;
import com.lumen.fastivr.IVRDto.MessageStatus;
import com.lumen.fastivr.IVRDto.TargetSchemaVersionUsed;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;



@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@ToString
public class RetrieveMaintenanceChangeTicketResponse {

	 	@JsonProperty("ReturnDataSet")
	 	private RetMaintChangeTicketReturnDataSet returnDataSet;
	 
	 	@JsonProperty("RequestId")
	    private String requestId;
		
		@JsonProperty("WebServiceName")
	    private String webServiceName;
		
		@JsonProperty("TargetSchemaVersionUsed")
	    private TargetSchemaVersionUsed targetSchemaVersionUsed;
		
		@JsonProperty("MessageStatus")
	    private MessageStatus messageStatus;
		
		@JsonProperty("ARTISInformation")
	    private ARTISInformation artisInformation;
		
		@JsonProperty("CompletedTimeStamp")
	    private String completedTimeStamp;
		
		@JsonProperty("CompletedTimeStampSpecified")
	    private boolean completedTimeStampSpecified;
}
