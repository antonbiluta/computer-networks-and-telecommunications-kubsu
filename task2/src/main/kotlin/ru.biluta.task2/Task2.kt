package ru.biluta.task2

import org.apache.commons.math3.distribution.GammaDistribution
import org.apache.commons.math3.distribution.RealDistribution
import org.apache.commons.math3.distribution.WeibullDistribution
import java.util.LinkedList
import java.util.TreeMap
import kotlin.math.sqrt
import kotlin.random.Random

fun main() {
    // Варианты исполнения/завершения задания: k – равномерно распределенная на [1, d] случаная величина
    // G2  Две параллельные связанные цепи 2xPn . Скрещивающиеся связи. Вершины Vi первой
    // цепи связаны ребрами с вершинами Ui-1 и Ui+1 второй цепи, и, аналогично, вершины Ui
    // второй цепи связаны ребрами с вершинами Vi-1 и Vi+1 первой цепи.
    // CA8 Переход к соседнему узлу с минимальным значением Idle(u).

    // A(x): E3
    // B(x): Rayleigh
    // Ядер: 1
    // Условия окончания моделирования: K
    // Размер буффера: 3
    // Дисциплина буффера: FIFO
    // Примечание: a > 0
    val sim = Simulation(100, 100, 3, 0.1, 0.5)
    sim.run()
}

class Simulation(
    private val N: Int,
    private val maxK: Int,
    private val maxQueueSize: Int,
    sigmaMean: Double,
    tauMean: Double,
) {
    private var servers = listOf<Server>()
    private val pool: TreeMap<Double, Event> = TreeMap<Double, Event>()
    // Распределение Релея - это частный случай распределения Вейбулла, где K = 2, lambda = s * sqrt(2)
    // где в свою очередь s = sigmaMean / sqrt(PI / 2)
    private val sigmaDistribution = WeibullDistribution(2.0, sigmaMean / sqrt(Math.PI / 2) * sqrt(2.0))
    private val tauDistribution = GammaDistribution(3.0, tauMean / 3.0)
    private val remainingComputationsDistribution = { 1 + Random.nextInt(N) }
    private var t: Double = 0.0
    private var k: Int = 0

    fun run() {
        // создаем серверы, указываем связи и инициируем поток внутренних событий
        init()
        do {
            // вытаскиваем из пула событий самое раннее
            val entry = pool.pollFirstEntry()
            t = entry.key
            val event = entry.value
            val server = event.server
            // исполняем на сервере обработку события
            when (event.type) {
                EventType.SUBMIT -> server.onSubmitEvent(t, event)
                EventType.FINISH -> {
                    // в случае полной обработки задачи инкрементируем счетчик
                    if (server.onFinishEvent(t, event)) {
                        k++
                    }
                }
            }
        } while (k < maxK)
    }

    private fun init() {
        val neighboursByIndex = mutableMapOf<Int, List<Server>>()
        // создаем 2*N серверов
        servers = List(2 * N) {
            Server(
                it,
                pool,
                maxQueueSize,
                sigmaDistribution,
                tauDistribution,
                remainingComputationsDistribution,
                neighboursByIndex
            )
        }
        // связываем цепи серверов
        for (i in 0 until N) {
            val neighbours = mutableListOf<Server>()
            if (i - 1 >= 0) {
                // между цепями
                neighbours.add(servers[N + i - 1])
                // в одной цепи
                neighbours.add(servers[i - 1])
            }
            if (i + 1 < N) {
                // между цепями
                neighbours.add(servers[N + i + 1])
                // в одной цепи
                neighbours.add(servers[i + 1])
            }
            neighboursByIndex[i] = neighbours
        }
        for (i in 0 until N) {
            val neighbours = mutableListOf<Server>()
            if (i - 1 >= 0) {
                // между цепями
                neighbours.add(servers[i - 1])
                // в одной цепи
                neighbours.add(servers[N + i - 1])
            }
            if (i + 1 < N) {
                // между цепями
                neighbours.add(servers[i + 1])
                // в одной цепи
                neighbours.add(servers[N + i + 1])
            }
            neighboursByIndex[N + i] = neighbours
        }
        // инициируем внутренний поток событий для каждого сервера
        for (server in servers) {
            val submitEvent = Event(
                tauDistribution.sample(),
                type = EventType.SUBMIT,
                server = server,
                remainingComputations = remainingComputationsDistribution.invoke(),
                sigma = sigmaDistribution.sample(),
                internal = true
            )
            pool[submitEvent.timestamp] = submitEvent
        }
    }
}

