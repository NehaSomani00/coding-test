package com.lumen.fastivr.IVRMLT.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.lumen.fastivr.IVRMLT.entity.IvrMltSession;

@Repository
public interface IvrMltSessionRepository extends JpaRepository<IvrMltSession, String>{
	
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
	Optional<IvrMltSession> findBySessionId(String sessionId);

}
