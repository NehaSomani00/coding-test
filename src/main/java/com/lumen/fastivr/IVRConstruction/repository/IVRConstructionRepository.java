package com.lumen.fastivr.IVRConstruction.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.lumen.fastivr.IVRConstruction.entity.IvrConstructionSession;

public interface IVRConstructionRepository extends JpaRepository<IvrConstructionSession, String> {

	
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
	Optional<IvrConstructionSession> findBySessionId(String sessionId);
}
