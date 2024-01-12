package com.lumen.fastivr.IVRRepository;

import com.lumen.fastivr.IVREntity.FastIvrUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface FastIvrDBInterface extends JpaRepository<FastIvrUser, String> {

	@Transactional(propagation = Propagation.REQUIRED, isolation =  Isolation.READ_COMMITTED)
	Optional<FastIvrUser> findByempID(String empId);
	
	@Transactional(propagation = Propagation.REQUIRED, isolation =  Isolation.READ_COMMITTED)
	@Query("SELECT t.password FROM FastIvrUser t where t.empID = :empID")
	Optional<String> findPasswordByempID(@Param(value = "empID") String empID);
	
	/*
	 * Derived query for spring-data-jpa
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	@Modifying
	@Query("UPDATE FastIvrUser t set t.loginJeopardyFlag = :jep where t.empID = :empID")
	int updateLoginJeopardyFlagByempID(@Param(value = "jep") String jep, @Param(value = "empID") String empID);

	@Transactional(propagation = Propagation.REQUIRED)
	@Modifying
	@Query("UPDATE FastIvrUser t set t.npaPrefix = :newAreaCode where t.empID = :empID")
	int updateAreaCodeByempID(@Param(value = "newAreaCode") String newAreaCode, @Param(value = "empID") String empID);

	@Transactional(propagation = Propagation.REQUIRED)
	@Modifying
	@Query("UPDATE FastIvrUser t set t.password = :newPassword where t.empID = :empID")
	int updatePasswordByempID(@Param(value = "newPassword") String newPassword, @Param(value = "empID") String empID);


	@Transactional(propagation = Propagation.REQUIRED)
	@Modifying
	@Query("UPDATE FastIvrUser t set t.passwordCounter = :passwordCounter where t.empID = :empID")
	int updatePasswordCounterByempID(@Param(value = "passwordCounter") int passwordCounter,
			@Param(value = "empID") String empID);

	@Transactional(propagation = Propagation.REQUIRED)
	@Modifying
	@Query("UPDATE FastIvrUser t set t.birthdate = :date where t.empID = :empID")
	int updateBirthdateByEmpID(@Param(value = "date") String date, @Param(value = "empID") String empID);
	
	@Transactional(propagation = Propagation.REQUIRED)
	@Modifying
	@Query("UPDATE FastIvrUser t set t.expireDate = :date where t.empID = :empID")
	int updatePasswordExpireByEmpID(@Param(value = "date") LocalDate date, @Param(value = "empID") String empID);
	
	@Transactional(propagation = Propagation.REQUIRED)
	@Modifying
	@Query("UPDATE FastIvrUser t set t.cutPageSent = :flag WHERE t.empID = :empid")
	int updateCutPageFlagByEmpID(@Param(value = "flag") String flag , @Param(value = "empid") String empid);

}
