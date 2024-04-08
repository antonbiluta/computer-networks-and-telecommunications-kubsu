package ru.biluta.version5;

import lombok.Getter;

@Getter
public class SimulationClock {
    private double currentTime;

    public SimulationClock() {
        this.currentTime = 0.0;
    }

    public void advanceTime(double newTime) {
        if (newTime >= currentTime) {
            currentTime = newTime;
        } else {
            throw new IllegalArgumentException("Simulation time cannot move backwards.");
        }
    }


}
