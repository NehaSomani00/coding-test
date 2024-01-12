package com.lumen.fastivr.IVREndpointSecurity.service;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

import com.lumen.fastivr.IVREndpointSecurity.entity.EndpointSecurity;
import com.lumen.fastivr.IVREndpointSecurity.userDetails.IVREndpointUserDetails;

@ExtendWith(MockitoExtension.class)
class EndpointSecurityServiceImplTest {
	
	@InjectMocks EndpointSecurityServiceImpl service;
	@Mock JdbcUserDetailsManager userDetailsManager;
	@Mock PasswordEncoder passwordEncoder;
	EndpointSecurity user;
	IVREndpointUserDetails userDetails; 
	
	@BeforeEach
	void setUp() throws Exception {
		user =  new EndpointSecurity();
		user.setUsername("tester");
		user.setSecret("fastivr");
		user.setEnabled(true);
		userDetails = new IVREndpointUserDetails(user, passwordEncoder);
	}

	@Test
	void testAddUser() {
		doNothing().when(userDetailsManager).createUser(any(IVREndpointUserDetails.class));
		verifyNoMoreInteractions(userDetailsManager);
		service.addUser(user);
	}

	@Test
	void testUpdateUser() {
		doNothing().when(userDetailsManager).updateUser(any(IVREndpointUserDetails.class));
		verifyNoMoreInteractions(userDetailsManager);
		service.updateUser(user);
	}

}
