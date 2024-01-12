package com.lumen.fastivr.IVRMLT.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "MLT_SESSION")
public class IvrMltSession implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "SESSIONID")
	private String sessionId;
	
	@Column(name = "TECH_ON_SAME_LINE")
	private Boolean techOnSameLine;
	
	@Column(name = "LAST_ACTIVE_SESSION")
	private LocalDateTime lastActiveSessionTime;
	
	@Column(name = "TEST_TYPE")
	private String testType;
	
	@Column(name = "OVERRIDE")
	private String override;
	
	@Column(name = "NPA_PREFIX")
	private String npaPrefix;
	
	@Column(name = "INQUIRY_TN")
	private String inquiredTn;
	
	@Column(name = "MLT_RSLT_CLOB")
	@Lob  @Basic(fetch=FetchType.LAZY)
	private String mltTestResult;
	
	@Column(name = "DATA_CHN_ID")
	private String datachannelId;
	
	@Column(name = "DATA_CHANNEL_PROXY")
	private String dataChannelProxyUrl;
	
	@Column(name = "TEST_REQUEST_PROXY")
	private String testRequestProxyUrl;
	
	@Column(name = "TONE_DURATION")
	private int toneDuration;
	
	public boolean getTechOnSameLine() {
		return techOnSameLine != null ? techOnSameLine.booleanValue() : false;
	}

}
