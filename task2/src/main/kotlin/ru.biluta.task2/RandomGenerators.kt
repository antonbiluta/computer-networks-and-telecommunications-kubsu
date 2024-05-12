package main.kotlin.ru.biluta.task2

import org.apache.commons.math3.distribution.AbstractRealDistribution
import org.apache.commons.math3.distribution.GammaDistribution
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.sqrt

class RandomGenerators(
    val sigma: Double,
    val lambda: Double,
    val K: Int,
    private val rayleighDistribution: RayleighDistribution = RayleighDistribution(sigma),
    private val gammaDistribution: GammaDistribution = GammaDistribution(K.toDouble(), 1.0 / lambda)
) {

    fun generateRayleigh(): Double = rayleighDistribution.sample()

    fun generateErlang(): Double = gammaDistribution.sample()

}

class RayleighDistribution(
    val sigma: Double
) : AbstractRealDistribution() {
    override fun density(x: Double): Double = (x / (sigma * sigma)) * exp(- (x * x) / (2 * sigma * sigma))

    override fun cumulativeProbability(x: Double): Double = 1 - exp(- (x * x) / (2 * sigma * sigma))

    override fun inverseCumulativeProbability(p: Double): Double = sigma * sqrt(-2 * ln(1 - p))
    override fun getNumericalMean(): Double = sigma * sqrt(PI / 2)

    override fun getNumericalVariance(): Double = (2 - PI / 2) * sigma * sigma

    override fun getSupportLowerBound(): Double = 0.0

    override fun getSupportUpperBound(): Double = Double.POSITIVE_INFINITY

    override fun isSupportLowerBoundInclusive(): Boolean = true

    override fun isSupportUpperBoundInclusive(): Boolean = false

    override fun isSupportConnected(): Boolean = true

}