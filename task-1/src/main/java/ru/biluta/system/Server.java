package ru.biluta.system;

import lombok.Getter;
import ru.biluta.model.Event;
import ru.biluta.model.Task;

import java.util.ArrayList;
import java.util.List;

public class Server {

    @Getter
    private final List<Core> cores;
    private final Buffer buffer;
    private final SimulationClock systemClock;

    @Getter
    private double idleTime;
    private double startIdleTime;

    public Server(int numberOfCores, int bufferSize) {
        this.cores = new ArrayList<>(numberOfCores);
        initializeCores(numberOfCores);

        this.buffer = new Buffer(bufferSize);

        this.systemClock = new SimulationClock();

        this.idleTime = 0.0;
        this.startIdleTime = -1;
    }

    // WORK WITH TASK

    public Integer processTask(Task task) {
        markServerBusy();
        for (Core core : cores) {
            if (!core.isBusy()) {
                core.load(task);
                int coreIndex = core.getIndex();
                markCoreBusy(coreIndex);
                return coreIndex;
            }
        }
        return null;
    }

    public void completeTask(Event event) {
        Integer coreIndex = event.getCoreIndex();
        cores.get(coreIndex).free();
        markCoreIdle(coreIndex);
        if (allCoreIsIdle()) {
            markServerIdle();
        }
    }

    public Task getNextTask() {
        return buffer.getNextTask();
    }

    // WORK WITH CORES

    private void initializeCores(int numberOfCores) {
        for (int i = 0; i < numberOfCores; i++) {
            cores.add(new Core(i));
        }
    }

    public boolean hasIdleCore() {
        for (Core core : cores) {
            if (!core.isBusy()) {
                return true;
            }
        }
        return false;
    }

    public boolean allCoreIsIdle() {
        for (Core core : cores) {
            if (core.isBusy()) {
                return false;
            }
        }
        return true;
    }

    public void markCoreIdle(int coreIndex) {
        Core core = cores.get(coreIndex);
        core.setStartIdleTime(systemClock.getCurrentTime());
    }

    public void markCoreBusy(int coreIndex) {
        Core core = cores.get(coreIndex);
        if (core.getStartIdleTime() != -1) {
            core.addIdleTime(systemClock.getCurrentTime());
            core.setStartIdleTime(-1);
        }
    }

    // WORK WITH SERVER

    public void markServerIdle() {
        startIdleTime = systemClock.getCurrentTime();
    }

    public void markServerBusy() {
        if (startIdleTime != -1) {
            idleTime += systemClock.getCurrentTime() - startIdleTime;
            startIdleTime = -1;
        }
    }

    // WORK WITH SYSTEM CLOCK

    public double getSystemTime() {
        return systemClock.getCurrentTime();
    }

    public void advanceSystemTime(double newTime) {
        systemClock.advanceTime(newTime);
    }

    // WORK WITH BUFFER

    public boolean addToBuffer(Task task) {
        return buffer.addTask(task);
    }

    public boolean bufferIsEmpty() {
        return buffer.isEmpty();
    }

}