class Server(
    private val index: Int,
    private val pool: MutableMap<Double, Event>,
    private val maxQueueSize: Int,
    private val sigmaDistribution: RealDistribution,
    private val tauDistribution: RealDistribution,
    private val remainingComputationsDistribution: () -> Int,
    private val neighboursByIndex: Map<Int, List<Server>>
) {
    // общее число задач переданных серверу
    var submittedEvents = 0
    // число отклоненных сервером задач
    var rejectedEvents = 0
    // длительности периодов простоя сервера
    val idleDurations = mutableListOf<Double>()
    private val queue: LinkedList<Event> = LinkedList()
    private var coreUsed: Boolean = false
    // сумма времени простоя ядра
    private var idleSum: Double = 0.0
    // временная метка восвобождения ядра
    private var coreIdleSince: Double = 0.0

    // обработчик событий прихода задачи
    fun onSubmitEvent(t: Double, event: Event) {
        submittedEvents++
        if (coreUsed) {
            if (queue.size < maxQueueSize) {
                // если в очереди есть место, то кладем в неё
                queue.addLast(event)
            } else {
                // если в очереди нет места, то отклоняем задачу
                rejectedEvents++
            }
        } else {
            // записываем длительность периода простоя ядра
            idleDurations.add(t - coreIdleSince)
            // суммируем длительность простоя ядра
            idleSum += (t - coreIdleSince)
            // планируем событие завершения выполнения задачи
            val finishEvent = Event(
                timestamp = t + event.sigma,
                type = EventType.FINISH,
                server = this,
                remainingComputations = event.remainingComputations,
                sigma = event.sigma,
                internal = event.internal
            )
            pool[finishEvent.timestamp] = finishEvent
            // помечаем ядра используемым
            coreUsed = true
        }
        if (event.internal) {
            // если событие было внутренним, то необходимо запланировать следующее через тау времени
            val nextSubmitEvent = Event(
                timestamp = t + tauDistribution.sample(),
                type = EventType.SUBMIT,
                server = this,
                remainingComputations = remainingComputationsDistribution.invoke(),
                sigma = sigmaDistribution.sample(),
                internal = true
            )
            pool[nextSubmitEvent.timestamp] = nextSubmitEvent
        }
    }

    // обработчик событий завершения задачи
    fun onFinishEvent(t: Double, event: Event): Boolean {
        if (queue.isNotEmpty()) {
            // если очередь задач не пуста, то отправляем оттуда задачу на исполнение
            val queueEvent = queue.pollFirst()
            // планируем событие завершения исполнения задачи
            val nextFinishEvent = Event(
                timestamp = t + queueEvent.sigma,
                type = EventType.FINISH,
                server = this,
                remainingComputations = queueEvent.remainingComputations,
                sigma = queueEvent.sigma,
                internal = queueEvent.internal
            )
            pool[nextFinishEvent.timestamp] = nextFinishEvent
        } else {
            // если очередь пуста, то помечаем ядро свободным
            coreUsed = false
            // записываем временную метку высвобождения ядра
            coreIdleSince = t
        }
        if (event.remainingComputations > 0) {
            // если задача не прошла через заданное кол-во серверов, то она отправляется на следующий
            val nextSubmitEvent = Event(
                timestamp = t,
                type = EventType.SUBMIT,
                // выбираем соседа с наибольшим суммарным временем простоя
                server = neighboursByIndex[index]!!.maxByOrNull { it.idleSum }!!,
                // декрементируем счетчик оставшихся исполнений
                remainingComputations = event.remainingComputations - 1,
                sigma = event.sigma,
                internal = false
            )
            pool[nextSubmitEvent.timestamp] = nextSubmitEvent
        }
        // если задача полностью завершена, то возвращаем true
        return event.remainingComputations == 0
    }

    override fun toString(): String = "$index $idleSum"
}

class Event(
    val timestamp: Double,
    val type: EventType,
    val server: Server,
    val sigma: Double,
    val remainingComputations: Int,
    val internal: Boolean
) {
    override fun toString(): String = "$timestamp $type $remainingComputations"
}

enum class EventType {
    SUBMIT,
    FINISH
}