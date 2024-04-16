package ru.biluta;

import lombok.Getter;
import ru.biluta.model.Event;
import ru.biluta.model.Task;
import ru.biluta.service.EventDispatcher;
import ru.biluta.system.Core;
import ru.biluta.system.Server;
import ru.biluta.service.TaskManager;

import java.util.List;

public class Simulation {

    private final String name;
    private final int lengthDashes = 15;
    private final Server server;
    private final TaskManager taskManager;
    private final EventDispatcher eventDispatcher;

    @Getter
    private int totalTasksGenerated;
    @Getter
    private int totalTasksProcessed;
    @Getter
    private int totalTasksDropped;
    private final int maxTasks;

    @Getter
    private final double sigma;
    @Getter
    private final double lambda;
    @Getter
    private final int numberOfCores;

    public Simulation(String name,
                      double sigma,
                      int k,
                      double lambda,
                      int bufferSize,
                      int cores,
                      int maxTasks) {
        this.name = name;
        this.sigma = sigma;
        this.lambda = lambda;
        this.numberOfCores = cores;

        this.server = new Server(cores, bufferSize);
        this.taskManager = new TaskManager(sigma, lambda, k);
        this.eventDispatcher = new EventDispatcher(sigma, lambda, k);

        this.maxTasks = maxTasks;

        this.totalTasksGenerated = 0;
        this.totalTasksProcessed = 0;
        this.totalTasksDropped = 0;
    }

    public void run() {
        scheduleTaskArrival();

        while (!eventDispatcher.isQueueEmpty() && (totalTasksProcessed < maxTasks)) {
            Event currentEvent = eventDispatcher.getFirst();
            server.advanceSystemTime(currentEvent.getTime());

            switch (currentEvent.getType()) {
                case TASK_ARRIVAL:
                    handleTaskArrival(currentEvent);
                    break;
                case TASK_COMPLETION:
                    handleTaskCompletion(currentEvent);
                    break;
            }
        }

        // printStatistics();
    }

    private void scheduleTaskArrival() {
        if (totalTasksGenerated < maxTasks) {
            Task newTask = taskManager.createTask();
            eventDispatcher.createArrivalEvent(newTask, null, server.getSystemTime());
            totalTasksGenerated++;
        }
    }

    private void handleTaskArrival(Event event) {
        Task task = event.getTask();
        if (server.hasIdleCore()) {
            Integer coreIndex = server.processTask(task);
            eventDispatcher.createCompletionEvent(task, coreIndex, server.getSystemTime());
            totalTasksProcessed++;
        } else {
            boolean added = server.addToBuffer(task);
            if (!added) {
                totalTasksDropped++;
            }
        }
        scheduleTaskArrival();
    }

    private void handleTaskCompletion(Event event) {
        server.completeTask(event);
        if (!server.bufferIsEmpty()) {
            Task newTask = server.getNextTask();
            Integer coreIndex = server.processTask(newTask);
            eventDispatcher.createCompletionEvent(newTask, coreIndex, server.getSystemTime());
            totalTasksProcessed++;
        }
    }

    private void printStatistics() {
        double probabilityOfIdle = server.calculateSummaryIdleTime() / server.getSystemTime();
        double probabilityOfRejection = (double) totalTasksDropped / (totalTasksGenerated + totalTasksDropped);
        printDashes();
        printDashesWithName();
        printDashes();
        System.out.println("Sigma: " + sigma + "; Lambda: " + lambda + "; Cores: " + numberOfCores);
        System.out.println("Probability of idle (Server): " + probabilityOfIdle);
        List<Core> cores = server.getCores();
//        for (int i = 0; i < numberOfCores; i++) {
//            double probabilityOfIdleCore = cores.get(i).getIdleTime() / server.getSystemTime();
//            System.out.println("Probability of idle (Core " + (i + 1) + "): " + probabilityOfIdleCore);
//        }
        System.out.println("Probability of rejection: " + probabilityOfRejection);
        System.out.println("Total tasks processed: " + totalTasksProcessed);
        System.out.println("Total tasks dropped: " + totalTasksDropped);
        System.out.println("Total tasks generated: " + totalTasksGenerated);
        printDashes();
        printEnter();
    }

    public List<Double> getIdleTimes() {
        return server.getIdleTimes();
    }

    public double getCalculateIdleTime() {
        return server.calculateSummaryIdleTime();
    }

    public Double getSystemTime() {
        return server.getSystemTime();
    }

    private void printDashesWithName() {
        int lengthTirePart = (lengthDashes - name.length()) / 2;
        StringBuilder builder = new StringBuilder();
        StringBuilder builder2 = new StringBuilder();
        for (int i = 0; i < lengthTirePart; i++) {
            builder.append("-");
            builder2.append("-");
        }
        if (lengthTirePart * 2 + name.length() != lengthDashes) {
            builder2.append("-");
        }
        System.out.println(builder + name.toUpperCase() + builder2);
    }

    private void printDashes() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < lengthDashes; i++) {
            builder.append("-");
        }
        System.out.println(builder);
    }

    private void printEnter() {
        System.out.println();
    }
}
