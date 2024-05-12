package main.kotlin.ru.biluta.task2

data class Core(
    val index: Int,
    var isBusy: Boolean = false,
    var currentTask: Task? = null,
    val idleTimes: MutableList<Double> = mutableListOf(),
    var startIdleTime: Double = 0.0
) {

    fun free() {
        currentTask = null
        isBusy = false
    }

    fun load(task: Task) {
        currentTask = task
        isBusy = true
    }

    fun addIdleTime(currentTime: Double) {
        val diff = currentTime - startIdleTime
        idleTimes.add(diff)
    }

    fun calculateSummaryIdleTime(): Double = idleTimes.sum()

}