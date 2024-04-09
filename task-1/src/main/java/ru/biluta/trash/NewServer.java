package ru.biluta.trash;

import lombok.Getter;

public class NewServer {
    private Task currentTask;
    @Getter
    private double currentTime;

    public NewServer() {
        this.currentTask = null;
        this.currentTime = 0.0;
    }

    public boolean isIdle() {
        return currentTask == null || currentTime >= currentTask.getArrivalTime() + currentTask.getProcessingTime();
    }

    public void processTask(Task task, double currentTime) {
        this.currentTask = task;
        this.currentTime = currentTime + task.getProcessingTime();
    }

    public boolean checkAndCompleteTask(double simulationTime) {
        if (currentTask != null && simulationTime >= currentTime) {
            currentTask = null;
            return true;
        }
        return false;
    }
}
