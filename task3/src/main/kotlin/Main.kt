package ru.biluta

import kotlin.math.*
import kotlin.random.Random

fun main() {
    val area = 10000.0
    val side = sqrt(area)
    val sigma = side / sqrt(2.0 * PI)
    val distribution = RayleighDistribution(sigma)

    val ns = (2..20).toList() // Range of n
    val rs = (5 until 105 step 5).toList() // Range if r
    val experimentsCount = 10000
    val connectivityMatrix = Array(ns.size) { DoubleArray(rs.size) { 0.0 } }

    ns.forEachIndexed { ni, n ->
        rs.forEachIndexed { ri, r ->
            var fullyConnectedCount = 0
            repeat(experimentsCount) {
                val (nodes, matrix) = generateGraph(n, r.toDouble(), distribution)
                val componentsCount = countGraphComponents(matrix, nodes)
                if (componentsCount == 1) {
                    fullyConnectedCount++
                }
            }
            connectivityMatrix[ni][ri] = fullyConnectedCount.toDouble() / experimentsCount
        }
    }

    println("Connectivity Matrix (n rows x r columns):")
    print("r\\n\t")
    ns.forEach { n ->
        print("$n\t")
    }
    println()
    rs.forEachIndexed { ri, r ->
        print("$r\t")
        ns.forEachIndexed { ni, _ ->
            print("${"%.3f".format(connectivityMatrix[ni][ri])}\t")
        }
        println()
    }
}

class RayleighDistribution(private val sigma: Double) {
    private val random = Random.Default

    fun sample(): Double {
        val u = random.nextDouble(1.0) // Генерируем равномерно распределенное число от 0 до 1
        return sigma * sqrt(-2.0 * ln(1 - u))
    }
}

class Node(val index: Int, val x: Double, val y: Double, var component: Int?) {
    override fun toString(): String = "$index $x $y $component"
}

fun generateGraph(n: Int, r: Double, distribution: RayleighDistribution): Pair<List<Node>, List<MutableList<Boolean>>> {
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
