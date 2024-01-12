package com.lumen.fastivr.IVRCNF.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "CNF_SESSION",schema = "FASTIVR_OWNER")
public class IVRCnfEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "SESSION_ID")
	private String sessionId;
	
	@Column(name = "CNF_INQ_TYPE")
	private String cnfInqType;
	
	@Column(name = "SEGMENT_READ")
	private String segmentRead;
	
	@Column(name = "CABLE_NUMBER")
	private String cable;
	
	@Column(name = "PAIR_NUMBER")
	private String pair;
	
	@Column(name = "IRP_RESP")
	private String getInstReplPrsResponse;
	
	@Column(name = "MRP_RESP")
	private String getMntReplPrsResponse;
	
	@Column(name = "LOOPQUAL_NII_RESP")
	private String getLoopQualNIIResponse;
	
	@Column(name = "VERIFY_CUT_RESP")
	private String getVerifyCutResponse;
	
	@Column(name = "LAST_ACTIVE_SESSION")
	private LocalDateTime lastActiveSessionTime;

	@Column(name = "SERVICE_ORDER_NO")
	private String serviceOrderNo;
}
