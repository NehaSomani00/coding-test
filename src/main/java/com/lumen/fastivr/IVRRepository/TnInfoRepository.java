package com.lumen.fastivr.IVRRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lumen.fastivr.IVREntity.NpaId;
import com.lumen.fastivr.IVREntity.TNInfo;

@Repository
public interface TnInfoRepository extends JpaRepository<TNInfo, NpaId> {

}
