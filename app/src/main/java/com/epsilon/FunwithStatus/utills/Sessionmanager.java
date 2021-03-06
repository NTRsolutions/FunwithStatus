package com.epsilon.FunwithStatus.utills;

import android.content.Context;
import android.content.SharedPreferences;

import com.epsilon.FunwithStatus.jsonpojo.login.LoginDatum;
import com.epsilon.FunwithStatus.jsonpojo.registration.RegistrationDatum;


/**
 * Created by DeLL on 18-01-2018.
 */

public class Sessionmanager {
    static SharedPreferences sharedPreferences;
    Context context;
    public static final String mypreference = "mypref";
    public static final String Id = "idKey";
    public static final String Email = "emailKey";
    public static final String Name = "nameKey";
    public static final String Token = "TokenKey";

    public Sessionmanager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(mypreference, Context.MODE_PRIVATE);
    }


    public String getValue(String KEY_ID) {
        return sharedPreferences.getString(KEY_ID, "");
    }


    public void putSessionValue(String KEY_NAME, String KEY_VALUE) {
        sharedPreferences.edit().putString(KEY_NAME, KEY_VALUE).apply();
    }

    public static void setPreferenceBoolean(Context activity, String key, boolean value) {
        sharedPreferences = activity.getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static boolean getPreferenceBoolean(Context activity, String key, boolean Default) {
        sharedPreferences = activity.getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, Default);
    }

    public void logoutUser() {
        sharedPreferences.edit().clear().apply();
    }

    public void createSession_userLogin(LoginDatum userLogin) {
        sharedPreferences.edit().putString(Token, userLogin.getToken()).apply();
        sharedPreferences.edit().putString(Id, userLogin.getUser().getId().toString()).apply();
        sharedPreferences.edit().putString(Name, userLogin.getUser().getName()).apply();
        sharedPreferences.edit().putString(Email, userLogin.getUser().getEmail()).apply();
    }


    public void createSession_userRegister(RegistrationDatum userregister) {
        sharedPreferences.edit().putString(Token, userregister.getToken()).apply();
        sharedPreferences.edit().putString(Id, userregister.getUser().getId().toString()).apply();
        sharedPreferences.edit().putString(Name, userregister.getUser().getName()).apply();
        sharedPreferences.edit().putString(Email, userregister.getUser().getEmail()).apply();
    }

    public static class TokenSaver {
        private final static String SHARED_PREF_NAME = "net.rouk1.SHARED_PREF_NAME";
        private final static String TOKEN_KEY = "net.rouk1.TOKEN_KEY";

        public static String getToken(Context c) {
            SharedPreferences prefs = c.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            return prefs.getString(TOKEN_KEY, "");
        }

        public static void setToken(Context c, String token) {
            SharedPreferences prefs = c.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(TOKEN_KEY, token);
            editor.apply();
        }

    }
}