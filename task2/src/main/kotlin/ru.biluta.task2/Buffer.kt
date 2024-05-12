package main.kotlin.ru.biluta.task2

import java.util.*

class Buffer(
    private val maxSize: Int,
    private val queue: Queue<Task> = LinkedList()
) {

    fun addTask(task: Task): Boolean {
        if (queue.size < maxSize) {
            queue.add(task)
            return true
        }
        return false
    }

    fun getNextTask(): Task {
        return queue.poll()
    }

    fun isEmpty(): Boolean {
        return queue.isEmpty()
    }

    fun getCurrentSize(): Int {
        return queue.size
    }

}