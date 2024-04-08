package ru.biluta;

import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.distribution.GammaDistribution;

public class RandomGenerator {
    private static final double shapeRayleigh = 2.0; // Форма для распределения
    private double scaleRayleigh; // Мастшаб для распределения Рэлея
    private double meanErlang; // Среднее значение для распределения Эрланга
    private int shapeErlang; // Форма для распределения Эрланга, в данном случае 3

    public RandomGenerator(double scaleRayleigh, double meanErlang, int shapeErlang) {
        this.scaleRayleigh = scaleRayleigh;
        this.meanErlang = meanErlang;
        this.shapeErlang = shapeErlang;
    }

    public double generateErlang() {
        GammaDistribution gamma = new GammaDistribution(shapeErlang, meanErlang / shapeErlang);
        return gamma.sample();
    }

    public double generateRayleigh() {
        ExponentialDistribution exponential = new ExponentialDistribution(scaleRayleigh);
        return Math.sqrt(exponential.sample());

    }
}
