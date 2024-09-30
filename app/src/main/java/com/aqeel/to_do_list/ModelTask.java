package com.aqeel.to_do_list;

public class ModelTask {
    private String taskName;
    private String taskDate;
    private String taskTime;
    private boolean isCompleted;

    public ModelTask(String taskName, String taskDate, String taskTime, boolean isCompleted) {
        this.taskName = taskName;
        this.taskDate = taskDate;
        this.taskTime = taskTime;
        this.isCompleted = isCompleted;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getTaskDate() {
        return taskDate;
    }

    public String getTaskTime() {
        return taskTime;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}

