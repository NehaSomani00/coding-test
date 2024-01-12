package com.lumen.fastivr.IVRSessionManagement;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import lombok.ToString;

@Entity
@Table(name = "TECHNICIAN_SESSION")
@ToString
public class IVRUserSession implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "SESSIONID")
	private String sessionId;
	
	//TODO: Add this column in database
	@Column(name = "NEW_SESSION_FLAG")
	private Boolean isNewSession;
	
	@Column(name = "CUID")
	private String cuid;
	
	@Column(name = "EMPID")
	private String empID;
	
	@Column(name = "AUTHENTICATED_FLAG")
	private Boolean isAuthenticated;
	
	@Column(name = "PASSWORD_ATTEMPT_COUNTER")
	private Integer passwordAttemptCounter;
	
	@Column(name = "BIRTHDATE")
	private String birthdate;
	
	@Column(name = "NEW_BIRTHDATE")
	private String newBirthDate;
	
	@Column(name = "JEOPARDY_FLAG")
	private Boolean loginJeopardyFlag;
	
	@Column(name = "ENABLED_FLAG" )
	private Boolean isEnabledFlag;
	
	@Column(name = "NEW_PASSWORD")
	private String newPassword;
	
	@Column(name = "OTP")
	private String otpGenerated;
	
	@Column(name = "CAN_BE_PAGED_MOBILE")
	private Boolean canBePagedMobile;
	
	@Column(name = "CAN_BE_PAGED_EMAIL")
	private Boolean canBePagedEmail;
	
	@Column(name = "NPA_PREFIX")
	private String npaPrefix;
	
	@Column(name = "CUT_PAGE_SENT")
	private Boolean cutPageSent;
	
	@Column(name = "EC" )
	private String ec;
	
	@Column(name = "MC")
	private String mc;
	
	@Column(name = "PASSWORD_EXPIRE_DATE")
	private LocalDate passwordExpireDate;
	
	@Column(name = "AGE")
	private Integer age;
	
	@Column(name = "SPLICER_FLAG")
	private Boolean isTechASplicer;
	
	@Column(name = "DEBUG_FLAG")
	private Boolean isDebugFlag;
	
	@Column(name = "PAGERCO")
	private String pagerCo;
	
	@Column(name = "LAST_ACTIVE_SESSION")
	private LocalDateTime lastActiveSessionTime;

