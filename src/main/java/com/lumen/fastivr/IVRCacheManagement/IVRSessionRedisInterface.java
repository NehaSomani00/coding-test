package com.lumen.fastivr.IVRCacheManagement;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.lumen.fastivr.IVRSessionManagement.IVRUserSession;

@Repository
public interface IVRSessionRedisInterface extends JpaRepository<IVRUserSession, String> {
	
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
	Optional<IVRUserSession> findBySessionId(String sessionId);

}
