package com.lumen.fastivr.IVRLFACS;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.ReflectionUtils;

import com.lumen.fastivr.IVRDto.LOSDB.CTelephone;
import com.lumen.fastivr.IVRDto.LOSDB.TNInfoResponse;
import com.lumen.fastivr.IVREntity.NpaId;
import com.lumen.fastivr.IVREntity.TNInfo;
import com.lumen.fastivr.IVRRepository.TnInfoRepository;
import com.lumen.fastivr.IVRSessionManagement.IVRUserSession;

@ExtendWith(MockitoExtension.class)
class IVRLOSDBManagerTest {
	
	@InjectMocks IVRLOSDBManager manager;
	
	@Mock TnInfoRepository mockTnRepository;
	
	@Mock HttpClient mockHttpClient;
	
	@Mock HttpResponse<String> mockResponseBody;
	
	private CTelephone ctelephone;
	
	private IVRUserSession session;
	
	private NpaId npaId;

	@BeforeEach
	void setUp() throws Exception {
		
		ctelephone = new CTelephone();
		ctelephone.setNpa("219");
		ctelephone.setNxx("303");
		ctelephone.setLineNumber("1234");
		session = new IVRUserSession();
		session.setSessionId("session123");
		npaId = new NpaId();
		npaId.setNpaPrefix(ctelephone.getNpa());
		npaId.setNxxPrefix(ctelephone.getNxx());
		
		// mock uri field values
		Field field = ReflectionUtils.findField(IVRLOSDBManager.class, "tnInfoUrl");
		field.setAccessible(true);
		field.set(manager, "https://mocklosdbserver/losdbLookup/v2/tn/");
		
		//mock service provider ids
		Field field2 = ReflectionUtils.findField(IVRLOSDBManager.class, "serviceProviderIds");
		field2.setAccessible(true);
		field2.set(manager, "9631,9636,9638,0209");
	}

	@Test
	void testGetTnInfo() throws IllegalArgumentException, IllegalAccessException {
		String mockResponse = "response";
		
		when(mockHttpClient.sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
		.thenReturn(CompletableFuture.completedFuture(mockResponseBody));
		
		when(mockResponseBody.body()).thenReturn(mockResponse);
		
		//mocked uri value in @BeforeSetup
		String actualResponse = manager.getTnInfo(ctelephone, session.getSessionId());
		assertEquals(mockResponse, actualResponse);
	}

	@Test
	void testMatchServiceProviderId_true() throws IllegalArgumentException, IllegalAccessException {
		//mocked service provider ids' value in @BeforeSetup
		TNInfoResponse tnInfoResponse = new TNInfoResponse();
		tnInfoResponse.setServiceProviderId("9631");
		assertTrue(manager.matchServiceProviderId(tnInfoResponse));
		
	}
	
	@Test
	void testMatchServiceProviderId_false() throws IllegalArgumentException, IllegalAccessException {
		//mocked service provider ids' value in @BeforeSetup
		TNInfoResponse tnInfoResponse = new TNInfoResponse();
		tnInfoResponse.setServiceProviderId("9632");
		assertFalse(manager.matchServiceProviderId(tnInfoResponse));
		
	}

	@Test
	void testValidateNPA() {
		TNInfo tninfo = new TNInfo();
		when(mockTnRepository.findById(npaId)).thenReturn(Optional.of(tninfo));
		TNInfo response = manager.validateNPA(ctelephone, session.getSessionId());
		assertNotNull(response);
	}
	
	@Test
	void testValidateNPA_empty() {
		when(mockTnRepository.findById(npaId)).thenReturn(Optional.empty());
		TNInfo response = manager.validateNPA(ctelephone, session.getSessionId());
		assertNull(response);
	}

}
