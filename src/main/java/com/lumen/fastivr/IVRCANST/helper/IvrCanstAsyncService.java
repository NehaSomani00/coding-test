package com.lumen.fastivr.IVRCANST.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumen.fastivr.IVRCANST.Dto.UpdateLoopResponseDto;
import com.lumen.fastivr.IVRCANST.entity.IVRCanstEntity;
import com.lumen.fastivr.IVRDto.common.IVRHttpResponseDto;
import com.lumen.fastivr.IVRLFACS.IVRLfacsServiceHelper;
import com.lumen.fastivr.IVRSessionManagement.IVRUserSession;
import com.lumen.fastivr.IVRUtils.IVRConstants;
import com.lumen.fastivr.httpclient.IVRHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.net.http.HttpTimeoutException;
import java.util.concurrent.CompletableFuture;

import static com.lumen.fastivr.IVRCNF.utils.IVRCNFConstants.UPDATE_LOOP_REQUEST;
import static com.lumen.fastivr.IVRUtils.IVRConstants.NET_MAIL_DEVICE;
import static com.lumen.fastivr.IVRUtils.IVRConstants.NET_PHONE_DEVICE;

@Service
public class IvrCanstAsyncService {

    private static final Logger LOGGER = LoggerFactory.getLogger(IvrCanstAsyncService.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IVRHttpClient ivrHttpClient;

    @Autowired
    private IVRLfacsServiceHelper ivrLfacsServiceHelper;


    @Async("threadPoolTaskExecutor-canst")
    public CompletableFuture<String> updateLoopRequest(String sessionId, String jsonRequest, String updateLoopUrl, IVRUserSession userSession, IVRCanstEntity canstEntity) throws HttpTimeoutException, JsonProcessingException {

        String pagerText= new String();
        String device= new String();
        UpdateLoopInfoPagerText updateLoopInfoPagerText = new UpdateLoopInfoPagerText();

        LOGGER.info("Execute method with configured executor - " + Thread.currentThread().getName());
        LOGGER.info("Session id: " + sessionId + " updateLoopRequest start");

        IVRHttpResponseDto responseDto = ivrHttpClient.httpPostApiCall(jsonRequest, updateLoopUrl,
                sessionId, UPDATE_LOOP_REQUEST);
        String responseString = responseDto.getResponseBody();

        String cleanJsonStr = ivrLfacsServiceHelper.cleanResponseString(responseString);

        canstEntity.setUpdateLoopResp(cleanJsonStr);


        UpdateLoopResponseDto responseObject = objectMapper.readValue(cleanJsonStr,
                UpdateLoopResponseDto.class);

        if ("S".equalsIgnoreCase(responseObject.getMessageStatus().getErrorStatus())) {
            pagerText = updateLoopInfoPagerText.createUpdateLoopInfoSuccessPage(canstEntity);
        } else {
            updateLoopInfoPagerText.createUpdateLoopInfoFailurePage(responseObject.getMessageStatus().getHostErrorList().get(0).getErrorList().get(0).getErrorMessage(),
                    canstEntity);
        }
        if (userSession.isCanBePagedMobile()) {
           device = NET_PHONE_DEVICE;
        }
        else {
            device = NET_MAIL_DEVICE;
        }

        ivrLfacsServiceHelper.sendTestResultToTech("CANST pager message", pagerText, device, userSession);

        return CompletableFuture.completedFuture(IVRConstants.SUCCESS);

    }


}
