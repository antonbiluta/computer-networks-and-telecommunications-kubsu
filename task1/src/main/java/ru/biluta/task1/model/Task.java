package ru.biluta.task1.model;

import lombok.Getter;

@Getter
public class Task {
    private final double processingTime;

    public Task(double processingTime) {
        this.processingTime = processingTime;
    }
}
