package ru.biluta.service;

import ru.biluta.model.Event;
import ru.biluta.model.Task;
import ru.biluta.utils.RandomGenerators;

import java.util.PriorityQueue;

public class EventDispatcher {

    private PriorityQueue<Event> eventQueue;
    private RandomGenerators generators;

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
        Event arrivalEvent = new Event(Event.EventType.TASK_ARRIVAL, nextArrivalTime, newTask, coreIndex);
        eventQueue.add(arrivalEvent);
    }

    public void createCompletionEvent(Task task, Integer coreIndex, double currentTime) {
        double completionTime = currentTime + task.getProcessingTime();
        Event newEvent = new Event(Event.EventType.TASK_COMPLETION, completionTime, task, coreIndex);
        eventQueue.add(newEvent);
    }
}
