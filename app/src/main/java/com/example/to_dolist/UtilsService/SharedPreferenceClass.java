package com.example.to_dolist.UtilsService;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceClass {

    private static final String USER_PREF = "user_todo";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor prefsEditor;


    public SharedPreferenceClass(Context context){

        sharedPreferences = context.getSharedPreferences(USER_PREF, Activity.MODE_PRIVATE);
        this.prefsEditor = sharedPreferences.edit();

    }

    //Getter Setter for Int type value

    public int getValue_Int(String key){
        return sharedPreferences.getInt(key, 0);
    }

    public void setValue_Int(String key, int value){
        prefsEditor.putInt(key, value).commit();
    }


    //Getter Setter for String type value

    public String getValue_String(String key){
        return sharedPreferences.getString(key, "");
    }

    public void setValue_String(String key, String value){
        prefsEditor.putString(key, value).commit();
    }


    //Getter Setter for Boolean type value

    public Boolean getValue_Boolean(String key){
        return sharedPreferences.getBoolean(key, false);
    }

    public void setValue_Boolean(String key, Boolean value){
        prefsEditor.putBoolean(key, value).commit();
    }


    //For clearing all the data from SharedPreferences

    public void clearData(){
        prefsEditor.clear().commit();
    }


}
