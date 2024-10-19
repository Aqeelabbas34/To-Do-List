package com.aqeel.to_do_list;

public class ModelTask {

     String taskName;
     String tasKID;
     String userID;
     boolean isComplete;
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

    public String getTasKID() {
        return tasKID;
    }

    public void setTasKID(String tasKID) {
        this.tasKID = tasKID;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }
}

