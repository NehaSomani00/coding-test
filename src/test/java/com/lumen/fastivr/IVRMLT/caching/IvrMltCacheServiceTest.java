package com.lumen.fastivr.IVRMLT.caching;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lumen.fastivr.IVRBusinessException.BadUserInputException;
import com.lumen.fastivr.IVRMLT.entity.IvrMltSession;
import com.lumen.fastivr.IVRMLT.repository.IvrMltSessionRepository;

@ExtendWith(MockitoExtension.class)
public class IvrMltCacheServiceTest {
	
	@InjectMocks IvrMltCacheService cacheService;
	
	@Mock IvrMltSessionRepository mockCache;

	@Test
	void testGetBySessionId() {
		IvrMltSession mockUser = loadIvrSession();
		when(mockCache.findBySessionId(mockUser.getSessionId())).thenReturn(Optional.of(mockUser));
		IvrMltSession returneduser = cacheService.getBySessionId(mockUser.getSessionId());
		
		assertEquals(mockUser.getSessionId(), returneduser.getSessionId());
	}
	
	@Test
	void testGetBySessionId_exception() {
		IvrMltSession mockUser = loadIvrSession();
		when(mockCache.findBySessionId(mockUser.getSessionId())).thenReturn(Optional.empty());
		assertNull(cacheService.getBySessionId(mockUser.getSessionId()));
	}

	@Test
	void testAddSession() {
		IvrMltSession mockUser = loadIvrSession();
		when(mockCache.save(mockUser)).thenReturn(mockUser);
		IvrMltSession returneduser = cacheService.addSession(mockUser);
		
		assertEquals(mockUser.getSessionId(), returneduser.getSessionId());
	}
	
	@Test
	void testAddSession_exception() {
		IvrMltSession mockUser = loadIvrSession();
		when(mockCache.save(mockUser)).thenReturn(null);
		assertThrows(BadUserInputException.class, () -> cacheService.addSession(mockUser));
	}

	@Test
	void testUpdateSession() {
		IvrMltSession mockUser = loadIvrSession();
		when(mockCache.save(mockUser)).thenReturn(mockUser);
		IvrMltSession returneduser = cacheService.updateSession(mockUser);
		
		assertEquals(mockUser.getSessionId(), returneduser.getSessionId());
	}
	
	@Test
	void testUpdateSession_exception() {
		IvrMltSession mockUser = loadIvrSession();
		when(mockCache.save(mockUser)).thenReturn(null);
		assertThrows(BadUserInputException.class, () -> cacheService.updateSession(mockUser));
	}
	
	@Test
	void testDeleteSession() {
		IvrMltSession mockUser = loadIvrSession();
		doNothing().when(mockCache).deleteById(mockUser.getSessionId());
		cacheService.deleteSession(mockUser);
	}

	private IvrMltSession loadIvrSession() {
		IvrMltSession session = new IvrMltSession();
		session.setSessionId("session123");
		return session;
	}


}
