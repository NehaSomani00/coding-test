package com.lumen.fastivr.IVRWebHookEvent;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GenesysCloudWebhookEvent {
	
	@JsonProperty("session_id")
    private String sessionId;
	@JsonProperty("current_state")
    private String currentState; // Which state Genesys is now in
	@JsonProperty("user_dtmf_inputs")
    private String userDtmfInputs; // DTMF input received from the caller (e.g., "1", "2", etc.)
    

    // Add other relevant properties as needed
    // ...
    
    // Getters and setters
    // ...
    
}
