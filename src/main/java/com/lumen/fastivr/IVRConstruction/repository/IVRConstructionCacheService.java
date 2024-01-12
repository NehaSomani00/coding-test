package com.lumen.fastivr.IVRConstruction.repository;

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
import com.lumen.fastivr.IVRConstruction.entity.IvrConstructionSession;


@Service
public class IVRConstructionCacheService {
	
	
	@Autowired
	private IVRConstructionRepository redisCache;

	private static final String TECH_CACHE = "LUMEN_CONSTRUCTION_TEST_CACHE";

	@Cacheable(value = TECH_CACHE, key = "#sessionId")
	public IvrConstructionSession getBySessionId(String sessionId) {
		return redisCache.findBySessionId(sessionId).orElse(null);
	}

	@CachePut(value = TECH_CACHE, key = "#session.sessionId")
	@Transactional(propagation = Propagation.REQUIRED)
	public IvrConstructionSession addSession(IvrConstructionSession session) {
		Optional<IvrConstructionSession> userSession = Optional.ofNullable(redisCache.save(session));
		return userSession.orElseThrow(
				() -> new BadUserInputException(session.getSessionId(), "Cannot Persist Construction Session in Database"));
	}

	@CachePut(value = TECH_CACHE, key = "#session.sessionId")
	@Transactional(propagation = Propagation.REQUIRED)
	public IvrConstructionSession updateSession(IvrConstructionSession session) {
		if (session != null) {
			session.setLastActiveSessionTime(LocalDateTime.now());
			Optional<IvrConstructionSession> userSession = Optional.ofNullable(redisCache.save(session));
			return userSession.orElseThrow(
					() -> new BadUserInputException(session.getSessionId(), "Cannot Update Construction Session in Database"));
		} else {
			return null;
		}
	}

	@CacheEvict(value = TECH_CACHE, key = "#session.sessionId")
	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteSession(IvrConstructionSession session) {
		redisCache.deleteById(session.getSessionId());
	}

}
