package ru.biluta.version6;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class Server {
    private final List<Boolean> cores;


    @Getter
    private final double idleTime;

    public Server(int numberOfCores) {
        this.cores = new ArrayList<>(numberOfCores);
        for (int i = 0; i < numberOfCores; i++) {
            cores.add(false);
        }
        this.idleTime = 0.0;
    }

    public boolean processTask(Task task) {
        for (int i = 0; i < cores.size(); i++) {
            if (!cores.get(i)) {
                cores.set(i, true);
                return true;
            }
        }
        return false;
    }

    public void completeTask(Event event) {
        for (int i = 0; i < cores.size(); i++) {
            if (cores.get(i)) {
                cores.set(i, false);
                break;
            }
        }
    }

    public boolean hasIdleCore() {
        return cores.contains(false);
    }
}
