package com.lumen.fastivr.IVRMLT.caching;

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
import com.lumen.fastivr.IVRMLT.entity.IvrMltSession;
import com.lumen.fastivr.IVRMLT.repository.IvrMltSessionRepository;

@Service
public class IvrMltCacheService {

	@Autowired
	private IvrMltSessionRepository redisCache;

	private static final String TECH_CACHE = "LUMEN_MLT_TEST_CACHE";

	@Cacheable(value = TECH_CACHE, key = "#sessionId")
	public IvrMltSession getBySessionId(String sessionId) {
		return redisCache.findBySessionId(sessionId).orElse(null);
	}

	@CachePut(value = TECH_CACHE, key = "#session.sessionId")
	@Transactional(propagation = Propagation.REQUIRED)
	public IvrMltSession addSession(IvrMltSession session) {
		Optional<IvrMltSession> userSession = Optional.ofNullable(redisCache.save(session));
		return userSession.orElseThrow(
				() -> new BadUserInputException(session.getSessionId(), "Cannot Persist MLT Session in Database"));
	}

	@CachePut(value = TECH_CACHE, key = "#session.sessionId")
	@Transactional(propagation = Propagation.REQUIRED)
	public IvrMltSession updateSession(IvrMltSession session) {
		if (session != null) {
			session.setLastActiveSessionTime(LocalDateTime.now());
			Optional<IvrMltSession> userSession = Optional.ofNullable(redisCache.save(session));
			return userSession.orElseThrow(
					() -> new BadUserInputException(session.getSessionId(), "Cannot Update MLT Session in Database"));
		} else {
			return null;
		}
	}

	@CacheEvict(value = TECH_CACHE, key = "#session.sessionId")
	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteSession(IvrMltSession session) {
		redisCache.deleteById(session.getSessionId());
	}
}
