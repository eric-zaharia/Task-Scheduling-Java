/* Implement this class. */

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class MyDispatcher extends Dispatcher {
    private volatile int lastHost;
    public MyDispatcher(SchedulingAlgorithm algorithm, List<Host> hosts) {
        super(algorithm, hosts);
        lastHost = hosts.size() - 1;
    }

    @Override
    public synchronized void addTask(Task task) {
        SchedulingAlgorithm algorithm = super.algorithm;
        switch(algorithm) {
            case ROUND_ROBIN -> {
                Host pickedHost = super.hosts.get((lastHost + 1) % super.hosts.size());
                lastHost += 1;
                pickedHost.addTask(task);
            }

            case SHORTEST_QUEUE -> {
                Optional<Host> pickedHost = super.hosts.stream()
                        .min(Comparator.comparingLong(Host::getQueueSize));

                pickedHost.ifPresent(host -> host.addTask(task));
            }

            case SIZE_INTERVAL_TASK_ASSIGNMENT -> {
                Host pickedHost;
                TaskType type = task.getType();

                switch(type) {
                    case SHORT -> {
                        pickedHost = super.hosts.get(0);
                    }

                    case MEDIUM -> {
                        pickedHost = super.hosts.get(1);
                    }

                    default -> {
                        pickedHost = super.hosts.get(2);
                    }
                }

                pickedHost.addTask(task);
            }

            case LEAST_WORK_LEFT -> {
                Optional<Host> pickedHost = super.hosts.stream()
                        .min(Comparator.comparingLong(Host::getWorkLeft));

                pickedHost.ifPresent(host -> host.addTask(task));
            }
        }
    }
}
