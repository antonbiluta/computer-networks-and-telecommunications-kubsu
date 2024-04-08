package ru.biluta.utils;

import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.JDKRandomGenerator;

public class RayleighDistribution {
    private final double sigma;
    private final RandomGenerator random;

    public RayleighDistribution(double sigma) {
        this.sigma = sigma;
        this.random = new JDKRandomGenerator();
    }

    public double sample() {
        double uniformRandom = random.nextDouble();
        return sigma * Math.sqrt(-2 * Math.log(uniformRandom));
    }
}
