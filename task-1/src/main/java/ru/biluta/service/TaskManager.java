package ru.biluta.service;

import ru.biluta.utils.RandomGenerators;
import ru.biluta.model.Task;

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
