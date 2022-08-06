package com.apimocker.helper;

import com.apimocker.model.Mock;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@RunWith(MockitoJUnitRunner.class)
public class MockMatchHelperTest {
    @Test
    public void testIsBodyMatching() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode incomingRequestBody = mapper.createObjectNode();
        incomingRequestBody.put("key", "value");

        Mock mockCandidate = new Mock();
        mockCandidate.setRequestBody("""
                {
                    "key": "value"
                }
                """);
        Assert.assertTrue(MockMatchHelper.isBodyMatching(incomingRequestBody, mockCandidate));
    }

    @Test
    public void testIsBodyMatchingWithEmptyMockCandidateResponseBody() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode incomingRequestBody = mapper.createObjectNode();
        incomingRequestBody.put("key", "value");

        Mock mockCandidate = new Mock();
        Assert.assertTrue(MockMatchHelper.isBodyMatching(incomingRequestBody, mockCandidate));
    }

    @Test
    public void testIsBodyMatchingWithBadIncomingRequestBody() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode incomingRequestBody = mapper.createObjectNode();
        incomingRequestBody.put("badKey", "value");

        Mock mockCandidate = new Mock();
        mockCandidate.setRequestBody("""
                {
                    "key": "value"
                }
                """);
        Assert.assertFalse(MockMatchHelper.isBodyMatching(incomingRequestBody, mockCandidate));
    }

    @Test
    public void testIsBodyMatchingWithBadMockCandidateRequestBody() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode incomingRequestBody = mapper.createObjectNode();
        incomingRequestBody.put("badKey", "value");

        Mock mockCandidate = new Mock();
        mockCandidate.setRequestBody("""
                { "key":
                """);
        Assert.assertFalse(MockMatchHelper.isBodyMatching(incomingRequestBody, mockCandidate));
    }

    @Test
    public void testIsQueryParamMatching() {
        HttpServletRequest incomingRequest = Mockito.mock(HttpServletRequest.class);
        Mockito.when(incomingRequest.getParameter("param1")).thenReturn("value1");
        Mockito.when(incomingRequest.getParameter("param2")).thenReturn("value2");

        Mock mockCandidate = new Mock();
        HashMap<String, String> queryParams = new HashMap<>();
        queryParams.put("param1", "value1");
        queryParams.put("param2", "value2");
        mockCandidate.setRequestQueryParams(queryParams);

        Assert.assertTrue("Query params match",
                MockMatchHelper.isQueryParamMatching(incomingRequest, mockCandidate));
    }

    @Test
    public void testIsQueryParamMatchingMissingIncomingRequestParam() {
        HttpServletRequest incomingRequest = Mockito.mock(HttpServletRequest.class);
        Mockito.when(incomingRequest.getParameter("param1")).thenReturn("value1");

        Mock mockCandidate = new Mock();
        HashMap<String, String> queryParams = new HashMap<>();
        queryParams.put("param1", "value1");
        queryParams.put("param2", "value2");
        mockCandidate.setRequestQueryParams(queryParams);

        Assert.assertFalse("Query params do not match when an incoming request param is missing",
                MockMatchHelper.isQueryParamMatching(incomingRequest, mockCandidate));
    }

    @Test
    public void testIsQueryParamMatchingWithNoMockCandidateRequestParams() {
        HttpServletRequest incomingRequest = Mockito.mock(HttpServletRequest.class);

        Assert.assertTrue("Query params match when Mock Candidate's has no request params configure",
                MockMatchHelper.isQueryParamMatching(incomingRequest, new Mock()));
    }

    @Test
    public void testIsHeaderMatching() {
        HttpServletRequest incomingRequest = Mockito.mock(HttpServletRequest.class);
        Mockito.when(incomingRequest.getHeader("header1")).thenReturn("value1");
        Mockito.when(incomingRequest.getHeader("header2")).thenReturn("value2");

        Mock mockCandidate = new Mock();
        HashMap<String, String> headers = new HashMap<>();
        headers.put("header1", "value1");
        headers.put("header2", "value2");
        mockCandidate.setRequestHeaders(headers);

        Assert.assertTrue("Headers match", MockMatchHelper.isHeaderMatching(incomingRequest, mockCandidate));
    }

    @Test
    public void testIsHeaderMatchingMissingIncomingRequestParam() {
        HttpServletRequest incomingRequest = Mockito.mock(HttpServletRequest.class);
        Mockito.when(incomingRequest.getHeader("header1")).thenReturn("value1");

        Mock mockCandidate = new Mock();
        HashMap<String, String> headers = new HashMap<>();
        headers.put("header1", "value1");
        headers.put("header2", "value2");
        mockCandidate.setRequestHeaders(headers);

        Assert.assertFalse("Headers do not match when an incoming request header is missing",
                MockMatchHelper.isHeaderMatching(incomingRequest, mockCandidate));
    }

    @Test
    public void testIsHeaderMatchingWithNoMockCandidateRequestParams() {
        HttpServletRequest incomingRequest = Mockito.mock(HttpServletRequest.class);

        Assert.assertTrue("Headers match when Mock Candidate's has no request headers configure",
                MockMatchHelper.isHeaderMatching(incomingRequest, new Mock()));
    }
}
