package com.bookstore.authenticationapp;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class UserManager {

    private static final String PREF_NAME = "UserDatabase";
    private static final String KEY_USERNAMES = "usernames";
    private SharedPreferences sharedPreferences;

    public UserManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // Inner class to represent a User
    public static class User {
        public String username;
        public String password;
        public String fullName;
        public String gender;
        public String yearOfBirth;

        public User(String username, String password, String fullName, String gender, String yearOfBirth) {
            this.username = username;
            this.password = password;
            this.fullName = fullName;
            this.gender = gender;
            this.yearOfBirth = yearOfBirth;
        }
    }

    // Register a new user
    public boolean registerUser(String username, String password, String fullName, String gender, String yearOfBirth) {
        if (userExists(username)) {
            return false;
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Save user data with username as prefix
        editor.putString(username + "_password", password);
        editor.putString(username + "_fullName", fullName);
        editor.putString(username + "_gender", gender);
        editor.putString(username + "_yearOfBirth", yearOfBirth);

        // Add username to the list of registered usernames
        Set<String> usernames = sharedPreferences.getStringSet(KEY_USERNAMES, new HashSet<>());
        Set<String> newUsernames = new HashSet<>(usernames);
        newUsernames.add(username);
        editor.putStringSet(KEY_USERNAMES, newUsernames);

        return editor.commit();
    }

    // Check if a user exists
    public boolean userExists(String username) {
        Set<String> usernames = sharedPreferences.getStringSet(KEY_USERNAMES, new HashSet<>());
        return usernames.contains(username);
    }

    // Verify user credentials
    public boolean verifyUser(String username, String password) {
        if (!userExists(username)) {
            return false;
        }

        String storedPassword = sharedPreferences.getString(username + "_password", null);
        return storedPassword != null && storedPassword.equals(password);
    }

    // Get user details
    public User getUser(String username) {
        if (!userExists(username)) {
            return null;
        }

        String password = sharedPreferences.getString(username + "_password", "");
        String fullName = sharedPreferences.getString(username + "_fullName", "");
        String gender = sharedPreferences.getString(username + "_gender", "");
        String yearOfBirth = sharedPreferences.getString(username + "_yearOfBirth", "");

        return new User(username, password, fullName, gender, yearOfBirth);
    }

    // Delete a user
    public boolean deleteUser(String username) {
        if (!userExists(username)) {
            return false;
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Remove user data
        editor.remove(username + "_password");
        editor.remove(username + "_fullName");
        editor.remove(username + "_gender");
        editor.remove(username + "_yearOfBirth");

        // Remove username from the list
        Set<String> usernames = sharedPreferences.getStringSet(KEY_USERNAMES, new HashSet<>());
        Set<String> newUsernames = new HashSet<>(usernames);
        newUsernames.remove(username);
        editor.putStringSet(KEY_USERNAMES, newUsernames);

        return editor.commit();
    }

    // Get all registered usernames
    public Set<String> getAllUsernames() {
        return sharedPreferences.getStringSet(KEY_USERNAMES, new HashSet<>());
    }
}