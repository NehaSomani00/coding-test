package com.lumen.fastivr.IVRCacheManagement;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.lumen.fastivr.IVRBusinessException.BadUserInputException;
import com.lumen.fastivr.IVRSessionManagement.IVRUserSession;

@Service
public class IVRCacheService {
	
	//TODO: move all @Autowired to constructor based injection. better performance by lazy loading 
	@Autowired
	private IVRSessionRedisInterface redisCache;
	
	private static final String TECH_CACHE =  "LUMEN_TECH_CACHE";
	
	
	@Cacheable(value = TECH_CACHE, key = "#sessionId")
	public IVRUserSession getBySessionId(String sessionId) {
		return redisCache.findBySessionId(sessionId).orElse(null);
	}
	
	@CachePut(value = TECH_CACHE, key = "#session.sessionId")
	@Transactional(propagation = Propagation.REQUIRED)
	public IVRUserSession addSession(IVRUserSession session) {
		Optional<IVRUserSession> userSession = Optional.ofNullable(redisCache.save(session));
		return userSession.orElseThrow(() -> new BadUserInputException(session.getSessionId(), "Cannot Persist Session in Database"));
	}
	
	@CachePut(value = TECH_CACHE, key = "#session.sessionId")
	@Transactional(propagation = Propagation.REQUIRED)
	public IVRUserSession updateSession(IVRUserSession session) {
		if(session!=null) {
		session.setLastActiveSessionTime(LocalDateTime.now());
		Optional<IVRUserSession> userSession = Optional.ofNullable(redisCache.save(session));
		return userSession.orElseThrow(() -> new BadUserInputException(session.getSessionId(), "Cannot Update Session in Database"));
		} else {
			return null;
		}
	}
	
	@CacheEvict(value = TECH_CACHE, key = "#session.sessionId")
	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteSession(IVRUserSession session) {
		redisCache.deleteById(session.getSessionId());
	}
}
