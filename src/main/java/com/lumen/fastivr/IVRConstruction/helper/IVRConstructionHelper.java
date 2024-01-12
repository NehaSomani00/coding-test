package com.lumen.fastivr.IVRConstruction.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumen.fastivr.IVRConstruction.Dto.ConstructionActivityResponse;
import com.lumen.fastivr.IVRDto.retrieveLoopAssignment.RetrieveLoopAssignmentRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IVRConstructionHelper {

    @Autowired
    private ObjectMapper objectMapper;

    final static Logger LOGGER = LoggerFactory.getLogger(IVRConstructionHelper.class);

    public RetrieveLoopAssignmentRequest buildReferTroubleReportRequest(String tn, String npa, String nxx, String npaState, String openNbr) {

        RetrieveLoopAssignmentRequest request = new RetrieveLoopAssignmentRequest();
        //request.setNPA(npa);
        //request.setNXX(nxx);
        //request.setTN(tn);
        //request.setNPAState(npaState);
        //request.setOpenNbr(openNbr);
        //request.setDirNum(tn.substring(6));

        return request;

    }

    public ConstructionActivityResponse extractDetailsFromConstructionResponse(String jsonString)
            throws JsonProcessingException {
        ConstructionActivityResponse constructionActivityResponse = objectMapper.readValue(jsonString, ConstructionActivityResponse.class);
        return constructionActivityResponse;
    }
}