//	@Column(name = "GETTNINFO_RESPONSE")
//	private String getTNInfoResponse;
	
	@Column(name = "OFFICE_EQUIPMENT_RESPONSE")
	private String centralOfficeEquipmentResponse;
	
	@Column(name = "DEFECTIVE_PAIR_RESPONSE")
	private String defectivePairResponse;
	
	@Column(name = "MULTIPLE_APPEARANCE_RESPONSE")
	private String multipleAppearanceResponse;
	
	@Column(name = "ADDITONAL_LINES_RESPONSE")
	private String additionalLinesResponse;

	@Column(name = "SEGMENT_READ")
	private String segmentRead;

	@Column(name = "LOSDB_RESPONSE")
	private String losDbResponse;
	
	@Column(name = "CURR_ASSG_RESP_CLOB")
	@Lob @Basic(fetch=FetchType.LAZY)
	private String currentAssignmentResponse;
	
	@Column(name = "FACS_INQ_TYPE")
	private String facsInqType;
	
	@Column(name = "ADDL_LINES_COUNTER")
	private Integer additionalLinesCounter;
	
	@Column(name = "CAND_PAIR_COUNTER")
	private Integer candPairCounter;
	
	@Column(name = "RTRV_MAINT_CHNGE_MSG_NAME")
	private String rtrvMaintChngeMsgName;
	
	@Column(name = "RTRV_LOOP_ASSG_MSG_NAME")
	private String rtrvLoopAssgMsgName;
	
	@Column(name = "CABLE_NUMBER")
	private String cable;
	
	@Column(name = "PAIR_NUMBER")
	private String pair;
	
	@Column(name = "INQUIRY_TN")
	private String inquiredTn;
	
	@Column(name = "VARIABLE_NPA_FLAG")
	private boolean variableNpaFlag;
	
	public IVRUserSession() {
		// TODO Auto-generated constructor stub
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public boolean isNewSession() {
		return isNewSession;
	}

	public void setNewSession(Boolean isNewSession) {
		this.isNewSession = isNewSession != null ? isNewSession : false;
	}

	public String getCuid() {
		return cuid;
	}

	public void setCuid(String cuid) {
		this.cuid = cuid;
	}

	public String getEmpID() {
		return empID;
	}

	public void setEmpID(String empID) {
		this.empID = empID;
	}

	public boolean isAuthenticated() {
		return isAuthenticated != null ? isAuthenticated : false;
	}

	public void setAuthenticated(Boolean isAuthenticated) {
		this.isAuthenticated = isAuthenticated;
	}

	public String getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(String birthdate) {
		this.birthdate = birthdate;
	}

	public String getNewBirthDate() {
		return newBirthDate;
	}

	public void setNewBirthDate(String newBirthDate) {
		this.newBirthDate = newBirthDate;
	}

	public boolean isLoginJeopardyFlag() {
		return loginJeopardyFlag != null ? loginJeopardyFlag : false;
	}

	public void setLoginJeopardyFlag(Boolean loginJeopardyFlag) {
		this.loginJeopardyFlag = loginJeopardyFlag;
	}

	public boolean isEnabledFlag() {
		return  isEnabledFlag != null ? isEnabledFlag : false;
	}

	public void setEnabledFlag(Boolean isEnabledFlag) {
		this.isEnabledFlag = isEnabledFlag;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getOtpGenerated() {
		return otpGenerated;
	}

	public void setOtpGenerated(String otpGenerated) {
		this.otpGenerated = otpGenerated;
	}

	public boolean isCanBePagedMobile() {
		return canBePagedMobile != null ? canBePagedMobile: false;
	}

	public void setCanBePagedMobile(Boolean canBePagedMobile) {
		this.canBePagedMobile = canBePagedMobile;
	}

	public boolean isCanBePagedEmail() {
		return canBePagedEmail != null ? canBePagedEmail : false;
	}

	public void setCanBePagedEmail(Boolean canBePagedEmail) {
		this.canBePagedEmail = canBePagedEmail;
	}

	public String getNpaPrefix() {
		return npaPrefix;
	}

	public void setNpaPrefix(String npaPrefix) {
		this.npaPrefix = npaPrefix;
	}

	public boolean isCutPageSent() {
		return cutPageSent != null ? cutPageSent : false;
	}

	public void setCutPageSent(Boolean cutPageSent) {
		this.cutPageSent = cutPageSent;
	}

	public String getEc() {
		return ec;
	}

	public void setEc(String ec) {
		this.ec = ec;
	}

	public String getMc() {
		return mc;
	}

	public void setMc(String mc) {
		this.mc = mc;
	}

	public LocalDate getPasswordExpireDate() {
		return passwordExpireDate;
	}

	public void setPasswordExpireDate(LocalDate passwordExpireDate) {
		this.passwordExpireDate = passwordExpireDate;
	}

	public int getAge() {
		return this.age = age != null ? age: 0;
	}

	public void setAge(Integer age) {
		this.age = age != null ? age: 0;
	}

	public boolean isTechASplicer() {
		return isTechASplicer != null ? isTechASplicer : false;
	}

	public void setTechASplicer(Boolean isTechASplicer) {
		this.isTechASplicer = isTechASplicer;
	}

	public boolean isDebugFlag() {
		return isDebugFlag != null ? isDebugFlag : false;
	}

	public void setDebugFlag(Boolean isDebugFlag) {
		this.isDebugFlag = isDebugFlag;
	}

	public String getPagerCo() {
		return pagerCo;
	}

	public void setPagerCo(String pagerCo) {
		this.pagerCo = pagerCo;
	}

	public LocalDateTime getLastActiveSessionTime() {
		return lastActiveSessionTime;
	}

	public void setLastActiveSessionTime(LocalDateTime lastActiveSessionTime) {
		this.lastActiveSessionTime = lastActiveSessionTime;
	}

//	public String getCurrentAssignmentResponse() {
//		return currentAssignmentResponse;
//	}
//
//	public void setCurrentAssignmentResponse(String currentAssignmentResponse) {
//		this.currentAssignmentResponse = currentAssignmentResponse;
//	}

//	public String getGetTNInfoResponse() {
//		return getTNInfoResponse;
//	}
//
//	public void setGetTNInfoResponse(String getTNInfoResponse) {
//		this.getTNInfoResponse = getTNInfoResponse;
//	}

	public String getCentralOfficeEquipmentResponse() {
		return centralOfficeEquipmentResponse;
	}

	public void setCentralOfficeEquipmentResponse(String centralOfficeEquipmentResponse) {
		this.centralOfficeEquipmentResponse = centralOfficeEquipmentResponse;
	}

	public String getDefectivePairResponse() {
		return defectivePairResponse;
	}

	public void setDefectivePairResponse(String defectivePairResponse) {
		this.defectivePairResponse = defectivePairResponse;
	}

	public String getMultipleAppearanceResponse() {
		return multipleAppearanceResponse;
	}

	public void setMultipleAppearanceResponse(String multipleAppearanceResponse) {
		this.multipleAppearanceResponse = multipleAppearanceResponse;
	}

	public int getPasswordAttemptCounter() {
		return passwordAttemptCounter != null ? passwordAttemptCounter : 0;
	}
	
	public void setPasswordAttemptCounter(Integer passwordAttemptCounter) {
		this.passwordAttemptCounter = passwordAttemptCounter;
	}


	public String getSegmentRead() {
		return segmentRead;
	}

	public void setSegmentRead(String segmentRead) {
		this.segmentRead = segmentRead;
	}

	public String getLosDbResponse() {
		return losDbResponse;
	}

	public void setLosDbResponse(String losDbResponse) {
		this.losDbResponse = losDbResponse;
	}

	public String getCurrentAssignmentResponse() {
		return currentAssignmentResponse;
	}

	public void setCurrentAssignmentResponse(String currentAssignmentResponse) {
		this.currentAssignmentResponse = currentAssignmentResponse;
	}

	public String getFacsInqType() {
		return facsInqType;
	}

	public void setFacsInqType(String inqType) {
		this.facsInqType = inqType;
	}

	public String getAdditionalLinesResponse() {
		return additionalLinesResponse;
	}

	public void setAdditionalLinesResponse(String additionalLinesResponse) {
		this.additionalLinesResponse = additionalLinesResponse;
	}

	public String getRtrvMaintChngeMsgName() {
		return rtrvMaintChngeMsgName;
	}

	public void setRtrvMaintChngeMsgName(String rtrvMaintChngeMsgName) {
		this.rtrvMaintChngeMsgName = rtrvMaintChngeMsgName;
	}

	public String getRtrvLoopAssgMsgName() {
		return rtrvLoopAssgMsgName;
	}

	public void setRtrvLoopAssgMsgName(String rtrvLoopAssgMsgName) {
		this.rtrvLoopAssgMsgName = rtrvLoopAssgMsgName;
	}

	public int getAdditionalLinesCounter() {
		return additionalLinesCounter != null ? additionalLinesCounter : 0;
	}

	public void setAdditionalLinesCounter(int additionalLinesCounter) {
		this.additionalLinesCounter = additionalLinesCounter;
	}

	public Integer getCandPairCounter() {
		return candPairCounter;
	}

	public void setCandPairCounter(Integer candPairCounter) {
		this.candPairCounter = candPairCounter;
	}

	public String getCable() {
		return cable;
	}

	public void setCable(String cable) {
		this.cable = cable;
	}

	public String getPair() {
		return pair;
	}

	public void setPair(String pair) {
		this.pair = pair;
	}

	public String getInquiredTn() {
		return inquiredTn;
	}

	public void setInquiredTn(String inquiredTn) {
		this.inquiredTn = inquiredTn;
	}

	public boolean isVariableNpaFlag() {
		return variableNpaFlag;
	}

	public void setVariableNpaFlag(boolean variableNpaFlag) {
		this.variableNpaFlag = variableNpaFlag;
	}
}
