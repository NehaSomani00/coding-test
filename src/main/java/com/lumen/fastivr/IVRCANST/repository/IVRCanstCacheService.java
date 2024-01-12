package com.lumen.fastivr.IVRCANST.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.lumen.fastivr.IVRBusinessException.BadUserInputException;
import com.lumen.fastivr.IVRCANST.entity.IVRCanstEntity;

@Service
public class IVRCanstCacheService {

	// TODO: move all @Autowired to constructor based injection. better performance
	// by lazy loading
	@Autowired
	private IVRCanstRepository redisCache;

	private static final String TECH_CACHE = "LUMEN_TECH_CACHE_CANST";

	@Cacheable(value = TECH_CACHE, key = "#sessionId")
	public IVRCanstEntity getBySessionId(String sessionId) {
		
		return redisCache.findBySessionId(sessionId).orElse(null);
	}

	@CachePut(value = TECH_CACHE, key = "#session.sessionId")
	@Transactional(propagation = Propagation.REQUIRED)
	public IVRCanstEntity addSession(IVRCanstEntity session) {
		
		Optional<IVRCanstEntity> userSession = Optional.ofNullable(redisCache.save(session));
		return userSession.orElseThrow(
				() -> new BadUserInputException(session.getSessionId(), "Cannot Persist Session in Database"));
	}

	@CachePut(value = TECH_CACHE, key = "#session.sessionId")
	@Transactional(propagation = Propagation.REQUIRED)
	public IVRCanstEntity updateSession(IVRCanstEntity session) {
		
		if (session != null) {
			session.setLastActiveSessionTime(LocalDateTime.now());
			Optional<IVRCanstEntity> userSession = Optional.ofNullable(redisCache.save(session));
			return userSession.orElseThrow(
					() -> new BadUserInputException(session.getSessionId(), "Cannot Update Session in Database"));
		} else {
			
			return null;
		}
	}

	@CacheEvict(value = TECH_CACHE, key = "#session.sessionId")
	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteSession(IVRCanstEntity session) {
		
		redisCache.deleteById(session.getSessionId());
	}
}
