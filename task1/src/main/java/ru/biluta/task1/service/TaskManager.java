package ru.biluta.task1.service;

import ru.biluta.task1.model.Task;
import ru.biluta.task1.utils.RandomGenerators;

public class TaskManager {

    private final RandomGenerators generators;

    public TaskManager(double sigma, double lambda, int k) {
        this.generators = new RandomGenerators(sigma, lambda, k);
    }

    public Task createTask() {
        double processingTime = generators.generateRayleigh();
        return new Task(processingTime);
    }

}
