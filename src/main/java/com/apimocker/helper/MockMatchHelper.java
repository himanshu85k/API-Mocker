package com.apimocker.helper;

import com.apimocker.model.Mock;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class MockMatchHelper {

    private static final Logger logger = LogManager.getLogger(MockMatchHelper.class);

    public static boolean isBodyMatching(JsonNode incomingRequestBodyJsonNode, Mock mockCandidate) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String mockCandidateRequestBody = mockCandidate.getRequestBody();
            if (mockCandidateRequestBody == null || mockCandidateRequestBody.isEmpty()) {
                return true;
            }
            JsonNode candidateBodyJsonNode = mapper.readTree(mockCandidateRequestBody);
            if (candidateBodyJsonNode.equals(incomingRequestBodyJsonNode)) {
                return true;
            }
        } catch (JsonProcessingException e) {
            logger.error("Unable to process mockCandidate's request body", e);
            return false;
        }
        return false;
    }


    public static boolean isQueryParamMatching(HttpServletRequest incomingRequest, Mock mockCandidate) {
        AtomicBoolean queryParamsMatch = new AtomicBoolean(true);
        HashMap<String, String> mockCandidateQueryParams = mockCandidate.getRequestQueryParams();
        if (mockCandidateQueryParams == null || mockCandidateQueryParams.isEmpty()) {
            return true;
        }
        mockCandidateQueryParams.forEach((key, value) -> {
            String incomingRequestQueryParam = incomingRequest.getParameter(key);
            // check if incomingRequestQueryParam's value matches to the one expected in mock configuration
            if (incomingRequestQueryParam == null || !incomingRequestQueryParam.equals(value)) {
                queryParamsMatch.set(false);
            }
        });
        return queryParamsMatch.get();
    }

    public static boolean isHeaderMatching(HttpServletRequest incomingRequest, Mock mockCandidate) {
        AtomicBoolean headerMatch = new AtomicBoolean(true);
        HashMap<String, String> mockCandidateHeaders = mockCandidate.getRequestHeaders();
        if (mockCandidateHeaders == null || mockCandidateHeaders.isEmpty()) {
            return true;
        }
        mockCandidateHeaders.forEach((key, value) -> {
            String incomingRequestHeaderValue = incomingRequest.getHeader(key);
            // check if incomingRequestHeaderValue's value matches to the one expected in mock configuration
            if (incomingRequestHeaderValue == null || !incomingRequestHeaderValue.equals(value)) {
                headerMatch.set(false);
            }
        });
        return headerMatch.get();
    }
}
