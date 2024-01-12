package com.lumen.fastivr.IVRCacheManagement;

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
import com.lumen.fastivr.IVRSessionManagement.IVRUserSession;

@ExtendWith(MockitoExtension.class)
class IVRCacheServiceTest {
	
	@InjectMocks IVRCacheService cacheService;
	
	@Mock IVRSessionRedisInterface mockCache;

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testGetBySessionId() {
		IVRUserSession mockUser = loadIvrSession();
		when(mockCache.findBySessionId(mockUser.getSessionId())).thenReturn(Optional.of(mockUser));
		IVRUserSession returneduser = cacheService.getBySessionId(mockUser.getSessionId());
		
		assertEquals(mockUser.getSessionId(), returneduser.getSessionId());
	}
	
	@Test
	void testGetBySessionId_exception() {
		IVRUserSession mockUser = loadIvrSession();
		when(mockCache.findBySessionId(mockUser.getSessionId())).thenReturn(Optional.empty());
		assertNull(cacheService.getBySessionId(mockUser.getSessionId()));
	}

	@Test
	void testAddSession() {
		IVRUserSession mockUser = loadIvrSession();
		when(mockCache.save(mockUser)).thenReturn(mockUser);
		IVRUserSession returneduser = cacheService.addSession(mockUser);
		
		assertEquals(mockUser.getSessionId(), returneduser.getSessionId());
	}
	
	@Test
	void testAddSession_exception() {
		IVRUserSession mockUser = loadIvrSession();
		when(mockCache.save(mockUser)).thenReturn(null);
		assertThrows(BadUserInputException.class, () -> cacheService.addSession(mockUser));
	}

	@Test
	void testUpdateSession() {
		IVRUserSession mockUser = loadIvrSession();
		when(mockCache.save(mockUser)).thenReturn(mockUser);
		IVRUserSession returneduser = cacheService.updateSession(mockUser);
		
		assertEquals(mockUser.getSessionId(), returneduser.getSessionId());
	}
	
	@Test
	void testUpdateSession_exception() {
		IVRUserSession mockUser = loadIvrSession();
		when(mockCache.save(mockUser)).thenReturn(null);
		assertThrows(BadUserInputException.class, () -> cacheService.updateSession(mockUser));
	}
	
	@Test
	void testDeleteSession() {
		IVRUserSession mockUser = loadIvrSession();
		doNothing().when(mockCache).deleteById(mockUser.getSessionId());
		cacheService.deleteSession(mockUser);
	}

	private IVRUserSession loadIvrSession() {
		IVRUserSession session = new IVRUserSession();
		session.setSessionId("session123");
		return session;
	}

}
