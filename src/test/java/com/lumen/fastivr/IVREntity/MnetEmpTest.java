package com.lumen.fastivr.IVREntity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MnetEmpTest {
	
	@InjectMocks MnetEmp emp;

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testMnetEmp() {
		emp.setCity("");
		emp.setCompanyId("");
		emp.setCompanyName("");
		emp.setCuid("");
		emp.setFirstName("");
		emp.setLastName("");
		emp.setMgrCuid("");
		emp.setMiddle("");
		emp.setMnetId("");
		emp.setPager("");
		emp.setPagerPin("");
		emp.setRoom("");
		emp.setState("");
		emp.setStreet("");
		emp.setType("");
		emp.setWorkPhone("");
		
		emp.getCity();
		emp.getCompanyId();
		emp.getCompanyName();
		emp.getCuid();
		emp.getFirstName();
		emp.getLastName();
		emp.getMgrCuid();
		emp.getMiddle();
		emp.getMnetId();
		emp.getPager();
		emp.getPagerPin();
		emp.getRoom();
		emp.getState();
		emp.getStreet();
		emp.getType();
		emp.getWorkPhone();
		
		emp.toString();
	}

}
