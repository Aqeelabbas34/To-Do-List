package com.aqeel.to_do_list;

public class ModelTask {

     String taskName;
     String tasKID;
     String userID;
     boolean isComplete;
     long timeStamp;
    public ModelTask() {
    }



    public ModelTask(String taskName, String userID,long timeStamp) {
        this.taskName = taskName;
        this.userID = userID;
        this.timeStamp=timeStamp;
    }


    public String getTaskName() {
        return taskName;
    }

    public String getUserID() {
        return userID;
    }
    //

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

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

