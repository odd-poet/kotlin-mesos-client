package net.oddpoet.mesos.http.dto

/**
 * Describes possible task states. IMPORTANT: Mesos assumes tasks that
 * enter terminal states (see below) imply the task is no longer
 * running and thus clean up any thing associated with the task
 * (ultimately offering any resources being consumed by that task to
 * another task).
 */
enum class TaskState {

    TASK_STAGING,  // Initial state. Framework status updates should not use.
    TASK_STARTING, // The task is being launched by the executor.
    TASK_RUNNING,

    // NOTE: This should only be sent when the framework has
    // the TASK_KILLING_STATE capability.
    TASK_KILLING,  // The task is being killed by the executor.

    // The task finished successfully on its own without external interference.
    TASK_FINISHED, // TERMINAL.

    TASK_FAILED,   // TERMINAL: The task failed to finish successfully.
    TASK_KILLED,   // TERMINAL: The task was killed by the executor.
    TASK_ERROR,    // TERMINAL: The task description contains an error.

    // In Mesos 1.3, this will only be sent when the framework does NOT
    // opt-in to the PARTITION_AWARE capability.
    //
    // NOTE: This state is not always terminal. For example, tasks might
    // transition from TASK_LOST to TASK_RUNNING or other states when a
    // partitioned agent reregisters.
    TASK_LOST,     // The task failed but can be rescheduled.

    // The following task states are only sent when the framework
    // opts-in to the PARTITION_AWARE capability.

    // The task failed to launch because of a transient error. The
    // task's executor never started running. Unlike TASK_ERROR, the
    // task description is valid -- attempting to launch the task again
    // may be successful.
    TASK_DROPPED,  // TERMINAL.

    // The task was running on an agent that has lost contact with the
    // master, typically due to a network failure or partition. The task
    // may or may not still be running.
    TASK_UNREACHABLE,

    // The task is no longer running. This can occur if the agent has
    // been terminated along with all of its tasks (e.g., the host that
    // was running the agent was rebooted). It might also occur if the
    // task was terminated due to an agent or containerizer error, or if
    // the task was preempted by the QoS controller in an
    // oversubscription scenario.
    TASK_GONE,    // TERMINAL.

    // The task was running on an agent that the master cannot contact;
    // the operator has asserted that the agent has been shutdown, but
    // this has not been directly confirmed by the master. If the
    // operator is correct, the task is not running and this is a
    // terminal state; if the operator is mistaken, the task may still
    // be running and might return to RUNNING in the future.
    TASK_GONE_BY_OPERATOR,

    // The master has no knowledge of the task. This is typically
    // because either (a) the master never had knowledge of the task, or
    // (b) the master forgot about the task because it garbage collected
    // its metadata about the task. The task may or may not still be
    // running.
    TASK_UNKNOWN,
}
