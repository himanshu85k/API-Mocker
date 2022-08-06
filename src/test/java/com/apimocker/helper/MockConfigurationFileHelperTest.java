package com.apimocker.helper;

import com.apimocker.model.Mock;
import com.google.gson.JsonSyntaxException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

@RunWith(MockitoJUnitRunner.class)
public class MockConfigurationFileHelperTest {

    public static final String MOCK_CONFIGURATION = """
            {
              "/path1": [
                {
                  "httpMethod": "POST",
                  "requestQueryParams": {
                    "param1": "value11"
                  },
                  "requestHeaders": {
                    "header1": "headerValue11"
                  },
                  "responseBody": "{\\"responseBodyParam11\\":\\"responseBodyValue11\\"}"
                },
                {
                  "httpMethod": "POST",
                  "requestQueryParams": {
                    "param11": "value12"
                  },
                  "requestHeaders": {
                    "header1": "headerValue1"
                  },
                  "responseBody": "{\\"responseBodyParam12\\":\\"responseBodyValue12\\"}"
                }
              ],
              "/path2": [
                {
                  "httpMethod": "PUT",
                  "requestQueryParams": {
                    "param21": "value1",
                    "param22": "value2"
                  },
                  "requestBody": "{\\"requestBodyParam21\\":\\"requestBodyValue21\\"}",
                  "requestHeaders": {
                    "header21": "headerValue21",
                    "header22": "headerValue22"
                  },
                  "responseBody": "{\\"responseBodyParam21\\":\\"responseBodyValue21\\"}"
                }
              ]
            }""";

    @Test
    public void testLoadConfig() throws IOException {
        try (MockedStatic<MockConfigurationFileHelper> mockConfigurationFileHelperMockedStatic = Mockito.mockStatic(MockConfigurationFileHelper.class)) {
            mockConfigurationFileHelperMockedStatic.when(() -> MockConfigurationFileHelper.readJsonFromUrl("test"))
                    .thenReturn(MockConfigurationFileHelperTest.MOCK_CONFIGURATION);

            HashMap<String, ArrayList<Mock>> mocksMap = MockConfigurationFileHelper.loadMockConfigurations(
                    "test");
            Assert.assertEquals("Correct number of mocks were loaded", 2, mocksMap.size());
            ArrayList<Mock> candidatesForPOSTPath1 = mocksMap.get("POST/path1");
            ArrayList<Mock> candidatesForPUTPath2 = mocksMap.get("PUT/path2");

            Assert.assertEquals("Correct number of mocks got mapped to /path1",
                    2, candidatesForPOSTPath1.size());
            Assert.assertEquals("Correct number of query parameters got mapped for /path1",
                    1, candidatesForPOSTPath1.get(0).getRequestQueryParams().size());
            Assert.assertEquals("Request Query param correctly mapped",
                    "value11", candidatesForPOSTPath1.get(0).getRequestQueryParams().get("param1"));
            Assert.assertEquals("Correct number of request headers got mapped for /path1",
                    1, candidatesForPOSTPath1.get(0).getRequestHeaders().size());
            Assert.assertEquals("Request Header correctly mapped for /path1",
                    "headerValue11", candidatesForPOSTPath1.get(0).getRequestHeaders().get("header1"));
            Assert.assertNull(candidatesForPOSTPath1.get(0).getRequestBody());
            Assert.assertEquals("Response body correctly mapped",
                    "{\"responseBodyParam11\":\"responseBodyValue11\"}",
                    candidatesForPOSTPath1.get(0).getResponseBody());

            Assert.assertEquals("Correct number of mocks got mapped to /path2",
                    1, candidatesForPUTPath2.size());
            Assert.assertEquals("Correct number of query parameters got mapped for /path2",
                    2, candidatesForPUTPath2.get(0).getRequestQueryParams().size());
            Assert.assertEquals("First request query param correctly mapped",
                    "value1", candidatesForPUTPath2.get(0).getRequestQueryParams().get("param21"));
            Assert.assertEquals("Second request query param correctly mapped",
                    "value2", candidatesForPUTPath2.get(0).getRequestQueryParams().get("param22"));
            Assert.assertEquals("Correct number of request headers got mapped for /path2",
                    2, candidatesForPUTPath2.get(0).getRequestHeaders().size());
            Assert.assertEquals("Request Header correctly mapped for /path2",
                    "headerValue21", candidatesForPUTPath2.get(0).getRequestHeaders().get("header21"));
            Assert.assertEquals("Request Header correctly mapped for /path2",
                    "headerValue22", candidatesForPUTPath2.get(0).getRequestHeaders().get("header22"));
            Assert.assertEquals("Request body correctly mapped",
                    "{\"requestBodyParam21\":\"requestBodyValue21\"}",
                    candidatesForPUTPath2.get(0).getRequestBody());
            Assert.assertEquals("Response body correctly mapped",
                    "{\"responseBodyParam21\":\"responseBodyValue21\"}",
                    candidatesForPUTPath2.get(0).getResponseBody());

        }
    }

    @Test
    public void testLoadBadConfig() {
        try (MockedStatic<MockConfigurationFileHelper> mockConfigurationFileHelperMockedStatic = Mockito.mockStatic(MockConfigurationFileHelper.class)) {
            mockConfigurationFileHelperMockedStatic.when(() -> MockConfigurationFileHelper.readJsonFromUrl("test2"))
                    .thenReturn("{ \"badJson\": ");
            Exception exception = Assert.assertThrows(JsonSyntaxException.class, () -> {
                HashMap<String, ArrayList<Mock>> mocksMap = MockConfigurationFileHelper.loadMockConfigurations(
                        "test2");
            });
            Assert.assertNotNull(exception);
        }
    }

    @Test
    public void testLoadEmptyConfig() throws IOException {
        try (MockedStatic<MockConfigurationFileHelper> mockConfigurationFileHelperMockedStatic = Mockito.mockStatic(MockConfigurationFileHelper.class)) {
            mockConfigurationFileHelperMockedStatic.when(() -> MockConfigurationFileHelper.readJsonFromUrl("test2"))
                    .thenReturn("");

            HashMap<String, ArrayList<Mock>> mocksMap = MockConfigurationFileHelper.loadMockConfigurations(
                    "test2");
            Assert.assertEquals("No mocks were loaded", 0, mocksMap.size());
        }
    }
}
