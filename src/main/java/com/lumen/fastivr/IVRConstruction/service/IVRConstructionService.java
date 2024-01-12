package com.lumen.fastivr.IVRConstruction.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.lumen.fastivr.IVRDto.IVRWebHookResponseDto;

import java.util.List;

public interface IVRConstructionService {

	IVRWebHookResponseDto processCTD500(String sessionId, String currentState, List<String> cleanDtmfInputList) throws JsonMappingException, JsonProcessingException;

	IVRWebHookResponseDto checkOpeningNumberExists(String sessionId) throws JsonProcessingException;

	IVRWebHookResponseDto parseTelephone(String userInput, String sessionId);

	IVRWebHookResponseDto issueReferTroubleReport(String userInput, String sessionId) throws JsonProcessingException;
}
