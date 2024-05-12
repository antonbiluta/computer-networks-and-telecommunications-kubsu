package main.kotlin.ru.biluta.task2

data class SimulationClock(
    var currentTime: Double = 0.0
) {

    fun advanceTime(newTime: Double) {
        when (newTime >= currentTime) {
            true -> currentTime = newTime
            else -> throw IllegalArgumentException("Simulation time cannot move backwards.")
        }
    }

}