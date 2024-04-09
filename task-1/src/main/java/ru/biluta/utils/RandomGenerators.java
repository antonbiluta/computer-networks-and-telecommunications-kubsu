package ru.biluta.utils;

import org.apache.commons.math3.distribution.GammaDistribution;
import org.apache.commons.math3.random.Well19937c;

public class RandomGenerators {
    private final Well19937c rng;
    private final double sigma;
    private final double lambda;
    private final int k;

    public RandomGenerators(double sigma, double lambda, int k) {
        this.rng = new Well19937c();
        this.sigma = sigma;
        this.lambda = lambda;
        this.k = k;
    }

    public double generateRayleigh() {
        RayleighDistribution rayleighDistribution = new RayleighDistribution(sigma);
        return rayleighDistribution.sample();
    }

    public double generateErlang() {
        GammaDistribution gammaDistribution = new GammaDistribution(rng, k, 1.0 / lambda);
        return gammaDistribution.sample();
    }
}
