package com.apimocker.controller;

import com.apimocker.helper.MockConfigurationFileHelper;
import com.apimocker.helper.MockMatchHelper;
import com.apimocker.model.Mock;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

@RestController
public class Controller {

    public static final String NO_MATCHING_MOCKS_FOUND = "No matching mocks found";
    public static final String ERROR = "error";
    private static Logger logger = LogManager.getLogger(Controller.class);
    private String mockConfigURL;
    private HashMap<String, ArrayList<Mock>> mocksMap;

    public Controller(@Value("${mock.config.URL}") String mockConfigURL) throws IOException {
        this.mockConfigURL = mockConfigURL;
        mocksMap = MockConfigurationFileHelper.loadMockConfigurations(mockConfigURL);
    }

    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
            value = "SPRING_CSRF_UNRESTRICTED_REQUEST_MAPPING",
            justification = "Mock server should be able to receive GET, PUT, POST etc requests, " +
                    "hence a generic @RequestMapping annotation is being used")
    @RequestMapping("**")
    public ResponseEntity<JsonNode> root(
            HttpServletRequest incomingRequest,
            @RequestBody(required = false) String incomingRequestBody)
            throws MalformedURLException, JsonProcessingException {
        logger.info("Incoming request: " + incomingRequest.getMethod() + incomingRequest.getRequestURL());

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode errorResponse = mapper.createObjectNode();

        // fetch the mocks configured for incomingRequest path and method
        String mockCandidateSearchKey = incomingRequest.getMethod()
                + new URL(incomingRequest.getRequestURL().toString()).getPath();
        ArrayList<Mock> mockCandidates = mocksMap.get(mockCandidateSearchKey);

        // check if any mocks exist for this incomingRequest path and method
        if (mockCandidates == null || mockCandidates.isEmpty()) {
            logger.error(NO_MATCHING_MOCKS_FOUND);
            errorResponse.put(ERROR, NO_MATCHING_MOCKS_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        // get the incomingRequestBody as JSON
        JsonNode incomingRequestBodyJsonNode = null;
        try {
            if (incomingRequestBody != null && !incomingRequestBody.isEmpty()) {
                incomingRequestBodyJsonNode = mapper.readTree(incomingRequestBody);
            }
        } catch (JsonProcessingException e) {
            logger.error("Unable to process incoming request body as JSON", e);
            errorResponse.put(ERROR, "Unable to process incoming request body as JSON");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        // Iterate over candidates and compare with incoming request, then return the match's response body
        for (Mock mockCandidate : mockCandidates) {
            if (
                    MockMatchHelper.isBodyMatching(incomingRequestBodyJsonNode, mockCandidate)
                            && MockMatchHelper.isQueryParamMatching(incomingRequest, mockCandidate)
                            && MockMatchHelper.isHeaderMatching(incomingRequest, mockCandidate)
            ) {
                // return the matched mockCandidate's responseBody Json
                JsonNode candidateResponseBody = mapper.readTree(mockCandidate.getResponseBody());
                logger.info("Sending mock response: " + candidateResponseBody);
                return ResponseEntity.ok().body(candidateResponseBody);
            }
        }
        logger.error(NO_MATCHING_MOCKS_FOUND);
        errorResponse.put(ERROR, NO_MATCHING_MOCKS_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
}
