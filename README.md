# API Mocker

This project enables to mock REST endpoints by stubbing mock API requests and responses.

Mocks can be configured in a json file as shown below:
```
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
      "responseBody": "{\"responseBodyParam11\":\"responseBodyValue11\"}"
    },
    {
      "httpMethod": "POST",
      "requestQueryParams": {
        "param11": "value12"
      },
      "requestHeaders": {
        "header1": "headerValue1"
      },
      "responseBody": "{\"responseBodyParam12\":\"responseBodyValue12\"}"
    }
  ],
  "/path2": [
    {
      "httpMethod": "PUT",
      "requestQueryParams": {
        "param21": "value1",
        "param22": "value2"
      },
      "requestBody": "{\"requestBodyParam21\":\"requestBodyValue21\"}",
      "requestHeaders": {
        "header21": "headerValue21",
        "header22": "headerValue22"
      },
      "responseBody": "{\"responseBodyParam21\":\"responseBodyValue21\"}"
    }
  ]
}
```


