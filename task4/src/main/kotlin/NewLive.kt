//package ru.biluta
//
//import org.apache.commons.math3.distribution.ExponentialDistribution
//import org.apache.commons.math3.distribution.UniformRealDistribution
//import org.apache.poi.xssf.usermodel.XSSFWorkbook
//import java.io.FileOutputStream
//import kotlin.math.exp
//import kotlin.math.pow
//import kotlin.math.sqrt
//
//data class Noda(var x: Double, var y: Double)
//
//fun main() {
//    val p1Set = (5..50 step 5)
//    val p2Set = (10..100 step 10)
//    val n1 = 25
//    val r1 = 10
//    val n2 = 25
//    val r2 = 20
//    val T = 100
//    val experiments = 1000
//
//    val workbook = XSSFWorkbook()
//    val sheet = workbook.createSheet("Results")
//
//    val headerRow = sheet.createRow(0)
//    headerRow.createCell(0).setCellValue("X")
//    for (i in 0..90 step 10) {
//        headerRow.createCell(i / 10 + 1).setCellValue(i.toDouble())
//    }
//
//    for ((rowIndex, x) in (0..500 step 50).withIndex()) {
//        val row = sheet.createRow(rowIndex + 1)
//        row.createCell(0).setCellValue(x.toDouble())
//        for((colIndex, y) in (0..90 step 10).withIndex()) {
//            val result = calculateProbability(x, y, n1, r1, n2, r2, T, experiments)
//            row.createCell(colIndex + 1).setCellValue(result)
//        }
//    }
//
//    FileOutputStream("result.xlsx").use { outputStream ->
//        workbook.write(outputStream)
//    }
//
//    val degreeResults = listOf(
//        calculateDegree(10, 10, n1, r1, n2, r2, T, experiments),
//        calculateDegree(10, 100, n1, r1, n2, r2, T, experiments),
//        calculateDegree(100, 10, n1, r1, n2, r2, T, experiments),
//        calculateDegree(100, 100, n1, r1, n2, r2, T, experiments)
//    )
//    println("Degree results: $degreeResults")
//}
//
//fun calculateProbability(
//    x: Int, y: Int,
//    n1: Int, r1: Int,
//    n2: Int, r2: Int,
//    T: Int, experiments: Int
//): Double {
//    val expDist = ExponentialDistribution(100.0)
//    val uniformDist = UniformRealDistribution(0.0, 100.0)
//    var connectivityCount = 0
//
//    repeat(experiments) {
//        val nodes1 = generateNodes(n1, expDist, uniformDist)
//        val nodes2 = generateNodes(n2, expDist, uniformDist)
//        if (isConnected(nodes1, r1) && isConnected(nodes2, r2)) {
//            connectivityCount++
//        }
//    }
//
//    return connectivityCount.toDouble() / experiments
//}
//
//fun generateNodes(n: Int, expDist: ExponentialDistribution, uniformDist: UniformRealDistribution): List<Noda> {
//    return List(n) {
//        Noda(expDist.sample(), uniformDist.sample())
//    }
//}
//
//fun isConnected(nodes: List<Noda>, r: Int): Boolean {
//    for (i in nodes.indices) {
//        for (j in i + 1 until nodes.size) {
//            if (distance(nodes[i], nodes[j]) < r) {
//                return true
//            }
//        }
//    }
//    return false
//}
//
//fun distance(node1: Noda, node2: Noda): Double {
//    return sqrt((node1.x - node2.x).pow(2) + (node1.y - node2.y).pow(2))
//}
//
//fun calculateDegree(
//    p1: Int, p2: Int,
//    n1: Int, r1: Int,
//    n2: Int, r2: Int,
//    T: Int, experiments: Int
//): Double {
//    val expDist = ExponentialDistribution(100.0)
//    val uniformDist = UniformRealDistribution(0.0, 100.0)
//    var totalDegree = 0
//
//    repeat(experiments) {
//        val nodes1 = generateNodes(n1, expDist, uniformDist)
//        val nodes2 = generateNodes(n2, expDist, uniformDist)
//        totalDegree += calculateGraphDegree(nodes1, r1)
//        totalDegree += calculateGraphDegree(nodes2, r2)
//    }
//    return totalDegree.toDouble() / experiments
//}
//
//fun calculateGraphDegree(nodes: List<Noda>, r: Int): Int {
//    var degreeSum = 0
//    for (i in nodes.indices) {
//        var degree = 0
//        for (j in nodes.indices) {
//            if ( i != j && distance(nodes[i], nodes[j]) < r) {
//                degree++
//            }
//        }
//        degreeSum += degree
//    }
//    return degreeSum
//}