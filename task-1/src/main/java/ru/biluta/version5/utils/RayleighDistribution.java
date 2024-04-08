package ru.biluta.version5.utils;

import org.apache.commons.math3.distribution.AbstractRealDistribution;
import org.apache.commons.math3.random.Well19937c;

public class RayleighDistribution extends AbstractRealDistribution {
    private static final long serialVersionUID = 20160311L;
    private final double sigma;

    public RayleighDistribution(double sigma) {
        super(new Well19937c());
        this.sigma = sigma;
    }

    @Override
    public double density(double x) {
        return (x / (sigma * sigma)) * Math.exp(- (x * x) / (2 * sigma * sigma));
    }

    @Override
    public double cumulativeProbability(double x) {
        return 1 - Math.exp(- (x * x) / (2 * sigma * sigma));
    }

    @Override
    public double inverseCumulativeProbability(double p) {
        return sigma * Math.sqrt(-2 * Math.log(1 - p));
    }

    @Override
    public double getNumericalMean() {
        return sigma * Math.sqrt(Math.PI / 2);
    }

    @Override
    public double getNumericalVariance() {
        return (2 - Math.PI / 2) * sigma * sigma;
    }

    @Override
    public double getSupportLowerBound() {
        return 0;
    }

    @Override
    public double getSupportUpperBound() {
        return Double.POSITIVE_INFINITY;
    }

    @Override
    public boolean isSupportLowerBoundInclusive() {
        return true;
    }

    @Override
    public boolean isSupportUpperBoundInclusive() {
        return false;
    }

    @Override
    public boolean isSupportConnected() {
        return true;
    }
}
