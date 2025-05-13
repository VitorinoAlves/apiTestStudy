package com.calves.apiTests.tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class TestBaseRestAssured {

    protected static String authToken;
    protected static String baseURI = "http://localhost:8080";

    static {
        RestAssured.baseURI = baseURI;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @BeforeAll
    static void authenticate() {
        Response response = given()
                .contentType(ContentType.JSON)
                .body("{\n" +
                        "  \"email\": \"john@example.com\",\n" +
                        "  \"password\": \"123456\"\n" +
                        "}")
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .body("token", notNullValue())
                .extract().response();

        authToken = "Bearer " +  response.jsonPath().getString("token");

        if (authToken == null) {
            throw new RuntimeException("Falha ao obter o token de autenticação.");
        }

    }
}
