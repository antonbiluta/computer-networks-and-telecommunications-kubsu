package ru.biluta.version5;

import lombok.Getter;

@Getter
public class Event implements Comparable<Event> {
    enum EventType {
        TASK_ARRIVAL,
        TASK_COMPLETION
    }

    private final EventType type;
    private final double time;
    private final Task task;
    private final Integer coreIndex;

    public Event(EventType type, double time, Task task, Integer coreIndex ) {
        this.type = type;
        this.time = time;
        this.task = task;
        this.coreIndex = coreIndex;
    }

    @Override
    public int compareTo(Event other) {
        return Double.compare(this.time, other.time);
    }
}
