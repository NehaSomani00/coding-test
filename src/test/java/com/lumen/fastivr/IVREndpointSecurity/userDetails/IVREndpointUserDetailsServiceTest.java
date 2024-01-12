package com.lumen.fastivr.IVREndpointSecurity.userDetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.lumen.fastivr.IVREndpointSecurity.entity.EndpointSecurity;
import com.lumen.fastivr.IVREndpointSecurity.repository.SecurityEndpointRepository;

@ExtendWith(MockitoExtension.class)
class IVREndpointUserDetailsServiceTest {

	@InjectMocks IVREndpointUserDetailsService service;
	@Mock SecurityEndpointRepository mockRepository;
	
	String username;
	EndpointSecurity endpointUser;
	@BeforeEach
	void setUp() throws Exception {
		username = "fastivr";
		endpointUser = new EndpointSecurity();
		endpointUser.setUsername(username);
		endpointUser.setSecret(username);
		endpointUser.setEnabled(true);
	}

	@Test
	void testLoadUserByUsername_Success() {
		when(mockRepository.findByUsername(username)).thenReturn(Optional.of(endpointUser));
		UserDetails securedUser = service.loadUserByUsername(username);
	    assertEquals(username,securedUser.getUsername());
	}
	
	@Test
	void testLoadUserByUsername_Failure() {
		when(mockRepository.findByUsername(username)).thenReturn(Optional.empty());
	    assertThrows(UsernameNotFoundException.class,() -> service.loadUserByUsername(username));
	}

}
