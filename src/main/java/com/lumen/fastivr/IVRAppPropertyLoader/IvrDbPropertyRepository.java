package com.lumen.fastivr.IVRAppPropertyLoader;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IvrDbPropertyRepository extends JpaRepository<IvrDbProperty, Integer> {
	
	Optional<IvrDbProperty> findByName(String name);

}
