package com.apimocker.helper;

import com.apimocker.model.Mock;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

public class MockConfigurationFileHelper {

    private static final Gson gson = new Gson();
    private static final Logger logger = LogManager.getLogger(MockConfigurationFileHelper.class);

    public static HashMap<String, ArrayList<Mock>> loadMockConfigurations(String mockConfigURL) throws IOException {
        HashMap<String, ArrayList<Mock>> mocksMap = new HashMap<>();
        logger.info("Loading mock configuration file URL...");
        String mocksString = readJsonFromUrl(mockConfigURL);
        logger.info("Mock configuration file loaded, parsing the file as json...");
        HashMap<String, ArrayList<Mock>> mocks;
        try {
            mocks = gson.fromJson(mocksString, new TypeToken<HashMap<String, ArrayList<Mock>>>() {
            }.getType());
        } catch (JsonSyntaxException e) {
            logger.error("Unable to parse Mock Configuration as Json Array");
            throw e;
        }
        /*
          Create Hashmap of-
                  key: HTTPMethod + RequestPath
                  value: List of Mocks
         */
        if (mocks != null) {
            logger.info("Json parsing done, Creating Hashmap of mocks...");
            mocks.forEach((path, mocksArray) -> {
                for (Mock mock : mocksArray) {
                    String mocksMapKey = mock.getHttpMethod() + path;
                    if (!mocksMap.containsKey(mocksMapKey)) {
                        mocksMap.put(mocksMapKey, new ArrayList<>());
                    }
                    mocksMap.get(mocksMapKey).add(mock);
                }
            });
            logger.info("Mocks are ready.");
        } else {
            logger.warn("No mocks found.");
        }
        return mocksMap;
    }

    public static String readJsonFromUrl(String link) throws IOException {
        try (InputStream input = new URL(link).openStream()) {
            BufferedReader re = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
            return read(re);
        } catch (Exception e) {
            return null;
        }
    }

    public static String read(Reader re) throws IOException {
        StringBuilder str = new StringBuilder();
        int temp;
        do {
            temp = re.read();
            str.append((char) temp);
        } while (temp != -1);
        return str.toString();
    }
}
