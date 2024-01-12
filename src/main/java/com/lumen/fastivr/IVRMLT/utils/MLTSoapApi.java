package com.lumen.fastivr.IVRMLT.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.NoArgsConstructor;

import static com.lumen.fastivr.IVRMLT.utils.IVRMltConstants.*;

@NoArgsConstructor
@Service
public class MLTSoapApi {
	
	//Denver apis 
	@Value("${mlt.soap.api.denver.central.MDataChannelFactoryAPIProxySoap}")
	private String denverCentralDatachannel;
	@Value("${mlt.soap.api.denver.central.MLTSessionAPIProxySoap}")
	private String denverCentralMLTSession;
	@Value("${mlt.soap.api.denver.central.TestRequestAPIProxySoap}")
	private String denverCentraTestRequest;
	
	@Value("${mlt.soap.api.denver.western.MDataChannelFactoryAPIProxySoap}")
	private String denverWesternDatachannel;
	@Value("${mlt.soap.api.denver.western.MLTSessionAPIProxySoap}")
	private String denverWesternMLTSession;
	@Value("${mlt.soap.api.denver.western.TestRequestAPIProxySoap}")
	private String denverWesternTestRequest;
	
	@Value("${mlt.soap.api.denver.eastern.MDataChannelFactoryAPIProxySoap}")
	private String denverEasternDatachannel;
	@Value("${mlt.soap.api.denver.eastern.MLTSessionAPIProxySoap}")
	private String denverEasternMLTSession;
	@Value("${mlt.soap.api.denver.eastern.TestRequestAPIProxySoap}")
	private String denverEasternTestRequest;
	
	//Omaha apis 
	@Value("${mlt.soap.api.omaha.central.MDataChannelFactoryAPIProxySoap}")
	private String omahaCentralDatachannel;
	@Value("${mlt.soap.api.omaha.central.MLTSessionAPIProxySoap}")
	private String omahaCentralMLTSession;
	@Value("${mlt.soap.api.omaha.central.TestRequestAPIProxySoap}")
	private String omahaCentraTestRequest;
	
	@Value("${mlt.soap.api.omaha.western.MDataChannelFactoryAPIProxySoap}")
	private String omahaWesternDatachannel;
	@Value("${mlt.soap.api.omaha.western.MLTSessionAPIProxySoap}")
	private String omahaWesternMLTSession;
	@Value("${mlt.soap.api.omaha.western.TestRequestAPIProxySoap}")
	private String omahaWesternTestRequest;
	
	@Value("${mlt.soap.api.omaha.eastern.MDataChannelFactoryAPIProxySoap}")
	private String omahaEasternDatachannel;
	@Value("${mlt.soap.api.omaha.eastern.MLTSessionAPIProxySoap}")
	private String omahaEasternMLTSession;
	@Value("${mlt.soap.api.omaha.eastern.TestRequestAPIProxySoap}")
	private String omahaEasternTestRequest;
	
	private String datachannel;
	private String mltSession;
	private String testRequest;
	
	private MLTSoapApi(MltSoapApiBuilder builder) {
		this.datachannel = builder.datachannel;
		this.mltSession = builder.mltSession;
		this.testRequest = builder.testRequest;
	}
	
	public String getDatachannel() {
		return datachannel;
	}

	public String getMltSession() {
		return mltSession;
	}

	public String getTestRequest() {
		return testRequest;
	}

	public class MltSoapApiBuilder {
		
		private String datachannel;
		private String mltSession;
		private String testRequest;
		
		private String region;
		private String dataCentre;
		
		public MltSoapApiBuilder(String region, String dataCentre) {
			this.region = region;
			this.dataCentre = dataCentre;
		}
		
		public MltSoapApiBuilder datachannelProxy() {
			this.datachannel = this.getDataChannel(region, dataCentre);
			return this;
		}
		
		public MltSoapApiBuilder mltSessionProxy() {
			this.mltSession = this.getMLtUserSession(region, dataCentre);
			return this;
		}
		
		public MltSoapApiBuilder testRequestProxy() {
			this.testRequest = this.getTestRequest(region, dataCentre);
			return this;
		}
		
		private String getDataChannel(String region, String dc) {
			String url = "";
			switch(dc) {
			case MLT_DC_DENVER:
				switch(region) {
				case MLT_REGION_CENTRAL:
					url = denverCentralDatachannel;
					break;
				case MLT_REGION_EASTERN:
					url = denverEasternDatachannel;
					break;
				case MLT_REGION_WESTERN:
					url = denverWesternDatachannel;
					break;
				}
				break;
				
			case MLT_DC_OMAHA:
				switch(region) {
				case MLT_REGION_CENTRAL:
					url = omahaCentralDatachannel;
					break;
				case MLT_REGION_EASTERN:
					url = omahaEasternDatachannel;
					break;
				case MLT_REGION_WESTERN:
					url = omahaWesternDatachannel;
					break;
				}
				break;
			}
			return url;
		}
		
		private String getTestRequest(String region, String dc) {
			String url = "";
			switch(dc) {
			case MLT_DC_DENVER:
				switch(region) {
				case MLT_REGION_CENTRAL:
					url = denverCentraTestRequest;
					break;
				case MLT_REGION_EASTERN:
					url = denverEasternTestRequest;
					break;
				case MLT_REGION_WESTERN:
					url = denverWesternTestRequest;
					break;
				}
				break;
				
			case MLT_DC_OMAHA:
				switch(region) {
				case MLT_REGION_CENTRAL:
					url = omahaCentraTestRequest;
					break;
				case MLT_REGION_EASTERN:
					url = omahaEasternTestRequest;
					break;
				case MLT_REGION_WESTERN:
					url = omahaWesternTestRequest;
					break;
				}
				break;
			}
			return url;
		}
		
		private String getMLtUserSession(String region, String dc) {
			String url = "";
			switch(dc) {
			case MLT_DC_DENVER:
				switch(region) {
				case MLT_REGION_CENTRAL:
					url = denverCentralMLTSession;
					break;
				case MLT_REGION_EASTERN:
					url = denverEasternMLTSession;
					break;
				case MLT_REGION_WESTERN:
					url = denverWesternMLTSession;
					break;
				}
				break;
				
			case MLT_DC_OMAHA:
				switch(region) {
				case MLT_REGION_CENTRAL:
					url = omahaCentralMLTSession;
					break;
				case MLT_REGION_EASTERN:
					url = omahaEasternMLTSession;
					break;
				case MLT_REGION_WESTERN:
					url = omahaWesternMLTSession;
					break;
				}
				break;
			}
			return url;
		}
		
		public MLTSoapApi build() {
			return new MLTSoapApi(this);
		}
		
	}
	
	
}
