package com.lumen.fastivr.IVRCANST.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.lumen.fastivr.IVRCANST.entity.IVRCanstEntity;

public interface IVRCanstRepository extends JpaRepository<IVRCanstEntity, String> {

	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
	Optional<IVRCanstEntity> findBySessionId(String sessionId);
}
