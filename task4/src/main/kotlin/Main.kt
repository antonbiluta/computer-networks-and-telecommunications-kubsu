package ru.biluta

import org.apache.commons.math3.distribution.ExponentialDistribution
import org.apache.commons.math3.distribution.UniformRealDistribution
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.FileOutputStream
import java.math.BigDecimal
import kotlin.random.Random
import kotlin.math.*

data class Node(var x: Double, var y: Double, var vx: Double, var vy: Double)

fun main() {
    // A Boundless Simulation Area Mobility Model: Модель с преобразованием двумерной области расположения сети в торообразную.

    // 1
    // Pc(p1, p2, n, r) – вероятность связности сети как функцию параметров мобильности p1, p2
    // и параметров n, r (для двух пар (n1 , r1), (n2 , r2) различных значений);
    // 7
    // Распределение вероятностей P{deg() = k} для 4 различных значений пар параметров (p1, p2 ).
    // 11
    // Двумерная плотность a(x, y) вероятности распределения узлов сети в
    // области S для двух различных значений пар параметров мобильности
    // p1, p2 и одной пары параметров n, r сети на основе координат узлов
    // графов G(t) на интервале t = [T/2; T].

    // Параметры сети
    val n1 = 25 // кол-во узлов
    val r1 = 10.0 // радиус связи
    val n2 = 25
    val r2 = 20.0
    val n = n2
    val r = r2

    // Параметры мобильности
    val p1Range = 5..50 step 5
    val p2Range = 10..100 step 10

    // Параметры симуляции
    val deltaT = 1.0 // шаг времени
    val T = 100.0 // Общее время симуляции
    val halfT = T / 2

    val numExperiments = 1000 // кол-во экспериментов

    val resultsPc = mutableMapOf<Pair<Int, Int>, Double>()
    val resultsDeg = mutableMapOf<Pair<Int, Int>, MutableList<Int>>()
    val resultsDensity = mutableMapOf<Pair<Int, Int>, MutableMap<Pair<Int, Int>, Int>>()

    for (p1 in p1Range) {
        for (p2 in p2Range) {
            var connectedCount = 0
            val degreeDistribution = mutableListOf<Int>()
            val nodePositionGrid = mutableMapOf<Pair<Int, Int>, Int>()

            repeat(numExperiments) {
                val nodes = initializeNodes(n, p1, p2)
                val graph = mutableMapOf<Int, MutableList<Int>>()

                // Симуляция движения узлов и вычисление характеристик графа
                for (t in 0..(T / deltaT).toInt()) {
                    val currentTime = t * deltaT
                    updateNodePositions(nodes, deltaT)
                    buildGraph(nodes, graph, r)

                    if (currentTime >= halfT) {
                        nodes.forEach {
                            val gridX = (it.x / 50 * 50).toInt()
                            val gridY = (it.y / 10 * 10).toInt()
                            val x = it.x.toInt()
                            val y = it.y.toInt()
                            nodePositionGrid[gridX to gridY] = nodePositionGrid.getOrDefault(gridX to gridY, 0) + 1
                        }
                    }

                    degreeDistribution.addAll(graph.values.map { it.size })
                }

                if (isGraphConnected(graph)) {
                    connectedCount++
                }
            }
            println("n=$n, r=$r, p1=$p1, p2=$p2: ${connectedCount / numExperiments.toDouble()}")
            resultsPc[p1 to p2] = connectedCount / numExperiments.toDouble()
            resultsDeg[p1 to p2] = degreeDistribution
            resultsDensity[p1 to p2] = nodePositionGrid
        }
    }

    // Сохранение результатов в Excel
    saveResultsToExcel(resultsPc, resultsDeg, resultsDensity, p1Range, p2Range)
}

fun initializeNodes(n: Int, p1: Int, p2: Int): List<Node> {
    // Инициализация узлов с случайными координатами и скоростями
    return List(n) {
        Node(
            Random.nextDouble(0.0, 100.0), // координата x
            Random.nextDouble(0.0, 100.0), // координата y
            Random.nextDouble(-p1.toDouble(), p1.toDouble()), // скорость по x
            Random.nextDouble(-p2.toDouble(), p2.toDouble()) // скорость по y
        )
    }
}

fun updateNodePositions(nodes: List<Node>, deltaT: Double) {
    // Обновление позиций узлов с учетом торообразной области
    nodes.forEach { node ->
        node.x = (node.x + node.vx * deltaT) % 100.0
        node.y = (node.y + node.vy * deltaT) % 100.0
        if (node.x < 0) node.x += 100.0
        if (node.y < 0) node.y += 100.0
    }
}

