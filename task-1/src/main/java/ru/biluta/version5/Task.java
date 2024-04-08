package ru.biluta.version5;

import lombok.Getter;

@Getter
public class Task {
    private final double arrivalTime;
    private final double processingTime;

    public Task(double arrivalTime, double processingTime) {
        this.arrivalTime = arrivalTime;
        this.processingTime = processingTime;
    }
}
