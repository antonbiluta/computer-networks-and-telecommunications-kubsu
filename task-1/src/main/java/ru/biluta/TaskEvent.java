package ru.biluta;

import lombok.Getter;

@Getter
public class TaskEvent {
    enum EventType { ARRIVAL, DEPARTURE }
    private EventType type;
    private Task task;
    private double eventTime;

    TaskEvent(EventType type, Task task, double eventTime) {
        this.type = type;
        this.task = task;
        this.eventTime = eventTime;
    }
}
