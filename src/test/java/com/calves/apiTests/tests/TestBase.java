package com.calves.apiTests.tests;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.RequestOptions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TestBase {
    protected static Playwright playwright;
    protected static APIRequestContext request;
    protected static String accessToken;

    @BeforeAll
    static void setup(){
        playwright = Playwright.create();
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        request = playwright.request().newContext(new APIRequest.NewContextOptions()
                .setBaseURL("http://localhost:8080")
                .setExtraHTTPHeaders(headers));

        // Obtain the access token during setup
        obtainAccessToken();

        // Update the request context with the Authorization header
        if(accessToken != null){
            headers.put("Authorization", "Bearer " + accessToken);
            request.dispose();
            request = playwright.request().newContext(new APIRequest.NewContextOptions()
                    .setBaseURL("http://localhost:8080")
                    .setExtraHTTPHeaders(headers));
        }
    }

    @AfterAll
    static void tearDown(){
        if(request != null){
            request.dispose();
        }
        if(playwright != null){
            playwright.close();
        }
    }
    private static void obtainAccessToken(){
        Map<String, String> data = new HashMap<>();
        data.put("email", "john@example.com");
        data.put("password", "123456");

        APIResponse response  = request.post("auth/login", RequestOptions.create().setData(data));
        assertTrue(response.ok());
        String responseBody = response.text();

        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(responseBody, JsonObject.class);
        if(jsonObject.has("token")){
            accessToken = jsonObject.get("token").getAsString();
        } else {
            System.err.println("Error: 'token' not found in the login response. Authentication failed.");
        }


        System.out.println(accessToken);
    }

    // Example test that would use the accessToken (can be moved to a subclass)
    @Test
    void exampleTestUsingAccessToken() {
        System.out.println("Performing a test that requires the access token: " + accessToken);
        // You would typically use the accessToken in the headers of subsequent API calls
    }

}
