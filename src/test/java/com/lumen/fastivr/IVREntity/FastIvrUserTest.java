package com.lumen.fastivr.IVREntity;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Date;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FastIvrUserTest {
	
	@InjectMocks FastIvrUser user;
	
	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testFastIvrUser() {
		user.setAge(20);
		user.setBirthdate("");
		user.setBirthdateCounter(2);
		user.setComments("");
		user.setCuid("");
		user.setCutPageSent("N");
		user.setDebugFlag("N");
		user.setEc("");
		user.setEmpID("");
		user.setIsEnabledFlag("N");
		user.setExpireDate(LocalDate.now());
		user.setFaxTN("");
		user.setLoginJeopardyFlag("N");
		user.setMc("");
		user.setEc("");
		user.setNpaPrefix("");
		user.setIsTechASplicer("N");
		user.setPagerCo("");
		user.setPassword("");
		
		user.getAge();
		user.getBirthdate();
		user.getBirthdateCounter();
		user.getComments();
		user.getCuid();
		user.getCutPageSent();
		user.getDebugFlag();
		user.getEc();
		user.getEmpID();
		user.getIsEnabledFlag();
		user.getExpireDate();
		user.getFaxTN();
		user.getLoginJeopardyFlag();
		user.getMc();
		user.getEc();
		user.getNpaPrefix();
		user.getIsTechASplicer();
		user.getPagerCo();
		user.getPassword();
		
		user.toString();
	}

}
