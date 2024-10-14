package com.aqeel.to_do_list;

public class UserSession {
    private  static  UserSession instance;
    private String userID;
    private UserSession(){}

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public static  UserSession getInstance(){
        if (instance==null){
            instance= new UserSession();
        }
        return instance;

    }
}
