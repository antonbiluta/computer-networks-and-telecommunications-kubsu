package ru.biluta.trash;

import lombok.Getter;

import java.util.LinkedList;
import java.util.Queue;

@Getter
public class Server {
    private Queue<TaskEvent> eventQueue = new LinkedList<>(); // Очередь для хранения задач
    private Queue<Task> taskQueue = new LinkedList<>();
    private double currentTime = 0.0;
    private double lastTaskFinishTime = 0.0;
    private double idleTime = 0.0;
    private int processedTasks = 0;
    private int totalTasks = 0;
    private int deniedTasks = 0;
    private final int bufferSize;
    private boolean isProcessing = false;


    public Server(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public void scheduleEvent(TaskEvent event) {
        eventQueue.add(event);
    }

    public void run() {
        while (!eventQueue.isEmpty()) {
            TaskEvent event = eventQueue.poll();
            currentTime = event.getEventTime();

            switch (event.getType()) {
                case ARRIVAL:
                    handleArrival(event);
                    break;
                case DEPARTURE:
                    handleDeparture(event);
                    break;
            }
        }
    }

    private void handleArrival(TaskEvent event) {
        if (!isProcessing) {
            startProcessing(event.getTask());
        } else if (taskQueue.size() < bufferSize) {
            taskQueue.add(event.getTask());
        } else {
            deniedTasks++;
        }
    }

    private void handleDeparture(TaskEvent event) {
        processedTasks++;
        isProcessing = false;
        if (!taskQueue.isEmpty()) {
            Task nextTask = taskQueue.poll();
            startProcessing(nextTask);
        } else {
            idleTime += currentTime - event.getEventTime();
        }
    }

    private void startProcessing(Task task) {
        isProcessing = true;
        double departureTime = currentTime + task.getProcessingTime();
        scheduleEvent(new TaskEvent(TaskEvent.EventType.DEPARTURE, task, departureTime));
    }

    public double getDenialProbability() {
        return idleTime / currentTime;
    }

    public double getIdleProbability() {
        return (double) deniedTasks / totalTasks;
    }
}