fun buildGraph(nodes: List<Node>, graph: MutableMap<Int, MutableList<Int>>, r: Double) {
    // Построение графа на основе текущих позиций узлов
    graph.clear()
    for (i in nodes.indices) {
        for (j in i + 1 until nodes.size) {
            if (distance(nodes[i], nodes[j]) < r) {
                graph.getOrPut(i) { mutableListOf() }.add(j)
                graph.getOrPut(j) { mutableListOf() }.add(i)
            }
        }
    }
}

fun distance(node1: Node, node2: Node): Double {
    // Вычисление расстояния между двумя узлами с учетом торообразной области
    val dx = min(abs(node1.x - node2.x), 100 - abs(node1.x - node2.x))
    val dy = min(abs(node1.y - node2.y), 100 - abs(node1.y - node2.y))
    return sqrt(dx * dx + dy * dy)
}

fun isGraphConnected(graph: MutableMap<Int, MutableList<Int>>): Boolean {
    // Проверка связности графа с помощью поиска в глубину (DFS)
    if (graph.isEmpty()) return false

    val visited = mutableSetOf<Int>()
    val stack = mutableListOf(graph.keys.first())

    while (stack.isNotEmpty()) {
        val node = stack.removeAt(stack.size - 1)
        if (node !in visited) {
            visited.add(node)
            stack.addAll(graph[node] ?: emptyList())
        }
    }

    return visited.size == graph.size
}

fun saveResultsToExcel(
    resultsPc: Map<Pair<Int, Int>, Double>,
    resultsDeg: Map<Pair<Int, Int>, List<Int>>,
    resultsDensity: Map<Pair<Int, Int>, Map<Pair<Int, Int>, Int>>,
    p1Range: IntProgression,
    p2Range: IntProgression
) {
    val workbook: Workbook = XSSFWorkbook()
    val sheetPc = workbook.createSheet("Probability Connectivity")
    val sheetDeg = workbook.createSheet("Degree Distribution")
    val sheetDensity = workbook.createSheet("Node Density")

    // Сохранение результатов Pc
    val rowHeaderPc = sheetPc.createRow(0)
    p2Range.forEachIndexed { index, p2 -> rowHeaderPc.createCell(index + 1).setCellValue(p2.toDouble()) }
    p1Range.forEachIndexed { rowIndex, p1 ->
        val row = sheetPc.createRow(rowIndex + 1)
        row.createCell(0).setCellValue(p1.toDouble())
        p2Range.forEachIndexed { colIndex, p2 ->
            row.createCell(colIndex + 1).setCellValue(resultsPc[p1 to p2] ?: 0.0)
        }
    }

    // Сохранение распределения степеней
    val rowHeaderDeg = sheetDeg.createRow(0)
    rowHeaderDeg.createCell(0).setCellValue("p1")
    rowHeaderDeg.createCell(1).setCellValue("p2")
    rowHeaderDeg.createCell(2).setCellValue("Degree")
    rowHeaderDeg.createCell(3).setCellValue("Count")
    rowHeaderDeg.createCell(4).setCellValue("Probability")
    var rowIndexDeg = 1
    resultsDeg.forEach { (params, degrees) ->
        degrees.groupingBy { it }.eachCount().forEach { (degree, count) ->
            val row = sheetDeg.createRow(rowIndexDeg++)
            row.createCell(0).setCellValue(params.first.toDouble())
            row.createCell(1).setCellValue(params.second.toDouble())
            row.createCell(2).setCellValue(degree.toDouble())
            row.createCell(3).setCellValue(count.toDouble())
            row.createCell(4).setCellValue(count / degrees.size.toDouble())
        }
    }

    // Сохранение двумерной плотности
    var rowIndexDensity = 0
    resultsDensity.forEach { (params, grid) ->
        val totalNodes = grid.values.size
        val header = sheetDensity.createRow(rowIndexDensity++)
        header.createCell(0).setCellValue("p1=${params.first}, p2=${params.second}")

        val maxX = grid.keys.maxOf { it.first }
        val maxY = grid.keys.maxOf { it.second }

        val xRow = sheetDensity.createRow(rowIndexDensity++)
        var countRow = 0
        for (x in 0..maxX step 10) {
            xRow.createCell(countRow + 1).setCellValue(x.toDouble())
            countRow++
        }

        for (y in 0..maxY step 5) {
            val row = sheetDensity.createRow(rowIndexDensity++)
            row.createCell(0).setCellValue(y.toDouble())
            countRow = 0
            for (x in 0..maxX step 10) {
                val probability = grid.getOrDefault(x to y, 0).toDouble() / totalNodes
                row.createCell(countRow + 1).setCellValue(probability)
                countRow++
            }
        }
        rowIndexDensity += 3
    }

    FileOutputStream("NetworkSimulationResults.xlsx").use { fileOut ->
        workbook.write(fileOut)
    }
}