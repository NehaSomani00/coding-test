package com.lumen.fastivr.IVRAppPropertyLoader;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.util.Optionals;
import org.springframework.stereotype.Service;

@Service
public class IvrDbPropertyCacheService {
	
	@Autowired
	private IvrDbPropertyRepository propertyRepository;

	private static final String TECH_CACHE = "DB_PROPERTY_CACHE";
	
	@Cacheable(value = TECH_CACHE, key = "#name")
	public String getValueByName(String name) {
		 Optional<IvrDbProperty> optional = propertyRepository.findByName(name);
		 if(optional.isPresent() && optional.get().getEnable().equalsIgnoreCase("Y")) {
			 return optional.get().getValue();
		 } else {
			 return "OFF";
		 }
		
	}

}
