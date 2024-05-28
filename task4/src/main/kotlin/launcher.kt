import org.apache.commons.math3.distribution.ExponentialDistribution
import org.apache.commons.math3.random.RandomDataGenerator
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.knowm.xchart.BitmapEncoder
import org.knowm.xchart.XYChartBuilder
import java.io.FileOutputStream
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.sqrt

data class Node(val id: Int, var x: Double, var y: Double)

fun main() {
    val T = 100
    val tRange = T / 2..T
    val p1Set = (5..50 step 5)
    val p2Set = (10..100 step 10)
    val n1 = 25 // количество узлов
    val r1 = 10.0 // радиус связи
    val n2 = 25
    val r2 = 20.0
    val iterations = 1000 // количество итераций

    val exponentialDistribution = ExponentialDistribution(100.0)
    val uniformReal = RandomDataGenerator()

    var results1 = mutableMapOf<Pair<Double, Double>, Map<String, Any>>()
    val results2 = mutableMapOf<Pair<Double, Double>, Map<String, Any>>()

    val connectivityResults1 = mutableListOf<Map<String, Double>>()
    val degreeResults1 = mutableListOf<Triple<Map<Int, Double>, Double, Double>>()
    val densityResults1 = mutableListOf<Triple<List<Triple<Double, Double, Double>>, Double, Double>>()
    val connectivityResults2 = mutableListOf<Map<String, Double>>()
    val degreeResults2 = mutableListOf<Triple<Map<Int, Double>, Double, Double>>()
    val densityResults2 = mutableListOf<Triple<List<Triple<Double, Double, Double>>, Double, Double>>()

    for (p1 in p1Set) {
        for (p2 in p2Set) {
            startExperiment(
                n1,
                r1,
                p1,
                p2,
                iterations,
                T,
                tRange,
                exponentialDistribution,
                uniformReal,
                connectivityResults1,
                degreeResults1,
                densityResults1
            )
            startExperiment(
                n2,
                r2,
                p1,
                p2,
                iterations,
                T,
                tRange,
                exponentialDistribution,
                uniformReal,
                connectivityResults2,
                degreeResults2,
                densityResults2
            )
        }
    }
    generateReport("Result_new1", connectivityResults1, degreeResults1, densityResults1)
    generateReport("Result_new2", connectivityResults2, degreeResults2, densityResults2)
}

fun startExperiment(
    n: Int,
    r: Double,
    p1: Int,
    p2: Int,
    iterations: Int,
    innerIterations: Int,
    tRange: IntRange,
    exponentialDistribution: ExponentialDistribution,
    uniformReal: RandomDataGenerator,
    connectivityResults: MutableList<Map<String, Double>>,
    degreeResults: MutableList<Triple<Map<Int, Double>, Double, Double>>,
    densityResults: MutableList<Triple<List<Triple<Double, Double, Double>>, Double, Double>>
) {
    val nodes = initializeNodes(n, exponentialDistribution, uniformReal)
    val connectivityProbability = calculateConnectivityProbability(nodes, r, p1.toDouble(), p2.toDouble(), iterations, innerIterations, tRange)
    val degreeDistribution = calculateDegreeDistribution(nodes, r, p1.toDouble(), p2.toDouble(), iterations, innerIterations, tRange)
    val densityDistribution = calculateDensityDistribution(nodes, r, p1.toDouble(), p2.toDouble(), iterations, innerIterations, tRange)
    connectivityResults.add(connectivityProbability)
    degreeResults.add(Triple(degreeDistribution, p1.toDouble(), p2.toDouble()))
    densityResults.add(Triple(densityDistribution, p1.toDouble(), p2.toDouble()))
}

fun initializeNodes(n: Int, exponentialDistribution: ExponentialDistribution, uniformReal: RandomDataGenerator): List<Node> {
    return List(n) { Node(it, exponentialDistribution.sample(), uniformReal.nextUniform(0.0, 100.0)) }
}

fun calculateConnectivityProbability(
    nodes: List<Node>,
    r: Double,
    p1: Double,
    p2: Double,
    iterations: Int,
    innerIterations: Int,
    tRange: IntRange
): Map<String, Double> {
    var connectedCount = 0

    for (i in 0 until iterations) {
        val newNodes = nodes.map { it.copy() }
        for (t in 0 until innerIterations) {
            val tStep = tRange.random()
            updateNodePositions(newNodes, p1, p2, tStep)
            if (isConnected(newNodes, r)) {
                connectedCount++
            }
        }
    }

    val probability = connectedCount.toDouble() / iterations
    return mapOf("p1" to p1, "p2" to p2, "n" to nodes.size.toDouble(), "r" to r, "probability" to probability)
}

fun updateNodePositions(nodes: List<Node>, p1: Double, p2: Double, t: Int) {
    val exponentialDistribution = ExponentialDistribution(p1)
    val uniformReal = RandomDataGenerator()

    nodes.map { node ->
        node.x = (node.x + t * exponentialDistribution.sample()) % 100
        node.y = (node.y + t * uniformReal.nextUniform(0.0, p2)) % 100
    }
}

fun isConnected(nodes: List<Node>, r: Double): Boolean {
    val graph = nodes.map { mutableListOf<Int>() }

    for (i in nodes.indices) {
        for (j in i + 1 until nodes.size) {
            if (distance(nodes[i], nodes[j]) <= r) {
                graph[i].add(j)
                graph[j].add(i)
            }
        }
    }

    val visited = BooleanArray(nodes.size)
    dfs(0, graph, visited)

    return visited.all { it }
}

