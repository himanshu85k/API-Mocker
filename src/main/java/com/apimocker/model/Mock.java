package com.apimocker.model;

import lombok.Data;
import org.springframework.http.HttpMethod;

import java.util.HashMap;

@Data
public class Mock {
    private HttpMethod httpMethod;
    private HashMap<String, String> requestQueryParams;
    private String requestBody;
    private HashMap<String, String> requestHeaders;

    private String responseBody;
}
