package net.oddpoet.mesos.http.dto

/**
 * A framework-generated ID to distinguish an executor. Only one
 * executor with the same ID can be active on the same agent at a
 * time. However, reusing executor IDs is discouraged.
 */
data class ExecutorID(val value: String)