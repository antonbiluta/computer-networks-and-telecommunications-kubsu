package ru.biluta.task_3

import org.apache.commons.math3.distribution.RealDistribution
import org.apache.commons.math3.distribution.RayleighDistribution
import kotlin.math.pow
import kotlin.math.sqrt

fun main() {
    val area = 10000.0 // Площадь квадрата
    val side = sqrt(area) // Сторона квадрата
    for (n in 1..20) {
        println("n=$n")
        for (r in 5 until 105 step 5) {
            val experimentsCount = 10000
            var componentCountSum = 0
            val resultSum = mutableMapOf<Int, Int>()
            for (i in 0 until experimentsCount) {
                val (nodes, matrix) = generateGraph(n, r.toDouble(), side)
                val nodeDegreeDistribution = getNodeDegreeDistribution(matrix)
                sumResults(nodeDegreeDistribution, resultSum)
                val countGraphComponents = countGraphComponents(matrix, nodes)
                componentCountSum += countGraphComponents
            }
            val results = resultSum.mapValues { 1.0 * it.value / experimentsCount }
            val meanComponentsCount = 1.0 * componentCountSum / experimentsCount
            println("Average components: $meanComponentsCount")
        }
    }
}

class Node(val index: Int, val x: Double, val y: Double, var component: Int?) {
    override fun toString(): String = "$index $x $y $component"
}

fun sumResults(source: Map<Int, Int>, destination: MutableMap<Int, Int>) {
    source.forEach { (k, v) -> destination[k] = (destination[k] ?: 0) + v }
}

fun getNodeDegreeDistribution(matrix: List<MutableList<Boolean>>): Map<Int, Int> {
    val result = mutableMapOf<Int, Int>()
    matrix.forEachIndexed { i, row ->
        val degree = row.count { it }
        result[degree] = (result[degree] ?: 0) + 1
    }
    return result
}

fun generateGraph(n: Int, r: Double, side: Double): Pair<List<Node>, List<MutableList<Boolean>>> {
    val distribution: RealDistribution = RayleighDistribution(side / sqrt(2.0 * Math.PI * n))
    val nodes = List(n) { index ->
        Node(index, distribution.sample(), distribution.sample(), null)
    }
    val matrix = List(n) { MutableList(n) { false } }
    nodes.forEachIndexed { i, nodeA ->
        for (j in i + 1 until n) {
            val nodeB = nodes[j]
            if ((nodeA.x - nodeB.x).pow(2) + (nodeA.y - nodeB.y).pow(2) < r * r) {
                matrix[i][j] = true
                matrix[j][i] = true
            }
        }
    }
    return nodes to matrix
}

fun countGraphComponents(matrix: List<MutableList<Boolean>>, nodes: List<Node>): Int {
    var componentsCount = 0
    nodes.forEachIndexed { i, node ->
        if (node.component == null) {
            markGraphComponent(matrix, nodes, i, componentsCount++)
        }
    }
    return componentsCount
}

fun markGraphComponent(matrix: List<MutableList<Boolean>>, nodes: List<Node>, i: Int, component: Int) {
    if (nodes[i].component == null) {
        nodes[i].component = component
        matrix[i].forEachIndexed { j, isConnected ->
            if (isConnected) markGraphComponent(matrix, nodes, j, component)
        }
    }
}