package com.aqeel.to_do_list;

public class ModelTask {

     String taskName;

     String userID;
    public ModelTask() {
    }

    public ModelTask(String taskName, String userID) {
        this.taskName = taskName;
        this.userID = userID;
    }


    public String getTaskName() {
        return taskName;
    }

    public String getUserID() {
        return userID;
    }
    //
}

