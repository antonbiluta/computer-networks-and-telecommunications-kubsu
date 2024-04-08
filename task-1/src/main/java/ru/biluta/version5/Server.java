package ru.biluta.version5;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class Server {
    private List<Task> currentTasks;
    private int cores;
    private PriorityQueue<Event> eventQueue;
    private SimulationClock clock;

    public Server(int cores, PriorityQueue<Event> eventQueue, SimulationClock clock) {
        this.cores = cores;
        this.eventQueue = eventQueue;
        this.clock = clock;
        this.currentTasks = new ArrayList<>(cores);
        for (int i = 0; i < cores; i++) {
            currentTasks.add(null);
        }
    }

    public boolean hasIdleCore() {
        return currentTasks.contains(null);
    }

    public Integer processTask(Task task) {
        for (int i = 0; i < cores; i++) {
            if (currentTasks.get(i) == null) {
                currentTasks.set(i, task);
                double completionTime = clock.getCurrentTime() + task.getProcessingTime();
                eventQueue.add(new Event(Event.EventType.TASK_COMPLETION, completionTime, task, i));
                return i;
            }
        }
        return null;
    }

    public void completeTask(int coreIndex) {
        currentTasks.set(coreIndex, null);
    }
}
