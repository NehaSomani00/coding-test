package com.lumen.fastivr.IVRCNF.repository;

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
import com.lumen.fastivr.IVRCNF.entity.IVRCnfEntity;

@Service
public class IVRCnfCacheService {

	// TODO: move all @Autowired to constructor based injection. better performance
	// by lazy loading
	@Autowired
	private IVRCnfRepository redisCache;

	private static final String TECH_CACHE = "LUMEN_TECH_CACHE_CNF";

	@Cacheable(value = TECH_CACHE, key = "#sessionId")
	public IVRCnfEntity getBySessionId(String sessionId) {
		
		return redisCache.findBySessionId(sessionId).orElse(null);
	}

	@CachePut(value = TECH_CACHE, key = "#session.sessionId")
	@Transactional(propagation = Propagation.REQUIRED)
	public IVRCnfEntity addSession(IVRCnfEntity session) {
		
		Optional<IVRCnfEntity> userSession = Optional.ofNullable(redisCache.save(session));
		return userSession.orElseThrow(
				() -> new BadUserInputException(session.getSessionId(), "Cannot Persist Session in Database"));
	}

	@CachePut(value = TECH_CACHE, key = "#session.sessionId")
	@Transactional(propagation = Propagation.REQUIRED)
	public IVRCnfEntity updateSession(IVRCnfEntity session) {
		
		if (session != null) {
			session.setLastActiveSessionTime(LocalDateTime.now());
			Optional<IVRCnfEntity> userSession = Optional.ofNullable(redisCache.save(session));
			return userSession.orElseThrow(
					() -> new BadUserInputException(session.getSessionId(), "Cannot Update Session in Database"));
		} else {
			
			return null;
		}
	}

	@CacheEvict(value = TECH_CACHE, key = "#session.sessionId")
	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteSession(IVRCnfEntity session) {
		
		redisCache.deleteById(session.getSessionId());
	}
}
