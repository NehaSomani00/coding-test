package com.lumen.fastivr.IVRCNF.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lumen.fastivr.IVRBusinessException.BadUserInputException;
import com.lumen.fastivr.IVRCNF.entity.IVRCnfEntity;

@ExtendWith(MockitoExtension.class)
public class IVRCnfCacheServiceTest {

	@InjectMocks IVRCnfCacheService cacheService;
	
	@Mock IVRCnfRepository mockCache;

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testGetBySessionId() {
		
		IVRCnfEntity mockUser = loadIvrSession();
		when(mockCache.findBySessionId(mockUser.getSessionId())).thenReturn(Optional.of(mockUser));
		IVRCnfEntity returneduser = cacheService.getBySessionId(mockUser.getSessionId());
		
		assertEquals(mockUser.getSessionId(), returneduser.getSessionId());
	}
	
	@Test
	void testGetBySessionId_exception() {
		
		IVRCnfEntity mockUser = loadIvrSession();
		when(mockCache.findBySessionId(mockUser.getSessionId())).thenReturn(Optional.empty());
		assertNull(cacheService.getBySessionId(mockUser.getSessionId()));
	}

	@Test
	void testAddSession() {
		
		IVRCnfEntity mockUser = loadIvrSession();
		when(mockCache.save(mockUser)).thenReturn(mockUser);
		IVRCnfEntity returneduser = cacheService.addSession(mockUser);
		
		assertEquals(mockUser.getSessionId(), returneduser.getSessionId());
	}
	
	@Test
	void testAddSession_exception() {
		
		IVRCnfEntity mockUser = loadIvrSession();
		when(mockCache.save(mockUser)).thenReturn(null);
		assertThrows(BadUserInputException.class, () -> cacheService.addSession(mockUser));
	}

	@Test
	void testUpdateSession() {
		
		IVRCnfEntity mockUser = loadIvrSession();
		when(mockCache.save(mockUser)).thenReturn(mockUser);
		IVRCnfEntity returneduser = cacheService.updateSession(mockUser);
		
		assertEquals(mockUser.getSessionId(), returneduser.getSessionId());
	}
	
	@Test
	void testUpdateSession_exception() {
		
		IVRCnfEntity mockUser = loadIvrSession();
		when(mockCache.save(mockUser)).thenReturn(null);
		assertThrows(BadUserInputException.class, () -> cacheService.updateSession(mockUser));
	}
	
	@Test
	void testDeleteSession() {
		
		IVRCnfEntity mockUser = loadIvrSession();
		doNothing().when(mockCache).deleteById(mockUser.getSessionId());
		cacheService.deleteSession(mockUser);
	}

	private IVRCnfEntity loadIvrSession() {
		
		IVRCnfEntity session = new IVRCnfEntity();
		session.setSessionId("session123");
		return session;
	}
}
