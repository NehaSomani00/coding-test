package com.lumen.fastivr.IVRRepository;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.lumen.fastivr.IVREntity.Npa;

@Repository
public interface NpaRepository extends JpaRepository<Npa, String> {
	
	@Transactional(propagation = Propagation.REQUIRED, isolation =  Isolation.READ_COMMITTED)
	List<Npa> findByNpaPrefix(String npaPrefix);

}
