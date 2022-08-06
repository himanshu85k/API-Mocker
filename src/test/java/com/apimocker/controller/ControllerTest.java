package com.apimocker.controller;

import com.apimocker.helper.MockConfigurationFileHelper;
import com.apimocker.helper.MockConfigurationFileHelperTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.MalformedURLException;

@RunWith(MockitoJUnitRunner.class)
public class ControllerTest {

    Controller controller;
    HttpServletRequest httpServletRequestMocked;

    @Before
    public void setup() throws IOException {
        try (MockedStatic<MockConfigurationFileHelper> mockConfigurationFileHelperMockedStatic = Mockito.mockStatic(MockConfigurationFileHelper.class)) {
            mockConfigurationFileHelperMockedStatic.when(() -> MockConfigurationFileHelper.readJsonFromUrl("test"))
                    .thenReturn(MockConfigurationFileHelperTest.MOCK_CONFIGURATION);
            controller = new Controller("test");
        }
        httpServletRequestMocked = Mockito.mock(HttpServletRequest.class);
    }

    @Test
    public void testRootWithMatchingMock1() throws MalformedURLException, JsonProcessingException {
        Mockito.doReturn("POST").when(httpServletRequestMocked).getMethod();
        Mockito.doReturn(new StringBuffer("http://testURLisWorking.com/path1"))
                .when(httpServletRequestMocked).getRequestURL();
        Mockito.doReturn("value11").when(httpServletRequestMocked).getParameter("param1");
        Mockito.doReturn("headerValue11").when(httpServletRequestMocked).getHeader("header1");
        ResponseEntity<JsonNode> response = controller.root(httpServletRequestMocked, "{}");
        Assert.assertEquals("Response body is correct",
                "{\"responseBodyParam11\":\"responseBodyValue11\"}", response.getBody().toString());
    }

    @Test
    public void testRootWithMatchingMock2() throws MalformedURLException, JsonProcessingException {
        Mockito.doReturn("PUT").when(httpServletRequestMocked).getMethod();
        Mockito.doReturn(new StringBuffer("http://testURLisWorking.com/path2"))
                .when(httpServletRequestMocked).getRequestURL();
        Mockito.doReturn("value1").when(httpServletRequestMocked).getParameter("param21");
        Mockito.doReturn("value2").when(httpServletRequestMocked).getParameter("param22");
        Mockito.doReturn("headerValue21").when(httpServletRequestMocked).getHeader("header21");
        Mockito.doReturn("headerValue22").when(httpServletRequestMocked).getHeader("header22");
        ResponseEntity<JsonNode> response = controller.root(httpServletRequestMocked,
                "{\"requestBodyParam21\":\"requestBodyValue21\"}");
        Assert.assertEquals("Response body is correct",
                "{\"responseBodyParam21\":\"responseBodyValue21\"}", response.getBody().toString());
    }

    @Test
    public void testRootWithNoMatchingRequests() throws MalformedURLException, JsonProcessingException {
        Mockito.doReturn("POST").when(httpServletRequestMocked).getMethod();
        Mockito.doReturn(new StringBuffer("http://testURLisWorking.com/path1"))
                .when(httpServletRequestMocked).getRequestURL();
        ResponseEntity<JsonNode> response = controller.root(httpServletRequestMocked, "");
        Assert.assertEquals("Response body contains correct error message",
                "{\"error\":\"No matching mocks found\"}", response.getBody().toString());
        Assert.assertEquals("Response body contains correct status code",
                "404 NOT_FOUND", response.getStatusCode().toString());
    }

    @Test
    public void testRootWithBadIncomingRequestBody() throws MalformedURLException, JsonProcessingException {
        Mockito.doReturn("PUT").when(httpServletRequestMocked).getMethod();
        Mockito.doReturn(new StringBuffer("http://testURLisWorking.com/path2"))
                .when(httpServletRequestMocked).getRequestURL();
        ResponseEntity<JsonNode> response = controller.root(httpServletRequestMocked,
                "{\"badJSON");
        Assert.assertEquals("Response body contains correct error message",
                "{\"error\":\"Unable to process incoming request body as JSON\"}", response.getBody().toString());
        Assert.assertEquals("Response body contains correct status code",
                "404 NOT_FOUND", response.getStatusCode().toString());
    }

    @Test
    public void testRootWithNoMocks() throws IOException {
        try (MockedStatic<MockConfigurationFileHelper> mockConfigurationFileHelperMockedStatic = Mockito.mockStatic(MockConfigurationFileHelper.class)) {
            mockConfigurationFileHelperMockedStatic.when(() -> MockConfigurationFileHelper.readJsonFromUrl("test"))
                    .thenReturn("");
            controller = new Controller("test");

            Mockito.doReturn("PUT").when(httpServletRequestMocked).getMethod();
            Mockito.doReturn(new StringBuffer("http://testURLisWorking.com/path2"))
                    .when(httpServletRequestMocked).getRequestURL();
            ResponseEntity<JsonNode> response = controller.root(httpServletRequestMocked,
                    "{\"requestBodyParam21\":\"requestBodyValue21\"}");
            Assert.assertEquals("Response body contains correct error message",
                    "{\"error\":\"No matching mocks found\"}", response.getBody().toString());
            Assert.assertEquals("Response body contains correct status code",
                    "404 NOT_FOUND", response.getStatusCode().toString());
        }
    }



}
