package main.kotlin.ru.biluta.task2

class TaskManager(
    private val sigma: Double,
    private val lambda: Double,
    private val k: Int,
    private val generators: RandomGenerators = RandomGenerators(sigma, lambda, k)
) {

    fun createTask(): Task {
        val processingTime = generators.generateRayleigh()
        return Task(processingTime)
    }

}