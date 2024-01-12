package com.lumen.fastivr.IVREndpointSecurity.authProvider;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.lumen.fastivr.IVREndpointSecurity.entity.EndpointSecurity;
import com.lumen.fastivr.IVREndpointSecurity.token.IVREndpointAuthenticationToken;
import com.lumen.fastivr.IVREndpointSecurity.userDetails.IVREndpointUserDetails;

@ExtendWith(MockitoExtension.class)
class IVRAuthenticationProviderTest {
	
	@InjectMocks IVRAuthenticationProvider provider;
	
	@Mock PasswordEncoder encoder;
	@Mock UserDetailsService userDetailsService;
	
	List<GrantedAuthority> authorities;
	@InjectMocks EndpointSecurity securityUser;
	
	@BeforeEach
	void setUp() throws Exception {
		GrantedAuthority authority = () -> "USER";
		authorities = new ArrayList<>();
		authorities.add(authority);
		
		securityUser.setUsername("tester");
		securityUser.setSecret("tester");
		securityUser.setEnabled(true);
	}

	
	@Test
	void testAuthenticate_positive() {
		Authentication authRequest = new IVREndpointAuthenticationToken(securityUser.getUsername(), securityUser.getSecret(), authorities);
		IVREndpointUserDetails mockUserDetails = new IVREndpointUserDetails(securityUser);
		when(userDetailsService.loadUserByUsername(authRequest.getName())).thenReturn(mockUserDetails);
		when(encoder.matches(securityUser.getSecret(), mockUserDetails.getPassword())).thenReturn(true);
		
		Authentication result = provider.authenticate(authRequest);
		assertInstanceOf(UsernamePasswordAuthenticationToken.class, result);
	}
	
	@Test
	void testAuthenticate_negative() {
		Authentication authRequest = new UsernamePasswordAuthenticationToken(securityUser.getUsername(), securityUser.getSecret(), authorities);
		Authentication result = provider.authenticate(authRequest);
		assertNull(result);
	}
	
	@Test
	void testAuthenticate_BadCredentialsException() {
		Authentication authRequest = new IVREndpointAuthenticationToken(securityUser.getUsername(), securityUser.getSecret(), authorities);
		IVREndpointUserDetails mockUserDetails = new IVREndpointUserDetails(securityUser);
		when(userDetailsService.loadUserByUsername(authRequest.getName())).thenReturn(mockUserDetails);
		when(encoder.matches(securityUser.getSecret(), mockUserDetails.getPassword())).thenReturn(false);
		
		assertThrows(BadCredentialsException.class, () ->  provider.authenticate(authRequest));
	}

}
