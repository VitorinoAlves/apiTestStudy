package com.calves.apiTests.tests.users;

import com.calves.apiTests.tests.TestBaseRestAssured;
import com.calves.apiTests.tests.users.Users;
import com.google.gson.Gson;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import com.github.javafaker.Faker;
import org.hamcrest.Matchers;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;


import static io.restassured.RestAssured.given;

public class UsersTestRestAssured extends TestBaseRestAssured {
    private static Users firstUser;
    private static final Faker faker = new Faker();
    private static final Gson gson = new Gson();
    @Test
    public void requestAllUsersTest(){
        Response response = given()
                .header("Authorization", authToken)
                .when()
                .get("/users")
                .then()
                .statusCode(200)
                .extract().response();

        firstUser = response.jsonPath().getList("$", Users.class).get(0);
        System.out.println("First User ID: " + firstUser.getId());
        System.out.println("First User Name: " + firstUser.getName());
        System.out.println("First User Email: " + firstUser.getEmail());
    }

    @Test
    public void requestSpecificUserTest(){
        if(firstUser != null){
            given()
                    .header("Authorization", authToken)
                    .pathParam("userId", firstUser.getId())
                    .when()
                    .get("/users/{userId}")
                    .then()
                    .statusCode(200)
                    .body("id", org.hamcrest.Matchers.equalTo(firstUser.getId()))
                    .body("name", org.hamcrest.Matchers.equalTo(firstUser.getName()))
                    .body("email", org.hamcrest.Matchers.equalTo(firstUser.getEmail()));
        } else {
            System.err.println("");
        }
    }

    @Test
    public void validateUsersSchema(){
        given()
                .header("Authorization", authToken)
                .when()
                .get("/users")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body(matchesJsonSchemaInClasspath("schemas/user-schema.json"));
    }

    @Test
    public void creatingNewUserTest(){

        Users newUser = new Users(faker.name().fullName(), faker.internet().emailAddress(), faker.number().digits(6));

        String newUserJson = gson.toJson(newUser);

        Response response = given()
                .header("Authorization", authToken)
                .contentType(ContentType.JSON)
                .body(newUserJson)
                .when()
                .post("/users")
                .then()
                .statusCode(201)
                .body("name", org.hamcrest.Matchers.equalTo(newUser.getName()))
                .body("email", org.hamcrest.Matchers.equalTo(newUser.getEmail()))
                .extract().response();

        Users createdUser = response.as(Users.class);
        System.out.println(createdUser);
    }

    @Test
    public void updateUserTest(){
        Response response = given()
                .header("Authorization", authToken)
                .when()
                .get("/users")
                .then()
                .statusCode(200)
                .extract().response();
        Users userToUpdate = response.jsonPath().getList("$", Users.class).get(0);

        userToUpdate.setName(faker.name().fullName());
        userToUpdate.setEmail(faker.internet().emailAddress());

        String userUpdated = gson.toJson(userToUpdate);

        given()
                .header("Authorization", authToken)
                .contentType(ContentType.JSON)
                .pathParam("userId", userToUpdate.getId())
                .body(userUpdated)
                .when()
                .put("/users/{userId}")
                .then()
                .statusCode(200)
                .body("name", org.hamcrest.Matchers.equalTo(userToUpdate.getName()))
                .body("email", org.hamcrest.Matchers.equalTo(userToUpdate.getEmail()))
                .log().all();
    }
}
