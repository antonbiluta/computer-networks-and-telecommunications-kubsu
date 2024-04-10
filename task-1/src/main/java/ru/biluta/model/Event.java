package ru.biluta.model;

import lombok.Getter;

@Getter
public class Event implements Comparable<Event> {
    public enum EventType {
        TASK_ARRIVAL,
        TASK_COMPLETION
    }

    private final EventType type;
    private final double time;
    private final Task task;
    private final Integer coreIndex;

    public Event(EventType eventType,
                 double eventTime,
                 Task task,
                 Integer coreIndex) {
        this.type = eventType;
        this.time = eventTime;
        this.task = task;
        this.coreIndex = coreIndex;
    }

    @Override
    public int compareTo(Event other) {
        return Double.compare(this.time, other.time);
    }
}
