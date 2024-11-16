package com.aqeel.to_do_list.DataClasses;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

public class SharedPref {
private final SharedPreferences sharedPreferences;
private final SharedPreferences.Editor editor;
private final Gson gson;

    public SharedPref(Context context) {
        sharedPreferences= context.getSharedPreferences("MyAppPref",Context.MODE_PRIVATE);
        editor= sharedPreferences.edit();
        gson= new Gson();
    }
public void saveData(ModelUser modelUser){
String userJson=gson.toJson(modelUser);
editor.putString("user",userJson);
editor.apply();
}
public  ModelUser getData(){
String userJson=sharedPreferences.getString("user","");
    if(userJson!=null)
    {
        return gson.fromJson(userJson,ModelUser.class);
    }
    return null;
}
public void saveTaskCount(ModelTask modelTask){
        String taskJson=gson.toJson(modelTask);
        editor.putString("user",taskJson);
        editor.apply();
}
public void setPending(int count ){
        editor.putInt("Pending",count);
        editor.apply();
}
    public void setComplete(int count ){
        editor.putInt("complete",count);
        editor.apply();
    }
    public void setLoggedIn(Boolean status){
        editor.putBoolean("Login_Status",status);
        editor.apply();
    }
    public boolean isLoggedIn(){
        return sharedPreferences.getBoolean("Login_Status",false);
    }
    public int getPending(){
        return sharedPreferences.getInt("Pending",0);
    }
    public int getComplete(){
        return sharedPreferences.getInt("complete",0);
    }




}
