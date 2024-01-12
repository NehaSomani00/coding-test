package com.lumen.fastivr.IVRCANST.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "CANST_SESSION",schema = "FASTIVR_OWNER")
public class IVRCanstEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "SESSION_ID")
	private String sessionId;
	
	@Column(name = "CANST_INQ_TYPE")
	private String canstInqType;
	
	@Column(name = "SEGMENT_READ")
	private String segmentRead;
	
	@Column(name = "CABLE_NUMBER")
	private String cable;
	
	@Column(name = "PAIR_NUMBER")
	private String pair;
	
	@Column(name = "LAST_ACTIVE_SESSION")
	private LocalDateTime lastActiveSessionTime;

	@Column(name = "SERVICE_ORDER_NO")
	private String serviceOrderNo;
	
	@Column(name = "ORDER_STATUS_RESP")
	private String orderStatusResp;
	
	@Column(name = "AOS_RESP")
	private String assignOrderServiceResp;
	
	@Column(name = "CNANGE_SER_TERM_RESP")
	private String changeServTermResp;

	@Column(name = "UPDATE_LOOP_RESP")
	private String updateLoopResp;
	
	@Column(name = "OLD_TEA")
	private String oldTea;
	
	@Column(name = "NEW_TEA")
	private String newTea;
	
	@Column(name = "CABLE_TROUBLE_TICKET")
	private String troubleTicketNo;
	
	@Column(name = "INQUIRY_TN")
	private String inquiryTn;
}
