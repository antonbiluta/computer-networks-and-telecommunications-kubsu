package ru.biluta;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Simulation {

    private final NewServer server;
    private final Buffer buffer;
    private final int maxTasks;
    private double currentTime = 0.0;
    private int processedTasks = 0;
    private int totalTasks = 0;
    private int droppedTasks = 0;
    private RandomGenerator generator = new RandomGenerator(2.0, 1.0, 3);

    public Simulation(int bufferSize, int maxTasks) {
        this.server = new NewServer();
        this.buffer = new Buffer(bufferSize);
        this.maxTasks = maxTasks;
    }

    public void run() {
        double arrivalTime = currentTime + generator.generateErlang();
        while (processedTasks < maxTasks && totalTasks < maxTasks) {
            double processingTime = generator.generateRayleigh();
            Task newTask = new Task(arrivalTime, processingTime);

            if (!buffer.addTask(newTask)) {
                droppedTasks++;
            }

            if (server.isIdle() && !buffer.isEmpty()) {
                Task task = buffer.getNextTask();
                server.processTask(task, currentTime);
                currentTime = server.getCurrentTime();
            }

            if (server.checkAndCompleteTask(currentTime)) {
                processedTasks++;
            }
            arrivalTime = currentTime + generator.generateErlang();
            double taskCompletionTime = server.isIdle() ? Double.MAX_VALUE : server.getCurrentTime();
            currentTime = Math.min(arrivalTime, taskCompletionTime);
            totalTasks++;
        }
        System.out.println("Total tasks: " + totalTasks);
        System.out.println("Dropped tasks: " + droppedTasks);
        System.out.println("Efficiency: " + ((double)(totalTasks - droppedTasks) / totalTasks));
    }

    public static void main(String[] args) {
        Simulation simulation = new Simulation(3, 10000);
        simulation.run();
    }



//    private static final double LAMBDA_ARRIVAL = 0.5; // Пример параметра для распределения Эрланга
//    private static final double SIGMA_PROCESSING = 1.0; // Пример параметра для распределения Рэлея
//    private static final int BUFFER_SIZE = 3; // Размер буфера
//    private static final int TOTAL_TASKS = 1000; // Общее количество задач для обработки
//
//    public static void main(String[] args) {
//        TaskGenerator taskGenerator = new TaskGenerator(LAMBDA_ARRIVAL, SIGMA_PROCESSING);
//        Server server = new Server(BUFFER_SIZE);
//
//        for (int i = 0; i < TOTAL_TASKS; i++) {
//            Task task = taskGenerator.generateTask();
//            double time = task.getArrivalTime();
//            server.scheduleEvent(new TaskEvent(TaskEvent.EventType.ARRIVAL, task, time));
//        }
//
//        server.run();
//
//        System.out.println(server.getTotalTasks());
//        System.out.println("Вероятность отказа: " + server.getDenialProbability());
//        System.out.println("Вероятность простоя сервера: " + server.getIdleProbability());
//    }

//    private Server server;
//    private RandomGenerator generator;
//    private final int totalIterations;
//    private List<Task> inStream = new ArrayList<>();
//    private List<Task> outStream = new ArrayList<>();
//
//    public Simulation(int queueCapacity, int totalIterations) {
//        this.server = new Server(queueCapacity);
//        this.generator = new RandomGenerator(2.0, 1.0, 3);
//        this.totalIterations = totalIterations;
//    }
//
//    public void run() {
//        double lastArrivalTime = 0.0; // Время прибытия последней задачи
//        for (int i = 0; i < totalIterations; i++) {
//            double interArrivalTime = generator.generateErlang();
//            double processingTime = generator.generateRayleigh();
//            lastArrivalTime += interArrivalTime;
//
//            Task task = new Task(lastArrivalTime, processingTime);
//            server.addTask(task);
//        }
//
//    }
//
//    public void printResult() {
//        System.out.println("Total tasks processed: " + server.getTasksProcessed());
//        System.out.println("Total tasks rejected: " + server.getTasksRejected());
//        System.out.println("Total processing time: " + server.getTotalProcessingTime());
//        System.out.println("Pidle: " + server.getPidle());
//        System.out.println("Pотказа: " + server.getPотказа());
//    }
//
//    public List<Task> getInStream() {
//        return inStream;
//    }
//
//    public List<Task> getOutStream() {
//        return outStream;
//    }
//
//    public static void main(String[] args) {
//        Simulation simulation = new Simulation(3, 10000);
//        simulation.run();
//        simulation.printResult();
//    }

}
