package net.oddpoet.mesos.http.dto

/**
 * A framework-generated ID to distinguish a task. The ID must remain
 * unique while the task is active. A framework can reuse an ID _only_
 * if the previous task with the same ID has reached a terminal state
 * (e.g., TASK_FINISHED, TASK_KILLED, etc.). However, reusing task IDs
 * is strongly discouraged (MESOS-2198).
 */
data class TaskID(val value: String)
