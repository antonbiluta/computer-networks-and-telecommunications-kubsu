package main.kotlin.ru.biluta.task2

data class Event(

    val type: EventType,
    val time: Double,
    val task: Task,
    val coreIndex: Int?

): Comparable<Event> {

    override fun compareTo(other: Event): Int {
        return time.compareTo(other.time)
    }

}
