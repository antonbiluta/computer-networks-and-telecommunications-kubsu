package ru.biluta.system;

import lombok.Getter;
import lombok.Setter;
import ru.biluta.model.Task;

@Getter
public class Core {

    private final int index;
    private boolean isBusy;
    private Task currentTask;
    private double idleTime;
    @Setter
    private double startIdleTime;

    public Core(int index) {
        this.index = index;
        this.isBusy = false;
        this.idleTime = 0.0;
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
        idleTime += currentTime - startIdleTime;
    }

}
