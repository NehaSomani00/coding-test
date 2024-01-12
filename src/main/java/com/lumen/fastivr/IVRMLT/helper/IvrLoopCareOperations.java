package com.lumen.fastivr.IVRMLT.helper;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import org.springframework.stereotype.Service;

import tollgrade.loopcare.testrequestapi.MDataChannelFactoryOperations;
import tollgrade.loopcare.testrequestapi.MDataChannelFactoryOperationsService;
import tollgrade.loopcare.testrequestapi.RequestToMLTOperations;
import tollgrade.loopcare.testrequestapi.RequestToMLTOperationsService;

@Service
public class IvrLoopCareOperations {

	public MDataChannelFactoryOperations dataChannelFactoryOperations(String dataChannelProxyUrl)
			throws MalformedURLException {
		
		URL dataChannelUrl = URI.create(dataChannelProxyUrl).toURL();
		MDataChannelFactoryOperationsService dataChannelService = new MDataChannelFactoryOperationsService(
				dataChannelUrl);
		MDataChannelFactoryOperations dataChannelOps = dataChannelService.getMDataChannelFactoryAPIProxySoap();
		return dataChannelOps;
	}

	public RequestToMLTOperations mltOperations(String testRequestProxyUrl) throws MalformedURLException {
		
		URL mltTestResquestUrl = URI.create(testRequestProxyUrl).toURL();
		RequestToMLTOperationsService requestMltService = new RequestToMLTOperationsService(mltTestResquestUrl);
		RequestToMLTOperations requestMltOps = requestMltService.getTestRequestAPIProxySoap();
		return requestMltOps;
	}

}