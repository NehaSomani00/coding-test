package com.lumen.fastivr.IVRDto.LOSDB;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CTelephoneTest {
	
	@InjectMocks CTelephone telephone;

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void test() {
		telephone.setNpa("123");
		telephone.setNxx("456");
		telephone.setLineNumber("7890");
		
		telephone.getNpa();
		telephone.getNxx();
		telephone.getLineNumber();
		
		telephone.equals(new CTelephone());
		telephone.hashCode();
		
	}

}
