package ru.biluta;

import ru.biluta.model.Event;
import ru.biluta.model.Task;
import ru.biluta.service.EventDispatcher;
import ru.biluta.system.Buffer;
import ru.biluta.system.Core;
import ru.biluta.system.Server;
import ru.biluta.utils.RandomGenerators;
import ru.biluta.service.TaskManager;

import java.util.List;
import java.util.PriorityQueue;

public class Simulation {

    private final String name;
    private final int lengthDashes = 15;
    private final Server server;
    private final TaskManager taskManager;
    private final EventDispatcher eventDispatcher;
    private int totalTasksGenerated;
    private int totalTasksProcessed;
    private int totalTasksDropped;
    private final int maxTasks;

    private final double sigma;
    private final double lambda;
    private final int numberOfCores;

    public Simulation(String name, double sigma, int k, double lambda, int bufferSize, int cores, int maxTasks) {
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

        printStatistics();
    }

    private void scheduleTaskArrival() {
        if (totalTasksGenerated < maxTasks) {
            Task newTask = taskManager.generateTask();
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
        double probabilityOfIdle = server.getIdleTime() / server.getSystemTime();
        double probabilityOfRejection = (double) totalTasksDropped / (totalTasksGenerated + totalTasksDropped);
        printDashes();
        printDashesWithName();
        printDashes();
        System.out.println("Sigma: " + sigma + "; Lambda: " + lambda + "; Cores: " + numberOfCores);
        System.out.println("Probability of idle (Server): " + probabilityOfIdle);
        List<Core> cores = server.getCores();
        for (int i = 0; i < numberOfCores; i++) {
            double probabilityOfIdleCore = cores.get(i).getIdleTime() / server.getSystemTime();
            System.out.println("Probability of idle (Core " + (i + 1) + "): " + probabilityOfIdleCore);
        }
        System.out.println("Probability of rejection: " + probabilityOfRejection);
        System.out.println("Total tasks processed: " + totalTasksProcessed);
        System.out.println("Total tasks dropped: " + totalTasksDropped);
        System.out.println("Total tasks generated: " + totalTasksGenerated);
        printDashes();
        printEnter();
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

    public static void main(String[] args) {
        Simulation simulation1 = new Simulation("test1", 1, 3, 2, 3, 1, 10000);
        simulation1.run();

        Simulation simulation2 = new Simulation("test2", 2, 3, 1, 3, 1, 10000);
        simulation2.run();

        Simulation simulation3 = new Simulation("test3", 6, 3, 2, 3, 1, 10000);
        simulation3.run();

        Simulation simulation4 = new Simulation("test4", 1, 3, 6, 3, 4, 10000);
        simulation4.run();

        Simulation simulation5 = new Simulation("test5", 2, 3, 1, 3, 3, 1000000);
        simulation5.run();
    }
}
