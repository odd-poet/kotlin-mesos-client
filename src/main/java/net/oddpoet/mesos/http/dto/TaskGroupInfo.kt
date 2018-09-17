package net.oddpoet.mesos.http.dto

/**
 * Describes a group of tasks that belong to an executor. The
 * executor will receive the task group in a single message to
 * allow the group to be launched "atomically".
 *
 * NOTES:
 * 1) `NetworkInfo` must not be set inside task's `ContainerInfo`.
 * 2) `TaskInfo.executor` doesn't need to set. If set, it should match
 *    `LaunchGroup.executor`.
 */
data class TaskGroupInfo(val tasks: List<TaskInfo>?)
