package com.lumen.fastivr.IVRRepository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.lumen.fastivr.IVREntity.MnetEmp;

@Repository
public interface FastIvrMnetRepository extends JpaRepository<MnetEmp, String> {

	@Transactional(readOnly = true)
    public MnetEmp findByCuid(String cuid);
	
	@Transactional(readOnly = true)
    @Query("SELECT m.lastName FROM MnetEmp m WHERE m.cuid = ?1")
	public Optional<String> findLastNameByCuid(String cuid);

}
