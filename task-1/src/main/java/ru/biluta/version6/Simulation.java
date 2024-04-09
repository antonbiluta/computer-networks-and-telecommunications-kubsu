package ru.biluta.version6;

import ru.biluta.utils.SimulationClock;
import ru.biluta.version5.utils.RandomGenerators;

import java.util.PriorityQueue;

public class Simulation {
    private PriorityQueue<Event> eventQueue;
    private Server server;
    private Buffer buffer;
    private RandomGenerators randomGenerators;
    private SimulationClock clock;
    private int totalTasksGenerated;
    private int totalTasksProcessed;
    private int totalTasksDropped;
    private final int maxTasks;

    public Simulation(double sigma, int k, double lambda, int bufferSize, int cores, int maxTasks) {
        this.maxTasks = maxTasks;
        this.buffer = new Buffer(bufferSize);
        this.clock = new SimulationClock();
        this.eventQueue = new PriorityQueue<>();
        this.server = new Server(cores);

        this.totalTasksGenerated = 0;
        this.totalTasksProcessed = 0;
        this.totalTasksDropped = 0;
        this.randomGenerators = new RandomGenerators(sigma, lambda, k);
    }

    public void run() {
        scheduleTaskArrival();

        while (!eventQueue.isEmpty() && (totalTasksProcessed < maxTasks)) {
            Event currentEvent = eventQueue.poll();
            clock.advanceTime(currentEvent.getEventTime());

            switch (currentEvent.getEventType()) {
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
            double nextArrivalTime = clock.getCurrentTime() + randomGenerators.generateErlang();
            Task newTask = new Task(randomGenerators.generateRayleigh());
            Event arrivalEvent = new Event(Event.EventType.TASK_ARRIVAL, nextArrivalTime, newTask);
            eventQueue.add(arrivalEvent);
            totalTasksGenerated++;
        }
    }

    private void handleTaskArrival(Event event) {
        Task task = event.getTask();
        if (server.hasIdleCore()) {
            server.processTask(task);
            double completionTime = clock.getCurrentTime() + task.getProcessingTime();
            Event newEvent = new Event(Event.EventType.TASK_COMPLETION, completionTime, task);
            eventQueue.add(newEvent);
            totalTasksProcessed++;
        } else {
            boolean added = buffer.addTask(task);
            if (!added) {
                totalTasksDropped++;
            }
        }
        scheduleTaskArrival();
    }

    private void handleTaskCompletion(Event event) {
        server.completeTask(event);
        if (!buffer.isEmpty()) {
            Task newTask = buffer.getNextTask();
            server.processTask(newTask);
            totalTasksProcessed++;
        }
    }

    private void printStatistics() {
        double probabilityOfIdle = server.getIdleTime() / clock.getCurrentTime();
        double probabilityOfRejection = (double) totalTasksDropped / (totalTasksGenerated + totalTasksDropped);
        System.out.println("Probability of idle: " + probabilityOfIdle);
        System.out.println("Probability of rejection: " + probabilityOfRejection);
        System.out.println("Total tasks processed: " + totalTasksProcessed);
        System.out.println("Total tasks dropped: " + totalTasksDropped);
        System.out.println("Total tasks generated: " + totalTasksGenerated);
    }

    public static void main(String[] args) {
        Simulation simulation = new Simulation(0.5, 3, 0.2, 3, 1, 10000);
        simulation.run();
    }
}
