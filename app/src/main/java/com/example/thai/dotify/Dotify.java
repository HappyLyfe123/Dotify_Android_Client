package com.example.thai.dotify;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.IOException;
import java.io.Serializable;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Dotify{

    //Dotify Constants
    public final static String APP_KEY_HEADER = "appKey";
    public final static String DOTIFY_USERNAME = "username";
    public final static String DOTIFY_STORAGE = "dotify_storage";

    //Private Constants
    private final static String TAG = Dotify.class.getSimpleName();

    //REST Api items
    private Retrofit.Builder retrofitBuilder;
    private OkHttpClient.Builder okHttpClientBuilder;

    /**
     * Dotify object constructor
     */

    public Dotify(String baseUrl) {
        //Instantiate a Retrofit.Builder object
        retrofitBuilder = new Retrofit.Builder();
        //Instantiate an OkHttpClient.Builder object
        okHttpClientBuilder = new OkHttpClient.Builder();
        //Create a standard retrofit object
        retrofitBuilder.baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create());
    }

    /**
     * Adds a logging interceptor for Retrofit only if its development mode, will crash otherwise
     *
     * @param loggingLevel This can be set to the following values:
     *                  Level.NONE : Nothing will be logged
     *                  Level.BASIC : Logs requests and response lines
     *                  Level.BODY : Will request everything ,however, be careful because it might print huge image arrays or videos
     *                  Level.HEADERS : Logs request and response lines, and along with the respective headers
     */
    public void addLoggingInterceptor(HttpLoggingInterceptor.Level loggingLevel) {
        if(BuildConfig.DEBUG) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(loggingLevel);
            okHttpClientBuilder.addInterceptor(loggingInterceptor);
        } else {
            throw new RuntimeException("Cannot add interceptor outside of development build");
        }
    }

    /**
     * Adds the ability to intercept a request before sending it to add any runtime information
     *
     * @param interceptor The interceptor interface
     */
    public void addRequestInterceptor(Interceptor interceptor) {
        okHttpClientBuilder.addInterceptor(interceptor);
    }

    /**
     * Returns a Retrofit object based on the attachments previously made
     *
     * @return a Retrofit object
     */
    public DotifyHttpInterface getHttpInterface() {
        //Add the current OkHttpClient that the user has optionally built
        retrofitBuilder.client(okHttpClientBuilder.build());
        //Return the client to be used for requests and responses
        Retrofit retrofit = retrofitBuilder.build();
        DotifyHttpInterface dotifyHttpInterface = retrofit.create(DotifyHttpInterface.class);
        return dotifyHttpInterface;
    }

    /**
     * Store the user's username inside of the phone for accessibility
     *
     * @param context The activity where this method is being called from
     * @param username The User's username
     */

    public static void storeDotifyUsername(Context context, String username) {
        //Create or open folder holding that hold's the user's Astral information
        SharedPreferences sharedPreferences = getDotifySharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //Add the User's username to the folder
        editor.putString(DOTIFY_USERNAME, username);
        editor.apply();
    }

    /**
     * Retrieve's the user's Dotify username from storage
     * @param context
     */
    public static String getCachedDotifyUsername(final Context context) {
        //Open the folder containing Astral's information
        SharedPreferences sharedPreferences = getDotifySharedPreferences(context);
        return sharedPreferences.getString(Dotify.DOTIFY_USERNAME, null);
    }

//    public static String getCachedAstralTokens(final Context context) {
//        //Open the folder containing Astral's information
//        SharedPreferences sharedPreferences = getAstralSharedPreferences(context);
//        return sharedPreferences.getString(Astral.ASTRAL_TOKEN, null);
//    }

//    /**
//     * Stores the User's tokens to perform any other transactions later
//     * @param userToken The token item received from an Astral Request
//     */
//    public static void storeAstralUserToken(Context context, String userToken) {
//        SharedPreferences sharedPreferences = getAstralSharedPreferences(context);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString(Astral.ASTRAL_TOKEN, userToken);
//        editor.apply();
//    }

//    /**\
//     * Removes the astral token from cache
//     *
//     * @param context The activity in which this method was called
//     */
//    public static void removeAstralToken(final Context context) {
//        SharedPreferences sharedPreferences = getAstralSharedPreferences(context);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.remove(Astral.ASTRAL_TOKEN);
//        editor.apply();
//    }

    /**
     * Removes the User's username that is cached
     *
     * @param context The application's current context
     */
    public static void removeDotifyUsername(final Context context) {
        SharedPreferences sharedPreferences = getDotifySharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(DOTIFY_USERNAME);
        //editor.remove(ASTRAL_TOKEN);
        editor.apply();
    }

    /**
     * Retrieves the Astral shared preferences
     * @param context The current context of the application
     *
     * @return Astral's shared preferences folder
     */
    private static SharedPreferences getDotifySharedPreferences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(DOTIFY_STORAGE, Context.MODE_PRIVATE);
        return sharedPreferences;
    }
}