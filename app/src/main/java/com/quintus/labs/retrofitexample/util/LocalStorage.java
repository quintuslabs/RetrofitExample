package com.quintus.labs.retrofitexample.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Created by BAPUNU on 02-04-2017.
 */
public class LocalStorage {
    private static final String IS_USER_LOGIN = "IsUserLoggedIn";
    private static final String KEY_USER = "LoggedInUser";

    private static LocalStorage instance = null;
    SharedPreferences sharedPreferences;
    Editor editor;
    int PRIVATE_MODE = 0;
    Context _context;

    public LocalStorage(Context context) {
        sharedPreferences = context.getSharedPreferences("Preferences", 0);
    }

    public static LocalStorage getInstance(Context context) {
        if (instance == null) {
            synchronized (LocalStorage.class) {
                if (instance == null) {
                    instance = new LocalStorage(context);
                }
            }
        }
        return instance;
    }


    public void logoutUser() {
        editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }

    public boolean checkLogin() {
        // Check login status
        return !this.isUserLoggedIn();
    }


    public boolean isUserLoggedIn() {
        return sharedPreferences.getBoolean(IS_USER_LOGIN, false);
    }


    public String getUserLogin() {
        return sharedPreferences.getString(KEY_USER, "");
    }

    public void setUserLogin(String userLogin) {
        editor = sharedPreferences.edit();
        editor.putBoolean(IS_USER_LOGIN, true);
        editor.putString(KEY_USER, userLogin);
        editor.commit();
    }

}
