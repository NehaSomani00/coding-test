package com.lumen.fastivr.IVRCANST.repository;

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
import com.lumen.fastivr.IVRCANST.entity.IVRCanstEntity;

@ExtendWith(MockitoExtension.class)
public class IVRCanstCacheServiceTest {

	@InjectMocks IVRCanstCacheService cacheService;
	
	@Mock IVRCanstRepository mockCache;

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testGetBySessionId() {
		
		IVRCanstEntity mockUser = loadIvrSession();
		when(mockCache.findBySessionId(mockUser.getSessionId())).thenReturn(Optional.of(mockUser));
		IVRCanstEntity returneduser = cacheService.getBySessionId(mockUser.getSessionId());
		
		assertEquals(mockUser.getSessionId(), returneduser.getSessionId());
	}
	
	@Test
	void testGetBySessionId_exception() {
		
		IVRCanstEntity mockUser = loadIvrSession();
		when(mockCache.findBySessionId(mockUser.getSessionId())).thenReturn(Optional.empty());
		assertNull(cacheService.getBySessionId(mockUser.getSessionId()));
	}

	@Test
	void testAddSession() {
		
		IVRCanstEntity mockUser = loadIvrSession();
		when(mockCache.save(mockUser)).thenReturn(mockUser);
		IVRCanstEntity returneduser = cacheService.addSession(mockUser);
		
		assertEquals(mockUser.getSessionId(), returneduser.getSessionId());
	}
	
	@Test
	void testAddSession_exception() {
		
		IVRCanstEntity mockUser = loadIvrSession();
		when(mockCache.save(mockUser)).thenReturn(null);
		assertThrows(BadUserInputException.class, () -> cacheService.addSession(mockUser));
	}

	@Test
	void testUpdateSession() {
		
		IVRCanstEntity mockUser = loadIvrSession();
		when(mockCache.save(mockUser)).thenReturn(mockUser);
		IVRCanstEntity returneduser = cacheService.updateSession(mockUser);
		
		assertEquals(mockUser.getSessionId(), returneduser.getSessionId());
	}
	
	@Test
	void testUpdateSession_exception() {
		
		IVRCanstEntity mockUser = loadIvrSession();
		when(mockCache.save(mockUser)).thenReturn(null);
		assertThrows(BadUserInputException.class, () -> cacheService.updateSession(mockUser));
	}
	
	@Test
	void testDeleteSession() {
		
		IVRCanstEntity mockUser = loadIvrSession();
		doNothing().when(mockCache).deleteById(mockUser.getSessionId());
		cacheService.deleteSession(mockUser);
	}

	private IVRCanstEntity loadIvrSession() {
		
		IVRCanstEntity session = new IVRCanstEntity();
		session.setSessionId("session123");
		return session;
	}
	
	
}
