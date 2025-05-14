package com.calves.apiTests.tests.users;

import com.calves.apiTests.tests.TestBase;
import com.github.javafaker.Faker;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.JsonArray;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.RequestOptions;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

public class UsersTest extends TestBase {
    private Gson gson = new Gson();
    private Faker faker = new Faker();
    @Test
    public void getAllUsersTest() {
        Users firstUser;
        APIResponse response = request.get("/users");
        assertEquals(response.status(), 200);

        String responseBody = response.text();
        Type userListType = new TypeToken<List<Users>>(){}.getType();
        List<Users> usersList = gson.fromJson(responseBody, userListType);

        firstUser = usersList.get(0);
        APIResponse responseUnicUser = request.get("/users/" + firstUser.getId());
        assertEquals(responseUnicUser.status(), 200);


        Users user = gson.fromJson(responseUnicUser.text(), Users.class);
        assertEquals(usersList.get(0).getName(), firstUser.getName());
        assertEquals(usersList.get(0).getEmail(), firstUser.getEmail());
        assertEquals(usersList.get(0).getId(), firstUser.getId());
    }

    @Test
    public void createNewUserTest(){
        Users newUser = new Users(faker.name().fullName(), faker.internet().emailAddress(), faker.number().digits(6));
        String newUserJson = gson.toJson(newUser);

        APIResponse response = request.post("/users", RequestOptions.create().setData(newUserJson));
        assertEquals(response.status(), 201);

        Users createdUser = gson.fromJson(response.text(), Users.class);

        assertEquals(createdUser.getName(), newUser.getName());
        assertEquals(createdUser.getEmail(), newUser.getEmail());
    }

    @Test
    public void editUserTest(){
        APIResponse responseUserList = request.get("/users", RequestOptions.create().setQueryParam("sort", "name"));

        Type userListType = new TypeToken<List<Users>>(){}.getType();
        List<Users> usersList = gson.fromJson(responseUserList.text(), userListType);
        Users firstUser = usersList.get(0);

        firstUser.setName(faker.name().fullName());
        firstUser.setEmail(faker.internet().emailAddress());

        String updatedUserJson = gson.toJson(firstUser);

        APIResponse responseUserUpdated = request.put("/users/" + firstUser.getId(), RequestOptions.create().setData(updatedUserJson));
        assertEquals(responseUserUpdated.status(), 200);
        Users updatedUser = gson.fromJson(responseUserUpdated.text(), Users.class);

        assertEquals(updatedUser.getName(), firstUser.getName());
        assertEquals(updatedUser.getEmail(), firstUser.getEmail());
    }

    @Test
    public void getUserNotFoundTest(){
        APIResponse responseAllUsers = request.get("/users");
        assertEquals(responseAllUsers.status(), 200);
        Type userListType = new TypeToken<List<Users>>(){}.getType();
        List<Users> existingUsers = gson.fromJson(responseAllUsers.text(), userListType);
        List<Integer> existingIds = existingUsers.stream().map(Users::getId).collect(Collectors.toList());

        int nonExistingUserId;
        int attempts = 0;
        final int MAX_ATTEMPTS = 100;

        do {
            nonExistingUserId = faker.number().numberBetween(10, 1000);
            attempts++;
            if(attempts>MAX_ATTEMPTS) {
                fail("Não foi possível gerar um ID de usuário não existente após várias tentativas.");
                return;
            }
        } while (existingIds.contains(nonExistingUserId));

        APIResponse responseNotFound = request.get("/users/" + nonExistingUserId);
        assertEquals(responseNotFound.status(), 404);
    }

}
