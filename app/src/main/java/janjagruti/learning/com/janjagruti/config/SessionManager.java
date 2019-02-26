package janjagruti.learning.com.janjagruti.config;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;

import java.util.Date;

import janjagruti.learning.com.janjagruti.LoginActivity;
import janjagruti.learning.com.janjagruti.entity.User;
import janjagruti.learning.com.janjagruti.entity.support.ApiSession;

public class SessionManager {

    //LogCat tag
    private static String TAG = SessionManager.class.getSimpleName();

    private Context _context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "com.learning.janjagruti";

    public SessionManager(Context context){
        this._context = context;
        sharedPreferences = _context.getApplicationContext().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public SharedPreferences.Editor getEditor() {
        return editor;
    }

    public static final String KEY_BASE_URL = "urlBase";

    public void setUrlBase(String newUrlBase){
        editor.putString(KEY_BASE_URL, newUrlBase).commit();
    }

    public String getUrlBase(){
        return sharedPreferences.getString(KEY_BASE_URL, ApiConfig.BASE_URL);
    }

    public static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    public static final String KEY_API_TOKEN = "apiToken";
    public static final String KEY_API_TOKEN_ISSUEDAT = "apiTokenIssuedAt";
    public static final String KEY_API_TOKEN_EXPIRES = "apiTokenExpires";

    public static final String KEY_USERNAME = "username";
    public static final String KEY_EMAIL = "email";

    public static final String KEY_USER_PROFILE = "userProfile";

    public void loggedIn(String token, Long issuedAt, Long expire, @Nullable User userProfile){

        editor
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putString(KEY_API_TOKEN, token)
            .putLong(KEY_API_TOKEN_ISSUEDAT, issuedAt)
            .putLong(KEY_API_TOKEN_EXPIRES, expire);

        if (userProfile != null){
            editor.putString(KEY_USER_PROFILE, new Gson().toJson(userProfile));
        }
        editor.commit();
    }

    public void loggedOut(){
        editor
            .putBoolean(KEY_IS_LOGGED_IN, false)
            .commit();
    }

    public boolean isTokenExpired(){

        long expire = sharedPreferences.getLong(KEY_API_TOKEN_EXPIRES, 0);
        if (expire < (new Date().getTime() / 1000))
            return true;
        return false;
    }

    public void handleTokenExpiry(Activity activity){
        if (isTokenExpired()){
            Intent intent = new Intent(activity, LoginActivity.class);
            activity.startActivity(intent);
            activity.finish();
        }
    }

    public User getUserProfile(){
        Log.d("LoginAttempt", sharedPreferences.getString(KEY_USER_PROFILE, null));
        return new Gson().fromJson(sharedPreferences.getString(KEY_USER_PROFILE, null), User.class);
    }

    public boolean isLoggedIn() {
        //sharedPreferences.edit().clear().commit();
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public boolean isPremiumUser(){

        User userProfile = getUserProfile();
        Date now = new Date();

        Log.d("LoginAttempt", "now = " + now.toString());

        if (userProfile.getActivePackage() != null && userProfile.getActivePackage().getUserPackage() != null
                && now.after(userProfile.getActivePackage().getUserPackage().getValidityStart())
                && now.before(userProfile.getActivePackage().getUserPackage().getValidityEnd())) {
            Log.d("LoginAttempt", "ValidityStart = " + userProfile.getActivePackage().getUserPackage().getValidityStart() + ", ValidityEnd = " + userProfile.getActivePackage().getUserPackage().getValidityEnd());
            return true;
        }
        return false;
    }
}
