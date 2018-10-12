package com.example.thai.dotify;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.*;

/**
 * A class that contains User Utility methods to be used throughout the application
 */
public class UserUtilities {
    private static final String SHARED_PREF_USER_DATA = "UserData";
    private static final String USER_INFO = "userJson";
    private static final String USERNAME = "username";


    // Caches the user's information
    public static void cacheUser(Context context, DotifyUser user){
        // Retrieve the user's shared preferences folder
        SharedPreferences userPreferences = getUserSharedPreferences(context);
        // Grab the editor to change the information currently stored in the User's shared preferences
        // folder
        SharedPreferences.Editor editor = userPreferences.edit();
        // Chache the User's username
        editor.putString(USERNAME, user.getUsername());
        // Create a Gson object for JSON utilities
        Gson gson = new Gson();
        // Cache the User object itself
        String userJson = gson.toJson(user);
        // Store the user json object
        editor.putString(USER_INFO, userJson);
        // Apply the changes
        editor.apply();
    }

    /**
     * Check whether there is a user who is currently cached, if there is, return their username,
     * otherwise, return null
     * @return The current logged in User's username, and null otherwise
     */
    public static String isLoggedIn(Context context){
        SharedPreferences userPreferences = getUserSharedPreferences(context);
        return userPreferences.getString(USERNAME, null);
    }

    /**
     * Retrieves the user's information that is cached, if there isn't anything cached, it means
     * that the user is not logged in
     *
     * @return A DotifyUser object if the user's information is currently cached
     */
    public static DotifyUser getCachedUserInfo(Context context){
        // Open the shared preferences folder containing the user's information
        SharedPreferences userPreferences = getUserSharedPreferences(context);
        // The user's information in JSON format
        String userJson = userPreferences.getString(USER_INFO, null);

        if (userJson != null) {
            // To convert Json strings to their object equivalent
            Gson gson = new Gson();
            // Convert the Json file containing the user information into a DotifyUser object
            return gson.fromJson(userJson, DotifyUser.class);
        }
        // If a cached user object does not exist, return null
        return null;
    }

    /**
     * Retrieves the SharedPreferences object that corresponds to the storing the User information
     * @param context
     * @return
     */
    private static SharedPreferences getUserSharedPreferences(Context context) {
        return context.getSharedPreferences(SHARED_PREF_USER_DATA, context.MODE_PRIVATE);
    }
}
