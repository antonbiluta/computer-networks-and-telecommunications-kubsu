package ru.biluta

import org.apache.commons.math3.distribution.ExponentialDistribution
import org.apache.commons.math3.distribution.UniformRealDistribution
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.FileOutputStream
import kotlin.random.Random
import kotlin.math.*

class Node(var x: Double, var y: Double)

fun main() {
    // City Section Mobility Model: Перемещение узлов возможно только в определенных направлениях (как по улицам города).

    // 3
    // Зависимость среднего количества компонент от параметров мобильности
    // p1, p2 и параметров n, r сети (для двух пар (n1 , r1 ), (n2 , r2 ) различных значений);
    // 7
    // Распределение вероятностей P{deg() = k} для 4 различных значений пар параметров (p1, p2 ).
    // 11
    // Двумерная плотность a(x, y) вероятности распределения узлов сети в
    // области S для двух различных значений пар параметров мобильности
    // p1, p2 и одной пары параметров n, r сети на основе координат узлов
    // графов G(t) на интервале t = [T/2; T].

    val experiments = 1000
    val T = 100
    val n1 = 25
    val r1 = 10.0
    val n2 = 25
    val r2 = 20.0
    val p1Values = (5..50 step 5).toList()
    val p2Values = (10..100 step 10).toList()

    val connectivityResults1 = mutableListOf<Triple<Double, Double, Double>>()
    val connectivityResults2 = mutableListOf<Triple<Double, Double, Double>>()

    for (p1 in p1Values) {
        for (p2 in p2Values) {
            var connectedCount1 = 0
            var connectedCount2 = 0

            for (experiment in 0 until experiments) {
                val nodes1 = generateGraph(n1)
                val nodes2 = generateGraph(n2)

                for (t in 0 until T) {
                    val adjacencyMatrix1 = computeAdjacencyMatrix(nodes1, r1)
                    val adjacencyMatrix2 = computeAdjacencyMatrix(nodes2, r2)

                    if (t >= T / 2) {
                        val componentCount1 = countComponents(adjacencyMatrix1)
                        val componentCount2 = countComponents(adjacencyMatrix2)

                        if (componentCount1 < 2) connectedCount1++
                        if (componentCount2 < 2) connectedCount2++
                    }

                    mutateGraph(nodes1, p1, p2)
                    mutateGraph(nodes2, p1, p2)
                }
            }

            val connectivityProbability1 = connectedCount1.toDouble() / experiments
            val connectivityProbability2 = connectedCount2.toDouble() / experiments

            connectivityResults1.add(Triple(p1.toDouble(), p2.toDouble(), connectivityProbability1))
            connectivityResults2.add(Triple(p1.toDouble(), p2.toDouble(), connectivityProbability2))

            println("p1: $p1, p2: $p2, n1: $n1, r1: $r1, Connectivity Probability: $connectivityProbability1")
            println("p1: $p1, p2: $p2, n2: $n2, r2: $r2, Connectivity Probability: $connectivityProbability2")
        }
    }
    saveToExcel(connectivityResults1, connectivityResults2)
}

fun generateGraph(n: Int): List<Node> {
    val xDistribution = ExponentialDistribution(100.0)
    val yDistribution = UniformRealDistribution(0.0, 100.0)
    val nodes = mutableListOf<Node>()
    for (i in 0 until n) {
        nodes.add(Node(xDistribution.sample().roundToInt().toDouble(), yDistribution.sample().roundToInt().toDouble()))
    }
    return nodes
}

fun mutateGraph(nodes: List<Node>, dx: Int, dy: Int) {
    for (node in nodes) {
        if (Random.nextBoolean()) {
            node.x += Random.nextInt(-dx, dx + 1)
            if (node.x < 0) {
                node.x = abs(node.x)
            }
            if (node.x > 100) {
                node.x = 200 - node.x
            }
        } else {
            node.y += Random.nextInt(-dy, dy + 1)
            if (node.y < 0) {
                node.y = abs(node.y)
            }
            if (node.y > 100) {
                node.y = 200 - node.y
            }
        }
    }
}

fun computeAdjacencyMatrix(nodes: List<Node>, r: Double): List<List<Boolean>> {
    val matrix = List(nodes.size) { MutableList(nodes.size) { false } }
    for (i in nodes.indices) {
        val iNode = nodes[i]
        for (j in i + 1 until nodes.size) {
            val jNode = nodes[j]
            if (distance(iNode, jNode) < r) {
                matrix[i][j] = true
                matrix[j][i] = true
            }
        }
    }
    return matrix
}

fun countComponents(adjacencyMatrix: List<List<Boolean>>): Int {
    val nodeColors = MutableList<Int?>(adjacencyMatrix.size) { null }
    var componentCount = 0
    for (i in nodeColors.indices) {
        if (nodeColors[i] == null) {
            componentCount++
            colorGraph(adjacencyMatrix, nodeColors, i, componentCount)
        }
    }
    return componentCount
}

fun colorGraph(adjacencyMatrix: List<List<Boolean>>, nodeColors: MutableList<Int?>, currentNodeIdx: Int, color: Int) {
    if (nodeColors[currentNodeIdx] != null) {
        return
    }
    nodeColors[currentNodeIdx] = color
    for (i in adjacencyMatrix.indices) {
        if (adjacencyMatrix[currentNodeIdx][i]) {
            colorGraph(adjacencyMatrix, nodeColors, i, color)
        }
    }
}

fun distance(node1: Node, node2: Node): Double {
    val dx = minOf((node1.x - node2.x).absoluteValue, (100 - (node1.x - node2.x).absoluteValue))
    val dy = minOf((node1.y - node2.y).absoluteValue, (100 - (node1.y - node2.y).absoluteValue))
    return sqrt(dx.pow(2) + dy.pow(2))
}

fun saveToExcel(connectivityResults1: List<Triple<Double, Double, Double>>, connectivityResults2: List<Triple<Double, Double, Double>>) {
    val workbook = XSSFWorkbook()
    val sheet1 = workbook.createSheet("Connectivity n1, r1")
    val sheet2 = workbook.createSheet("Connectivity n2, r2")

    // Save connectivity results for n1, r1
    saveConnectivityResults(sheet1, connectivityResults1)

    // Save connectivity results for n2, r2
    saveConnectivityResults(sheet2, connectivityResults2)

    val fileOut = FileOutputStream("connectivity_results.xlsx")
    workbook.write(fileOut)
    fileOut.close()
}

fun saveConnectivityResults(sheet: org.apache.poi.ss.usermodel.Sheet, results: List<Triple<Double, Double, Double>>) {
    val p1List = results.map { it.first }.distinct().sorted()
    val p2List = results.map { it.second }.distinct().sorted()

    // Create header row
    val headerRow = sheet.createRow(0)
    headerRow.createCell(0).setCellValue("p1 \\ p2")
    p2List.forEachIndexed { index, p2 ->
        headerRow.createCell(index + 1).setCellValue(p2)
    }

    // Fill in the data
    p1List.forEachIndexed { rowIndex, p1 ->
        val row = sheet.createRow(rowIndex + 1)
        row.createCell(0).setCellValue(p1)
        p2List.forEachIndexed { colIndex, p2 ->
            val cell = row.createCell(colIndex + 1)
            val connectivity = results.find { it.first == p1 && it.second == p2 }?.third ?: 0.0
            cell.setCellValue(connectivity)
        }
    }
}