package ru.biluta.version6;

import lombok.Getter;

@Getter
public class Event implements Comparable<Event> {
    public enum EventType {
        TASK_ARRIVAL,
        TASK_COMPLETION
    }

    private final EventType eventType;
    private final double eventTime;
    private final Task task;

    public Event(EventType eventType, double eventTime, Task task) {
        this.eventType = eventType;
        this.eventTime = eventTime;
        this.task = task;
    }

    @Override
    public int compareTo(Event other) {
        return Double.compare(this.eventTime, other.eventTime);
    }
}
