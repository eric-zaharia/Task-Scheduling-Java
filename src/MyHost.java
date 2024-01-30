/* Implement this class. */

import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class MyHost extends Host {

    private final PriorityBlockingQueue<Task> tasks;
    private volatile AtomicBoolean shutdown;
    private volatile Task executingTask;

    public MyHost() {
        this.tasks = new PriorityBlockingQueue<>(100, Comparator
                .comparing(Task::getPriority).reversed()
                .thenComparing(Task::getStart));
        shutdown = new AtomicBoolean(false);
    }

    @Override
    public void run() {
        while (true) {
            try {
                executingTask = tasks.take();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if (shutdown.get()) {
                break;
            }

            long rem = executingTask.getLeft();
            double initialTime = Timer.getTimeDouble();
            boolean preempted = false;

            double currentTime = Timer.getTimeDouble();
            while ((currentTime - initialTime) * 1000 <= (double) rem) {
                executingTask.setLeft(rem - (long) ((Timer.getTimeDouble() - initialTime) * 1000));
                if (executingTask.getLeft() <= 0) {
                    executingTask.setLeft(0);
                    break;
                }

                if (executingTask.isPreemptible() && tasks.peek() != null
                        && tasks.peek().getPriority() > executingTask.getPriority()) {
                        preempted = true;
                        break;
                }

                currentTime = Timer.getTimeDouble();
            }

            if (preempted) {
                tasks.put(executingTask);
            } else {
                executingTask.finish();
            }

            executingTask = null;
        }
    }

    @Override
    public void addTask(Task task) {
        this.tasks.put(task);
    }

    @Override
    public synchronized int getQueueSize() {
        return tasks.size() + ((executingTask != null) ? 1 : 0);
    }

    @Override
    public synchronized long getWorkLeft() {
        return tasks.stream()
                .map(Task::getLeft)
                .reduce(0L, Long::sum)
                + ((executingTask != null) ?
                executingTask.getLeft() : 0L);
    }

    @Override
    public synchronized void shutdown() {
        shutdown.set(true);
        tasks.put(new Task(-1, 10, -1, TaskType.SHORT, -1, false));
        try {
            this.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
