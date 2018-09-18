package net.oddpoet.mesos.http.dto

/**
 * Synchronous responses for calls made to the scheduler API.
 */
data class Response(
        val type: Type?,
        val reconcileOperations: ReconcileOperations?) {

    /**
     * ach of the responses of type `FOO` corresponds to `Foo` message below.
     */
    enum class Type {
        UNKNOWN,
        RECONCILE_OPERATIONS
    }

    data class ReconcileOperations(val operationStatuses: List<OperationStatus>?)
}