package ru.biluta.task1.system;

import ru.biluta.task1.model.Task;

import java.util.LinkedList;
import java.util.Queue;

public class Buffer {
    private final Queue<Task> queue;
    private final int maxSize;

    public Buffer(int maxSize) {
        this.queue = new LinkedList<>();
        this.maxSize = maxSize;
    }

    public boolean addTask(Task task) {
        if (queue.size() < maxSize) {
            queue.add(task);
            return true;
        }
        return false;
    }

    public Task getNextTask() {
        return queue.poll();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public int getCurrentSize() {
        return queue.size();
    }
}