fun distance(node1: Node, node2: Node): Double {
    val dx = minOf((node1.x - node2.x).absoluteValue, (100 - (node1.x - node2.x).absoluteValue))
    val dy = minOf((node1.y - node2.y).absoluteValue, (100 - (node1.y - node2.y).absoluteValue))
    return sqrt(dx.pow(2) + dy.pow(2))
}

fun dfs(node: Int, graph: List<MutableList<Int>>, visited: BooleanArray) {
    visited[node] = true
    for (neighbor in graph[node]) {
        if (!visited[neighbor]) {
            dfs(neighbor, graph, visited)
        }
    }
}

fun calculateDegreeDistribution(
    nodes: List<Node>,
    r: Double,
    p1: Double,
    p2: Double,
    iterations: Int,
    innerIterations: Int,
    tRange: IntRange): Map<Int, Double> {
    val degreeCount = mutableMapOf<Int, Int>()

    for (i in 0 until iterations) {
        val newNodes = nodes.map { it.copy() }
        for (t in 0 until innerIterations) {
            val tStep = tRange.random()
            updateNodePositions(newNodes, p1, p2, tStep)
        }
        val degrees = calculateDegrees(newNodes, r)

        degrees.forEach { degree ->
            degreeCount[degree] = degreeCount.getOrDefault(degree, 0) + 1
        }
    }

    val total = iterations * nodes.size
    return degreeCount.mapValues { it.value.toDouble() / total }
}

fun calculateDegrees(nodes: List<Node>, r: Double): List<Int> {
    val degrees = IntArray(nodes.size)

    for (i in nodes.indices) {
        for (j in i + 1 until nodes.size) {
            if (distance(nodes[i], nodes[j]) <= r) {
                degrees[i]++
                degrees[j]++
            }
        }
    }

    return degrees.toList()
}

fun calculateDensityDistribution(
    nodes: List<Node>,
    r: Double,
    p1: Double,
    p2: Double,
    iterations: Int,
    innerIterations: Int,
    tRange: IntRange
): List<Triple<Double, Double, Double>> {
    val densityMap = mutableMapOf<Pair<Int, Int>, Int>()

    for (i in 0 until iterations) {
        val newNodes = nodes.map { it.copy() }
        for (t in 0 until innerIterations) {
            val tStep = tRange.random()
            updateNodePositions(newNodes, p1, p2, tStep)
        }

        newNodes.forEach { node ->
            val cellX = (node.x / r).toInt()
            val cellY = (node.y / r).toInt()
            val cell = Pair(cellX, cellY)
            densityMap[cell] = densityMap.getOrDefault(cell, 0) + 1
        }
    }

    val total = iterations * nodes.size
    return densityMap.map { (cell, count) ->
        Triple(
            cell.first.toDouble() * r,
            cell.second.toDouble() * r,
            count.toDouble() / total
        )
    }
}

fun generateReport(
    fileName: String,
    connectivityResults: List<Map<String, Double>>,
    degreeResults: List<Triple<Map<Int, Double>, Double, Double>>,
    densityResults: List<Triple<List<Triple<Double, Double, Double>>, Double, Double>>
) {
    val workbook = XSSFWorkbook()
    val sheet = workbook.createSheet(fileName)

    // Заполнение таблицы данными
    var rowIndex = 0
    connectivityResults.forEach { connectivityProbability ->
        val rowConnectivity = sheet.createRow(rowIndex++)
        rowConnectivity.createCell(0).setCellValue("p1")
        rowConnectivity.createCell(1).setCellValue("p2")
        rowConnectivity.createCell(2).setCellValue("n")
        rowConnectivity.createCell(3).setCellValue("r")
        rowConnectivity.createCell(4).setCellValue("Probability")
        rowConnectivity.createCell(5).setCellValue(connectivityProbability["p1"]!!)
        rowConnectivity.createCell(6).setCellValue(connectivityProbability["p2"]!!)
        rowConnectivity.createCell(7).setCellValue(connectivityProbability["n"]!!)
        rowConnectivity.createCell(8).setCellValue(connectivityProbability["r"]!!)
        rowConnectivity.createCell(9).setCellValue(connectivityProbability["probability"]!!)
    }

    degreeResults.forEach { (degreeDistribution, p1, p2) ->
        degreeDistribution.forEach { (degree, probability) ->
            val row = sheet.createRow(rowIndex++)
            row.createCell(0).setCellValue("p1")
            row.createCell(1).setCellValue("p2")
            row.createCell(2).setCellValue("Degree")
            row.createCell(3).setCellValue("Probability")
            row.createCell(4).setCellValue(p1)
            row.createCell(5).setCellValue(p2)
            row.createCell(6).setCellValue(degree.toDouble())
            row.createCell(7).setCellValue(probability)
        }
    }

    densityResults.forEach { (densityDistribution, p1, p2) ->
        densityDistribution.forEach { (x, y, density) ->
            val row = sheet.createRow(rowIndex++)
            row.createCell(0).setCellValue("p1")
            row.createCell(1).setCellValue("p2")
            row.createCell(2).setCellValue("X")
            row.createCell(3).setCellValue("Y")
            row.createCell(4).setCellValue("Density")
            row.createCell(5).setCellValue(p1)
            row.createCell(6).setCellValue(p2)
            row.createCell(7).setCellValue(x)
            row.createCell(8).setCellValue(y)
            row.createCell(9).setCellValue(density)
        }
    }

    // Сохранение Excel-файла
    workbook.write(FileOutputStream("$fileName.xlsx"))
}