package com.calves.apiTests.tests.users;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Users {
    //https://github.com/mosh-hamedani/spring-api-finished/tree/main
    private int id;
    private String name;
    private String email;
    private String password;

    public Users() {
        // Jackson needs this to create an instance
    }

    public Users(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public Users(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    @Override
    public String toString() {
        return "Users{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
