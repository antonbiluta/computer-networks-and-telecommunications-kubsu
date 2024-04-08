package ru.biluta;

import org.apache.commons.math3.distribution.AbstractRealDistribution;
import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.random.JDKRandomGenerator;
import ru.biluta.utils.RayleighDistribution;

public class TaskGenerator {
    private final ExponentialDistribution arrivalDistribution;
    private final RayleighDistribution processingDistribution;

    public TaskGenerator(double lambdaArrival, double sigmaProcessing) {
        this.arrivalDistribution = new ExponentialDistribution(new JDKRandomGenerator(), 1 / lambdaArrival);
        this.processingDistribution = new RayleighDistribution(sigmaProcessing);
    }

    public Task generateTask() {
        double arrivalTime = arrivalDistribution.sample();
        double processingTime = processingDistribution.sample();
        return new Task(arrivalTime, processingTime);
    }
}
