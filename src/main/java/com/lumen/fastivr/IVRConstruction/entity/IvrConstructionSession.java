package com.lumen.fastivr.IVRConstruction.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
//@Table(name = "",schema = "FASTIVR_OWNER")
public class IvrConstructionSession implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "SESSIONID")
	private String sessionId;
	
	
	@Column(name = "LAST_ACTIVE_SESSION")
	private LocalDateTime lastActiveSessionTime;

	@Column(name = "CONSTRUCTION_RESPONSE")
	private String constructionResponse;
}
