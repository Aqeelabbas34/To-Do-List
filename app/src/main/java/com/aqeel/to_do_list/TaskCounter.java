package com.aqeel.to_do_list;

import java.util.Arrays;

public class TaskCounter {
    private static final TaskCounter instance = new TaskCounter();
    private int pendingTaskCount = 0;
    private int completedTaskCount = 0;
    private final int[] dailyCompletedTasks = new int[7];
    private TaskCounter() {}

    public static TaskCounter getInstance() {
        return instance;
    }

    public int getPendingTaskCount() {
        return pendingTaskCount;
    }

    public int getCompletedTaskCount() {
        return completedTaskCount;
    }

    public void addTaskToPending() {
        pendingTaskCount++;
    }

    public void completeTask() {
        if (pendingTaskCount > 0) {
            pendingTaskCount--;
            completedTaskCount++;
        }
    }

    public int getCompletedTaskCountForDay(int dayIndex) {
        if (dayIndex >= 0 && dayIndex < 7) {
            return dailyCompletedTasks[dayIndex];
        }
        return 0;
    }

    public void addCompletedTaskForDay(int dayIndex) {
        if (dayIndex >= 0 && dayIndex < 7) {
            dailyCompletedTasks[dayIndex]++;
        }
    }

    public void resetDailyCounts() {
        Arrays.fill(dailyCompletedTasks, 0);
    }
}
