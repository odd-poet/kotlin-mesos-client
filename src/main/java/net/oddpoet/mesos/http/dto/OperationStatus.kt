package net.oddpoet.mesos.http.dto

/**
 * Describes the current status of an operation.
 */
data class OperationStatus(
        /**
         * While frameworks will only receive status updates for operations on which
         * they have set an ID, this field is optional because this message is also
         * used internally by Mesos components when the operation's ID has not been
         * set.
         */
        val operationId: OperationID?,
        val state: OperationState,
        val message: String?,
        /**
         * Converted resources after applying the operation. This only
         * applies if the `state` is `OPERATION_FINISHED`.
         */
        val convertedResources: List<Resource>,
        /**
         * Statuses that are delivered reliably to the scheduler will
         * include a `uuid`. The status is considered delivered once
         * it is acknowledged by the scheduler.
         */
        val uuid: UUID?)

