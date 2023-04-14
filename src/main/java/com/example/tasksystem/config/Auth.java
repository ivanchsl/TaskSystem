package com.example.tasksystem.config;

import com.example.tasksystem.models.User;

public class Auth {

    private static boolean auth = false;

    private static User user;

    public static void login(User usr) {
        auth = true;
        user = usr;
    }

    public static void logout() {
        auth = false;
    }

    public static User getAuth() {
        if (auth) {
            return user;
        }
        return null;
    }
}
