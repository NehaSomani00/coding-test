package com.lumen.fastivr.IVRSessionManagement;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IVRUserSessionTest {

	@InjectMocks IVRUserSession session;
	
	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testIVRUserSession() {
		session.setAge(0);
		session.setAuthenticated(false);
		session.setBirthdate("");
		session.setSessionId("sesion123");
		session.setCanBePagedEmail(true);
		session.setCanBePagedMobile(true);
		session.setCentralOfficeEquipmentResponse("");
		session.setCuid("");
		session.setCurrentAssignmentResponse("");
		session.setCutPageSent(false);
		session.setDebugFlag(false);
		session.setDefectivePairResponse("");
		session.setEc("");
		session.setEmpID("");
		session.setEnabledFlag(false);
		session.setPasswordExpireDate(LocalDate.now());
		session.setLastActiveSessionTime(LocalDateTime.now());
		session.setLoginJeopardyFlag(false);
		session.setMc("");
		session.setMultipleAppearanceResponse("");
		session.setNewBirthDate("");
		session.setNewPassword("");
		session.setNpaPrefix("");
		session.setOtpGenerated("");
		session.setPagerCo("");
		session.setPasswordAttemptCounter(0);
		session.setTechASplicer(false);
		session.setNewSession(false);
		session.setLosDbResponse("");
		
		session.getAge();
		session.getBirthdate();
		session.getCentralOfficeEquipmentResponse();
		session.getCuid();
		session.getCurrentAssignmentResponse();
		session.isCutPageSent();
		session.getDefectivePairResponse();
		session.getEc();
		session.getEmpID();
		session.getPasswordExpireDate();
		session.getLastActiveSessionTime();
		session.getMc();
		session.getMultipleAppearanceResponse();
		session.getNewBirthDate();
		session.getNewPassword();
		session.getNpaPrefix();
		session.getOtpGenerated();
		session.getPagerCo();
		session.getPasswordAttemptCounter();
		session.getSessionId();
		session.isAuthenticated();
		session.isCanBePagedMobile();
		session.isCanBePagedEmail();
		session.isDebugFlag();
		session.isEnabledFlag();
		session.isLoginJeopardyFlag();
		session.isTechASplicer();
		session.isNewSession();
		session.getLosDbResponse();
	}

}
