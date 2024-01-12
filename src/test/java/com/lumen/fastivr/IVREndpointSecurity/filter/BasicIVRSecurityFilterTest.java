package com.lumen.fastivr.IVREndpointSecurity.filter;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class BasicIVRSecurityFilterTest {
	
	@InjectMocks BasicIVRSecurityFilter filter;
	@Mock AuthenticationManager manager;
	@Mock FilterChain filterChain;
	@Mock HttpServletResponse response;
	@Mock HttpServletRequest request;
	Authentication authResult;
	
	MockMvc mockMvc;
	List<GrantedAuthority> authorities;
	String username;
	String password;
	
	private static final String SERVICE_ASSURANCE_V1_TROUBLE_VOICE_IVR_WEBHOOK_ADDUSER = "/ServiceAssurance/v1/Trouble/voiceIvrWebhook/security/addUser";
	
	private static final String SERVICE_ASSURANCE_V1_TROUBLE_VOICE_IVR_WEBHOOK_SANITY = "/ServiceAssurance/v1/Trouble/voiceIvrWebhook/sanity";
	
	@BeforeEach
	void setUp() throws Exception {
		mockMvc = MockMvcBuilders.standaloneSetup().addFilters(filter).build();
		request.setAttribute("Authorization", "Basic ZmFzdGl2cjpmYXN0aXZy");
		username = "tester";
		password = "tester";
		GrantedAuthority authority = () -> "USER";
		authorities = new ArrayList<>();
		authorities.add(authority);
	}
	
	@Test
	void testBasicIVRSecurityFilter_UnauthorizedAccess_InvalidAuthToken() throws ServletException, IOException {
		authResult = new UsernamePasswordAuthenticationToken(username, password);
		MockHttpServletRequest request = new MockHttpServletRequest("GET", SERVICE_ASSURANCE_V1_TROUBLE_VOICE_IVR_WEBHOOK_SANITY);
		request.addHeader("Authorization", "Basic ZmFzdGl2cjpmYXN0aXZy");
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		when(manager.authenticate(any(Authentication.class))).thenReturn(authResult);
		filter.doFilterInternal(request, response, filterChain);
		assertEquals("Unauthorized access", response.getErrorMessage());
	
	}
	
	@Test
	void testBasicIVRSecurityFilter_AuthorizedAccess() throws ServletException, IOException {
		authResult = new UsernamePasswordAuthenticationToken(username, password, authorities);
		MockHttpServletRequest request = new MockHttpServletRequest("GET", SERVICE_ASSURANCE_V1_TROUBLE_VOICE_IVR_WEBHOOK_SANITY);
		request.addHeader("Authorization", "Basic ZmFzdGl2cjpmYXN0aXZy");
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		when(manager.authenticate(any(Authentication.class))).thenReturn(authResult);
		filter.doFilterInternal(request, response, filterChain);
		assertEquals(HttpStatus.OK.value(), response.getStatus()); 
	
	}

	@Test
	void testShouldNotFilterHttpServletRequest() throws ServletException, IOException {
		MockHttpServletRequest request = new MockHttpServletRequest("POST", SERVICE_ASSURANCE_V1_TROUBLE_VOICE_IVR_WEBHOOK_ADDUSER);
		request.addHeader("Authorization", "Basic ZmFzdGl2cjpmYXN0aXZy");
		MockHttpServletResponse response = new MockHttpServletResponse();
		filter.doFilterInternal(request, response, filterChain);
		boolean result = filter.shouldNotFilter(request);
		assertFalse(result);
	}

}
