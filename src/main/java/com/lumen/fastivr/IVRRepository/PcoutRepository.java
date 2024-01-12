package com.lumen.fastivr.IVRRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lumen.fastivr.IVREntity.Pcout;

@Repository
public interface PcoutRepository extends JpaRepository<Pcout, String> {

	@Query("select count(*) from Pcout p where p.pgCntr = :pgCntrALL")
	int countPageCentreALL(@Param(value= "pgCntrALL")String pgCntrALL);
	
	@Query("select count(*) from FastIvrUser t, Pcout p where t.pagerCo = p.pgCntr and t.empID = :empID")
	int countPagerByEmp(@Param(value = "empID") String empID);
}
