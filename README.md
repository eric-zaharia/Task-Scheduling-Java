Within the scope of this project, our objective is to simulate a specific system architecture comprising two central elements: 
the dispatcher, or load balancer (depicted in green), and the nodes (indicated in blue). The dispatcher functions as a task coordinator,
receiving incoming tasks from different sources—such as clients and the datacenter—and then strategically dispatching them to the nodes
within the datacenter based on predefined policies.

The nodes, on the other hand, serve as the execution units within the system. Marked in blue, these nodes are tasked with carrying out
the assigned operations. Each node has the ability to manage tasks based on priority, preempting those currently in progress for more 
critical assignments. Additionally, every node maintains a queue (depicted in the image as s, i) where tasks awaiting execution are stored.

The crux of this project lies in the implementation of the logic governing both the dispatcher and the computing nodes. This entails 
developing the algorithms and functionalities that enable the dispatcher to intelligently distribute tasks while empowering the computing 
nodes to execute tasks efficiently, manage priorities, and handle task queues effectively. The ultimate goal is to create a well-coordinated
and optimized system that enhances the overall performance and responsiveness of the given architecture.


Planning policies
The Dispatcher can work with one of four scheduling policies: Round Robin, Shortest Queue, Size Interval Task Assignment, Least Work Left.

-> Round Robin (RR)
In this scheduling policy, tasks are assigned, as they arrive, to node (i + 1) % n, where i is
ID of the last node to which a task was assigned, and n is the total number of nodes. It always starts
with ID 0.

-> Shortest Queue (SQ)
At Shortest Queue, the dispatcher assigns a task to that node that has the queue of tasks waiting and executing
of minimum size. To calculate the minimum queue, the tasks already running are also taken into account
at a node. Thus, if we have two nodes, one with a task in execution and none in the waiting queue, and
another with no task in execution, ie or in ace, waiting, the dispatcher will send a new task to the second node. If
there are several nodes with the same queue size, a task will be sent to the node with the smaller ID.

-> Size Interval Task Assignment (SITA)
Within this scheduling policy, there is a fixed number of three nodes, and the tasks are grouped into three
categories: short, medium, long. Each of the three nodes is specific to a type of task (in order, from the smallest
to high), so the tasks are assigned to the corresponding node as they arrive in the system. E.g,
if a short task arrives in the system, it will be assigned to node 0.

-> Least Work Left (LWL)
The Least Work Left policy is similar to SQ, with the difference that it no longer takes into account the size of queues
at each node, but of the total duration of calculations left to be executed. The amount of calculations refers to time
left to be processed at each node, which can be approximated to the granularity of seconds (in other words, if
one queue has 2.9 seconds to work and another 2.95, it is considered that the two queues are equal and the node is chosen
with the smaller ID).

The most important classes are the following:

The MyDispatcher class
    I implemented the addTask method in this class, to send each host tasks depending on
    selected algorithm. I added the lastHost class variable and instantiated it in the constructor with
    the number of hosts - 1, using it for the Round Robin algorithm.
    Otherwise, I just check the algorithm used and add it to the hosts' tasks.

MyHost class
    Here I applied the template of the Producer-Consumer problem and used the PriorityBlockingQueue structure because it helps me
    and for task priorities. I also use a shutdown variable, which announces the end of the execution
    host and executingTask, where we save the task that is being executed at the current time.
    In the run method, I extract the task with the highest priority and begin to decrease its remaining time, thus simulating
    its execution, until it ends or is preempted. If it was preempted, I add it back to the queue, otherwise
    I call its finish method. If the shutdown variable is set to true, we exit the loop.
    In the shutdown method, I set the shutdown variable to true, then add a dummy element to the queue (I simulate a
    release on a semaphore that unlocks the thread running run), then I will join the thread.
    In getQueueSize I calculate the length of the queue plus the running task, and in getWorkLeft I collect all the "work"
    left on each task, including the one in execution
