package main.kotlin.ru.biluta.task2

import java.util.PriorityQueue

class EventDispatcher(
    val sigma: Double,
    val lambda: Double,
    val K: Int,
    val eventQueue: PriorityQueue<Event> = PriorityQueue(),
    val generators: RandomGenerators = RandomGenerators(sigma, lambda, K),
) {

    fun isQueueEmpty() = eventQueue.isEmpty()

    fun getFirst() = eventQueue.poll()

    fun createArrivalEvent(
        task: Task,
        coreIndex: Int,
        currentTime: Double
    ) {
        val nextArrivalTime = currentTime + generators.generateErlang();
        val arrivalEvent = Event(EventType.TASK_ARRIVAL, nextArrivalTime, task, coreIndex)
        eventQueue.add(arrivalEvent)
    }

    fun createCompletionEvent(
        task: Task,
        coreIndex: Int,
        currentTime: Double
    ) {
        val completionTime = currentTime + task.processingTime
        val completionEvent = Event(EventType.TASK_COMPLETION, completionTime, task, coreIndex)
        eventQueue.add(completionEvent)
    }

}