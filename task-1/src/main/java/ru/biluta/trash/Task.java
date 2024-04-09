package ru.biluta.trash;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Task {
    private final double arrivalTime; // Время прибытия
    private final double processingTime; // Время обработки

    public Task(double arrivalTime, double processingTime) {
        this.arrivalTime = arrivalTime;
        this.processingTime = processingTime;
    }
}
