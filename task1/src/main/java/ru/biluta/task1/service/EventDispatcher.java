package ru.biluta.task1.service;

import ru.biluta.task1.model.Event;
import ru.biluta.task1.model.EventType;
import ru.biluta.task1.model.Task;
import ru.biluta.task1.utils.RandomGenerators;

import java.util.PriorityQueue;

public class EventDispatcher {

    private final PriorityQueue<Event> eventQueue;
    private final RandomGenerators generators;

    public EventDispatcher(double sigma, double lambda, int k) {
        this.eventQueue = new PriorityQueue<>();
        this.generators = new RandomGenerators(sigma, lambda, k);
    }

    public boolean isQueueEmpty() {
        return eventQueue.isEmpty();
    }

    public Event getFirst() {
        return eventQueue.poll();
    }

    public void createArrivalEvent(Task newTask, Integer coreIndex, double currentTime) {
        double nextArrivalTime = currentTime + generators.generateErlang();
        Event arrivalEvent = new Event(EventType.TASK_ARRIVAL, nextArrivalTime, newTask, coreIndex);
        eventQueue.add(arrivalEvent);
    }

    public void createCompletionEvent(Task task, Integer coreIndex, double currentTime) {
        double completionTime = currentTime + task.getProcessingTime();
        Event newEvent = new Event(EventType.TASK_COMPLETION, completionTime, task, coreIndex);
        eventQueue.add(newEvent);
    }
}
