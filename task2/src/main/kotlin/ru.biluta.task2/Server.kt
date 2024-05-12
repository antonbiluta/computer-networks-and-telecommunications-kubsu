package main.kotlin.ru.biluta.task2

class Server(
    numberOfCores: Int,
    private val bufferSize: Int,
    private val cores: MutableList<Core>,
    private val buffer: Buffer = Buffer(bufferSize),
    private val systemClock: SimulationClock = SimulationClock(),
    private val idleTimes: MutableList<Double> = mutableListOf(),
    private var startIdleTime: Double = -1.0
) {

    init {
        initializeCores(numberOfCores)
    }

    // WORK WITH TASK
    fun processTask(task: Task): Int? {
        markServerBusy()
        for (core in cores) {
            if (!core.isBusy) {
                core.load(task)
                val coreIndex: Int = core.index
                markCoreBusy(coreIndex)
                return coreIndex
            }
        }
        return null
    }

    fun completeTask(event: Event) {
        val coreIndex: Int = event.coreIndex ?: return
        cores[coreIndex].free()
        markCoreIdle(coreIndex)
        if (allCoreIsIdle()) {
            markServerIdle()
        }
    }

    val nextTask: Task = buffer.getNextTask()

    // WORK WITH CORES
    private fun initializeCores(numberOfCores: Int) {
        for (i in 0 until numberOfCores) {
            cores.add(Core(i))
        }
    }

    fun hasIdleCore(): Boolean {
        for (core in cores) {
            if (!core.isBusy) {
                return true
            }
        }
        return false
    }

    fun allCoreIsIdle(): Boolean {
        for (core in cores) {
            if (core.isBusy) {
                return false
            }
        }
        return true
    }

    fun markCoreIdle(coreIndex: Int) {
        val core: Core = cores[coreIndex]
        core.startIdleTime = systemClock.currentTime
    }

    fun markCoreBusy(coreIndex: Int) {
        val core: Core = cores[coreIndex]
        if (core.startIdleTime != -1.0) {
            core.addIdleTime(systemClock.currentTime)
            core.startIdleTime = -1.0
        }
    }

    // WORK WITH SERVER
    fun markServerIdle() {
        startIdleTime = systemClock.currentTime
    }

    fun markServerBusy() {
        if (startIdleTime != -1.0) {
            val idleTime: Double = systemClock.currentTime - startIdleTime
            idleTimes.add(idleTime)
            startIdleTime = -1.0
        }
    }

    val systemTime: Double = systemClock.currentTime

    fun advanceSystemTime(newTime: Double) = systemClock.advanceTime(newTime)

    // WORK WITH BUFFER
    fun addToBuffer(task: Task): Boolean = buffer.addTask(task)

    fun bufferIsEmpty(): Boolean = buffer.isEmpty()

    // FOR STATISTIC
    fun calculateSummaryIdleTime(): Double {
        var sum = 0.0
        for (idleTime in idleTimes) {
            sum += idleTime
        }
        return sum
    }
}
