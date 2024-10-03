package com.aqeel.to_do_list;

public class ModelTask {
    private String taskName;

    private boolean isCompleted;

    public ModelTask(String taskName, String taskDate, String taskTime, boolean isCompleted) {
        this.taskName = taskName;

        this.isCompleted = isCompleted;
    }

    public String getTaskName() {
        return taskName;
    }


    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}

