package ru.biluta.task1.system;

import lombok.Getter;
import lombok.Setter;
import ru.biluta.task1.model.Task;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Core {

    private final int index;
    private boolean isBusy;
    private Task currentTask;
    private List<Double> idleTimes;
    @Setter
    private double startIdleTime;

    public Core(int index) {
        this.index = index;
        this.isBusy = false;
        this.idleTimes = new ArrayList<>();
        this.startIdleTime = -1;
    }

    public void free() {
        currentTask = null;
        isBusy = false;
    }

    public void load(Task task) {
        currentTask = task;
        isBusy = true;
    }

    public void addIdleTime(double currentTime) {
        double idleTime = currentTime - startIdleTime;
        idleTimes.add(idleTime);
    }

    public double calculateSummaryIdleTime() {
        double sum = 0;
        for (double idleTime : idleTimes) {
            sum += idleTime;
        }
        return sum;
    }

}
