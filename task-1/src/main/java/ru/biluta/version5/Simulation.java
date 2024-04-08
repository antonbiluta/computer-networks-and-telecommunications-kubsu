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
    private double idleTime;
    private double lastEventTime;

    public Simulation(double sigma, int k, double lambda, int bufferSize, int cores, int maxTasks) {
        this.eventQueue = new PriorityQueue<>();
        this.clock = new SimulationClock();
        this.server = new Server(cores, eventQueue, clock);
        this.buffer = new Buffer(bufferSize);
        this.randomGenerators = new RandomGenerators(sigma, lambda, k);
        this.totalTasksGenerated = maxTasks;
        this.totalTasksProcessed = 0;
        this.totalTasksDropped = 0;
        this.idleTime = 0.0;
        this.lastEventTime = 0.0;

        scheduleNextTaskArrival();
    }

    private void scheduleNextTaskArrival() {
        if (totalTasksGenerated > 0) {
            double nextArrivalTime = clock.getCurrentTime() + randomGenerators.generateErlang();
            eventQueue.add(new Event(Event.EventType.TASK_ARRIVAL, nextArrivalTime, new Task(nextArrivalTime, randomGenerators.generateRayleigh()), null));
            totalTasksGenerated--;
        }
    }

    public void run() {
        while (!eventQueue.isEmpty() || totalTasksGenerated > 0) {
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
        totalTasksProcessed++;
        dispatchTasks();
    }

    private void dispatchTasks() {
        while (!buffer.isEmpty() && server.hasIdleCore()) {
            Task nextTask = buffer.getNextTask();
            Integer coreIndex = server.processTask(nextTask);
            double completionTime = clock.getCurrentTime() + nextTask.getProcessingTime();
            eventQueue.add(new Event(Event.EventType.TASK_COMPLETION, completionTime, nextTask, coreIndex));
        }
    }

    private void printStatistics() {
        double probabilityOfIdle = idleTime / clock.getCurrentTime();
        double probabilityOfRejection = (double) totalTasksDropped / (totalTasksGenerated + totalTasksDropped);
        System.out.println("Probability of idle: " + probabilityOfIdle);
        System.out.println("Probability of rejection: " + probabilityOfRejection);
        System.out.println("Total tasks processed: " + totalTasksProcessed);
        System.out.println("Total tasks dropped: " + totalTasksDropped);
    }

    public static void main(String[] args) {
        Simulation simulation = new Simulation(1.0, 3, 0.5, 5, 1, 10000);
        simulation.run();
    }
}
