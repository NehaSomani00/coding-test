package com.lumen.fastivr.IVRLFACS;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.lumen.fastivr.IVRBusinessException.BusinessException;
import com.lumen.fastivr.IVRDto.LOSDB.CTelephone;
import com.lumen.fastivr.IVRDto.LOSDB.TNInfoResponse;
import com.lumen.fastivr.IVREntity.NpaId;
import com.lumen.fastivr.IVREntity.TNInfo;
import com.lumen.fastivr.IVRRepository.TnInfoRepository;

@Service
public class IVRLOSDBManager {
	
	@Value("${losdb.gettninfo.url}")
	private String tnInfoUrl;
	
	@Value("${losdb.gettninfo.serviceprovider.ids}")
	private String serviceProviderIds;
	
	@Autowired
	TnInfoRepository tnRepository;
	
	@Autowired
	private HttpClient httpClient;
	
	final static Logger LOGGER = LoggerFactory.getLogger(IVRLOSDBManager.class);
	
	//makes the actual LOS DB rest api call
	public String getTnInfo(CTelephone requestDto, String sessionid) {
		URI uri = buildGetTnInfoURI(requestDto);
		HttpRequest httpRequest = HttpRequest.newBuilder()
				.uri(uri)
				.GET()
				.build();
		
		CompletableFuture<String> futureResponse = httpClient.sendAsync(httpRequest, BodyHandlers.ofString())
		.thenApply(response -> response.body());
		
		LOGGER.info("Session id:"+ sessionid +", LOS DB GetTNInfo request sent, Request URI: "+ uri.toString());
		String responseString = "";
		try {
			responseString = futureResponse.get();
			LOGGER.info("Session id:"+ sessionid +", GetTNInfo response: "+ responseString);
			return responseString;
		} catch (InterruptedException | ExecutionException e) {
			LOGGER.error("Session id:"+ sessionid +", Error while fetching GetTNInfo response: ", e);
			throw new BusinessException(sessionid, "Error while fetching data from NET Device API");
		}
		
	}
	
	private URI buildGetTnInfoURI(CTelephone tn) {
		URI uri = URI.create(tnInfoUrl + tn.getNpa() + tn.getNxx() + tn.getLineNumber());
		return uri;
	}
	
	public boolean matchServiceProviderId(TNInfoResponse response) {
		String[] serviceProviderArray = serviceProviderIds.split(",");
		boolean isPresent = Arrays.asList(serviceProviderArray).contains(response.getServiceProviderId());
		return isPresent;
	}

	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
	public TNInfo validateNPA(CTelephone dto, String sessionid) {
		NpaId npaId = new NpaId();
		npaId.setNpaPrefix(dto.getNpa());
		npaId.setNxxPrefix(dto.getNxx());
		System.out.println("npa:"+npaId.getNpaPrefix());
		System.out.println("nxa:"+npaId.getNxxPrefix());
		Optional<TNInfo> tnInfoOptional = tnRepository.findById(npaId);
		return tnInfoOptional.orElse(null);
	}
}
