package com.lumen.fastivr.IVRCNF.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.lumen.fastivr.IVRCNF.entity.IVRCnfEntity;

public interface IVRCnfRepository extends JpaRepository<IVRCnfEntity, String> {

	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
	Optional<IVRCnfEntity> findBySessionId(String sessionId);
}
