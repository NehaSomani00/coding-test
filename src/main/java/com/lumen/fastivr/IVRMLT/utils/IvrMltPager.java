package com.lumen.fastivr.IVRMLT.utils;

import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.lumen.fastivr.IVRMLT.utils.IVRMltConstants.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lumen.fastivr.IVRDto.NETMessagingRequestDto;
import com.lumen.fastivr.IVRLFACS.IVRLfacsServiceHelper;
import com.lumen.fastivr.IVRMLT.caching.IvrMltCacheService;
import com.lumen.fastivr.IVRMLT.entity.IvrMltSession;
import com.lumen.fastivr.IVRSessionManagement.IVRUserSession;
import com.lumen.fastivr.IVRUtils.IVRConstants;

@Service
public class IvrMltPager {
	
	final static Logger LOGGER = LoggerFactory.getLogger(IvrMltPager.class);
	
	@Autowired
	private IVRLfacsServiceHelper ivrLfacsServiceHelper;
	
	@Autowired
	private IvrMltCacheService mltCacheService;
	
	
	public boolean sendPage(String pagertext, String device, IVRUserSession session) {
		boolean result=false;
		String mltSubject=getMltPageTextSubject(session.getSessionId());
		try {
			result= ivrLfacsServiceHelper.sendTestResultToTech(mltSubject, pagertext, device, session);
		} catch (JsonProcessingException e) {
			//page send failed
			LOGGER.error("Page failed for MLT Test result, error:" ,e);
		}
		return result;
	}
	
	
	public String getMltPageTextSubject(String sessionId) {
		
		IvrMltSession mltSession = mltCacheService.getBySessionId(sessionId);
		String pageTextSub=IVRMltConstants.NET_MAIL_SUBJECT_MLT;
		
		if(mltSession!=null && StringUtils.isNotBlank(mltSession.getTestType()))
		{
			switch(mltSession.getTestType())
			{
				case IVRMltUtilities.QUICKX_TEST:
						pageTextSub=IVRMltConstants.NET_MAIL_SUBJECT_MLT_QUICK_TEST;
						break;
			
				case IVRMltUtilities.LOOPX_TEST:
						pageTextSub=IVRMltConstants.NET_MAIL_SUBJECT_MLT_LOOP_TEST;
						break;
			
				case IVRMltUtilities.FULLX_TEST:
						pageTextSub=IVRMltConstants.NET_MAIL_SUBJECT_MLT_FULL_TEST;
						break;
			
				case IVRMltUtilities.TONE_PLUS_TEST:
						pageTextSub=IVRMltConstants.NET_MAIL_SUBJECT_MLT_TONE_PLUS_TEST;
						break;
			
				case IVRMltUtilities.TONE_REMOVAL_TEST:
						pageTextSub=IVRMltConstants.NET_MAIL_SUBJECT_MLT_TONE_REMOVAL_TEST;
						break;
			}
		}
		return pageTextSub;
	}
	

}
