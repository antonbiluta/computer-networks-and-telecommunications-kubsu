package ru.biluta.version5;

import ru.biluta.version5.utils.RandomGenerators;

import java.util.PriorityQueue;

public class Simulation {
    private PriorityQueue<Event> eventQueue;
    private SimulationClock clock;
    private Server server;
    private Buffer buffer;
    private RandomGenerators randomGenerators;
    private int totalTasksGenerated;
    private int totalTasksProcessed;
    private int totalTasksDropped;

    public Simulation(double sigma, int k, double lambda, int bufferSize, int cores, int maxTasks) {
        this.eventQueue = new PriorityQueue<>();
        this.clock = new SimulationClock();
        this.server = new Server(cores, eventQueue, clock);
        this.buffer = new Buffer(bufferSize);
        this.randomGenerators = new RandomGenerators(sigma, lambda, k);
        this.totalTasksGenerated = maxTasks;
        this.totalTasksProcessed = 0;
        this.totalTasksDropped = 0;

        scheduleNextTaskArrival();
    }

    private void scheduleNextTaskArrival() {
        if ((totalTasksProcessed + totalTasksDropped) < totalTasksGenerated) {
            double nextArrivalTime = clock.getCurrentTime() + randomGenerators.generateErlang();
            double processingTime = randomGenerators.generateRayleigh();
            Task task = new Task(nextArrivalTime, processingTime);
            Event event = new Event(Event.EventType.TASK_ARRIVAL, nextArrivalTime, task, null);
            eventQueue.add(event);
        }
    }

    public void run() {
        while (!eventQueue.isEmpty() || (totalTasksProcessed + totalTasksDropped) < totalTasksGenerated) {
            Event event = eventQueue.poll();
            if (event != null) {
                clock.advanceTime(event.getTime());
                switch (event.getType()) {
                    case TASK_ARRIVAL:
                        handleTaskArrival(event);
                        break;
                    case TASK_COMPLETION:
                        handleTaskCompletion(event.getCoreIndex());
                        break;
                }
            }

            if (server.hasIdleCore() && !buffer.isEmpty()) {
                dispatchTasks();
            }

            if (totalTasksGenerated <= 0 && buffer.isEmpty() && eventQueue.isEmpty()) {
                break;
            }
        }
        printStatistics();
    }

    private void handleTaskArrival(Event event) {
        Task task = event.getTask();
        if (!buffer.addTask(task)) {
            totalTasksDropped++;
        } else {
            dispatchTasks();
        }
        if (totalTasksGenerated > 0) {
            scheduleNextTaskArrival();
        }
    }

    private void handleTaskCompletion(Integer coreIndex) {
        server.completeTask(coreIndex);
        if (!buffer.isEmpty()) {
            dispatchTasks();
        } else {
            server.markCoreIdle(coreIndex);
        }
        totalTasksProcessed++;
    }

    private void dispatchTasks() {
        while (!buffer.isEmpty() && server.hasIdleCore()) {
            Task nextTask = buffer.getNextTask();
            Integer coreIndex = server.processTask(nextTask);
            server.markCoreBusy(coreIndex);

            double completionTime = clock.getCurrentTime() + nextTask.getProcessingTime();
            Event event = new Event(Event.EventType.TASK_COMPLETION, completionTime, nextTask, coreIndex);
            eventQueue.add(event);
        }
    }

    private void printStatistics() {
        double probabilityOfIdle = server.getIdleTime() / clock.getCurrentTime();
        double probabilityOfRejection = (double) totalTasksDropped / (totalTasksGenerated + totalTasksDropped);
        System.out.println("Probability of idle: " + probabilityOfIdle);
        System.out.println("Probability of rejection: " + probabilityOfRejection);
        System.out.println("Total tasks processed: " + totalTasksProcessed);
        System.out.println("Total tasks dropped: " + totalTasksDropped);
    }

    public static void main(String[] args) {
        Simulation simulation = new Simulation(1.0, 3, 0.5, 5, 1, 100000);
        simulation.run();
    }
}
