package ru.biluta.version5;

import lombok.Getter;
import ru.biluta.utils.SimulationClock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

@Getter
public class Server {
    private List<Task> currentTasks;
    private int cores;
    private PriorityQueue<Event> eventQueue;
    private SimulationClock clock;
    private double[] idleStartTime;
    private double idleTime = 0.0;

    public Server(int cores, PriorityQueue<Event> eventQueue, SimulationClock clock) {
        this.cores = cores;
        this.eventQueue = eventQueue;
        this.clock = clock;
        this.currentTasks = new ArrayList<>(cores);
        for (int i = 0; i < cores; i++) {
            currentTasks.add(null);
        }
        idleStartTime = new double[cores];
        Arrays.fill(idleStartTime, -1);
    }

    public void markCoreIdle(int coreIndex) {
        if (idleStartTime[coreIndex] == -1) {
            idleStartTime[coreIndex] = clock.getCurrentTime();
        }
    }

    public void markCoreBusy(int coreIndex) {
        if (idleStartTime[coreIndex] != -1) {
            idleTime += clock.getCurrentTime() - idleStartTime[coreIndex];
            idleStartTime[coreIndex] = -1;
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
