package net.oddpoet.mesos.http.dto

import kotlin.reflect.jvm.internal.impl.protobuf.ByteString

/**
 * Describes the current status of a task.
 */
data class TaskStatus(
        val taskId: TaskID,
        val state: TaskState,
        val message: String?,
        val source: Source?,
        val reason: Reason?,
        val data: ByteString?,
        val agentId: AgentID?,
        val executorId: ExecutorID?,
        val timestamp: Double?,
        /**
         * Statuses that are delivered reliably to the scheduler will
         * include a 'uuid'. The status is considered delivered once
         * it is acknowledged by the scheduler. Schedulers can choose
         * to either explicitly acknowledge statuses or let the scheduler
         * driver implicitly acknowledge (default).

         * TODO(bmahler): This is currently overwritten in the scheduler
         * driver and executor driver, but executors will need to set this
         * to a valid RFC-4122 UUID if using the HTTP API.
         */
        val uuid: ByteString?,
        /**
         * Describes whether the task has been determined to be healthy (true) or
         * unhealthy (false) according to the `health_check` field in `TaskInfo`.
         */
        val healthy: Boolean?,
        /**
         * Contains check status for the check specified in the corresponding
         * `TaskInfo`. If no check has been specified, this field must be
         * absent, otherwise it must be present even if the check status is
         * not available yet. If the status update is triggered for a different
         * reason than `REASON_TASK_CHECK_STATUS_UPDATED`, this field will contain
         * the last known value.
         *
         * NOTE: A check-related task status update is triggered if and only if
         * the value or presence of any field in `CheckStatusInfo` changes.
         *
         * NOTE: Check support in built-in executors is experimental.
         */
        val checkStatus: CheckStatusInfo?,
        /**
         * Labels are free-form key value pairs which are exposed through
         * master and agent endpoints. Labels will not be interpreted or
         * acted upon by Mesos itself. As opposed to the data field, labels
         * will be kept in memory on master and agent processes. Therefore,
         * labels should be used to tag TaskStatus message with light-weight
         * meta-data. Labels should not contain duplicate key-value pairs.
         */
        val labels: Labels?,
        /**
         * Container related information that is resolved dynamically such as
         * network address.
         */
        val containerStatus: ContainerStatus?,
        /**
         * The time (according to the master's clock) when the agent where
         * this task was running became unreachable. This is only set on
         * status updates for tasks running on agents that are unreachable
         * (e.g., partitioned away from the master).
         */
        val unreachableTime: TimeInfo,
        /**
         * If the reason field indicates a container resource limitation,
         * this field optionally contains additional information.
         */
        val limitation: TaskResourceLimitation?) {

    /**
     * Describes the source of the task status update.
     */
    enum class Source {
        SOURCE_MASTER,
        SOURCE_AGENT,
        SOURCE_EXECUTOR
    }

    /**
     * Detailed reason for the task status update.
     * Refer to docs/task-state-reasons.md for additional explanation.
     */
    enum class Reason {
        REASON_COMMAND_EXECUTOR_FAILED,

        REASON_CONTAINER_LAUNCH_FAILED,
        REASON_CONTAINER_LIMITATION,
        REASON_CONTAINER_LIMITATION_DISK,
        REASON_CONTAINER_LIMITATION_MEMORY,
        REASON_CONTAINER_PREEMPTED,
        REASON_CONTAINER_UPDATE_FAILED,
        REASON_MAX_COMPLETION_TIME_REACHED,
        REASON_EXECUTOR_REGISTRATION_TIMEOUT,
        REASON_EXECUTOR_REREGISTRATION_TIMEOUT,
        REASON_EXECUTOR_TERMINATED,
        REASON_EXECUTOR_UNREGISTERED,
        REASON_FRAMEWORK_REMOVED,
        REASON_GC_ERROR,
        REASON_INVALID_FRAMEWORKID,
        REASON_INVALID_OFFERS,
        REASON_IO_SWITCHBOARD_EXITED,
        REASON_MASTER_DISCONNECTED,
        REASON_RECONCILIATION,
        REASON_RESOURCES_UNKNOWN,
        REASON_AGENT_DISCONNECTED,
        REASON_AGENT_REMOVED,
        REASON_AGENT_REMOVED_BY_OPERATOR,
        REASON_AGENT_REREGISTERED,
        REASON_AGENT_RESTARTED,
        REASON_AGENT_UNKNOWN,
        REASON_TASK_KILLED_DURING_LAUNCH,
        REASON_TASK_CHECK_STATUS_UPDATED,
        REASON_TASK_HEALTH_CHECK_STATUS_UPDATED,
        REASON_TASK_GROUP_INVALID,
        REASON_TASK_GROUP_UNAUTHORIZED,
        REASON_TASK_INVALID,
        REASON_TASK_UNAUTHORIZED,
        REASON_TASK_UNKNOWN,
    }
}
