package net.oddpoet.mesos.http.dto

import kotlin.reflect.jvm.internal.impl.protobuf.ByteString

/**
 * Describes a task. Passed from the scheduler all the way to an
 * executor (see SchedulerDriver::launchTasks and
 * Executor::launchTask). Either ExecutorInfo or CommandInfo should be set.
 * A different executor can be used to launch this task, and subsequent tasks
 * meant for the same executor can reuse the same ExecutorInfo struct.
 */
data class TaskInfo(
        val name: String,
        val taskId: TaskID,
        val agentId: AgentID,
        val resources: List<Resource>?,
        val executor: ExecutorInfo?,
        val command: CommandInfo?,
        /**
         * Task provided with a container will launch the container as part
         * of this task paired with the task's CommandInfo.
         */
        val container: ContainerInfo?,
        /**
         * A health check for the task. Implemented for executor-less
         * command-based tasks. For tasks that specify an executor, it is
         * the executor's responsibility to implement the health checking.
         */
        val healthCheck: HealthCheck?,
        /**
         * A general check for the task. Implemented for all built-in executors.
         * For tasks that specify an executor, it is the executor's responsibility
         * to implement checking support. Executors should (all built-in executors
         * will) neither interpret nor act on the check's result.
         *
         * NOTE: Check support in built-in executors is experimental.
         *
         * TODO(alexr): Consider supporting multiple checks per task.
         */
        val check: CheckInfo?,
        /**
         * A kill policy for the task. Implemented for executor-less
         * command-based and docker tasks. For tasks that specify an
         * executor, it is the executor's responsibility to implement
         * the kill policy.
         */
        val killPolicy: KillPolicy?,
        val data: ByteString?,
        /**
         * Labels are free-form key value pairs which are exposed through
         * master and agent endpoints. Labels will not be interpreted or
         * acted upon by Mesos itself. As opposed to the data field, labels
         * will be kept in memory on master and agent processes. Therefore,
         * labels should be used to tag tasks with light-weight meta-data.
         * Labels should not contain duplicate key-value pairs.
         */
        val labels: Labels?,
        /**
         * Service discovery information for the task. It is not interpreted
         * or acted upon by Mesos. It is up to a service discovery system
         * to use this information as needed and to handle tasks without
         * service discovery information.
         */
        val discovery: DiscoveryInfo?,
        /**
         * Maximum duration for task completion. If the task is non-terminal at the
         * end of this duration, it will fail with the reason
         * `REASON_MAX_COMPLETION_TIME_REACHED`. Mesos supports this field for
         * executor-less tasks, and tasks that use Docker or default executors.
         * It is the executor's responsibility to implement this, so it might not be
         * supported by all custom executors.
         */
        val maxCompletionTime: DurationInfo?)
