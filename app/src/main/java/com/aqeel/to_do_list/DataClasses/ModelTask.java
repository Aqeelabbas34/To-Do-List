package com.aqeel.to_do_list.DataClasses;

import java.io.Serializable;

public class ModelTask implements Serializable {

     String taskName;
     String taskID;
     String userID;
     long timeStamp;
     Boolean starFlag;
     String DueDate;
     String time;
     String category;
     String dateCompleted;

    public String getDateCompleted() {
        return dateCompleted;
    }

    public void setDateCompleted(String dateCompleted) {
        this.dateCompleted = dateCompleted;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    String status;


    public ModelTask() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ModelTask(String taskName, String userID, long timeStamp,String status,String category,String DueDate,String time) {
        this.taskName = taskName;
        this.userID = userID;
        this.timeStamp=timeStamp;
        this.status= status;
        this.category=category;
        this.DueDate=DueDate;
        this.time=time;

    }

    public String getDueDate() {
        return DueDate;
    }

    public void setDueDate(String dueDate) {
        DueDate = dueDate;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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

    public String getTaskID() {
        return taskID;
    }

    public void setTaskID(String tasKID) {
        this.taskID = tasKID;
    }

}

