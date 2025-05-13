package com.calves.apiTests.tests.users;

import com.calves.apiTests.tests.TestBase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.JsonArray;
import com.microsoft.playwright.APIResponse;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.lang.reflect.Type;
import java.util.List;

public class UsersTest extends TestBase {
    private Gson gson = new Gson();
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
}
