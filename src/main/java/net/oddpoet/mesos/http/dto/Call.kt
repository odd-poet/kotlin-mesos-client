package net.oddpoet.mesos.http.dto

import java.util.*

/**
 * Scheduler call API.
 *
 * Like Event, a Call is described using the standard protocol buffer
 * "union" trick (see above).
 */
data class Call(
        /**
         * Identifies who generated this call. Master assigns a framework id
         * when a new scheduler subscribes for the first time. Once assigned,
         * the scheduler must set the 'framework_id' here and within its
         * FrameworkInfo (in any further 'Subscribe' calls). This allows the
         * master to identify a scheduler correctly across disconnections,
         * failovers, etc.
         */
        val frameworkId: FrameworkID?,
        /**
         * Type of the call, indicates which optional field below should be
         * present if that type has a nested message definition.
         * See comments on `Event::Type` above on the reasoning behind this
         * field being optional.
         */
        val type: Type?,
        val subscribe: Subscribe?,
        val accept: Accept?,
        val acceptInverseOffers: AcceptInverseOffers?,
        val declineInverseOffers: DeclineInverseOffers?,
        val revive: Revive?,
        val kill: Kill?,
        val shutdown: Shutdown?,
        val acknowledge: Acknowledge?,
        val acknowledgeOperationStatus: AcknowledgeOperationStatus?,
        val reconcile: Reconcile?,
        val reconcileOperations: ReconcileOperations?,
        val message: Message?,
        val request: Request?,
        val suppress: Suppress?) {

    /**
     * Possible call types, followed by message definitions if applicable.
     */
    enum class Type {

        UNKNOWN,        // See comments above on `Event::Type` for more details on this enum value.

        SUBSCRIBE,   // See 'Subscribe' below.
        TEARDOWN,    // Shuts down all tasks/executors and removes framework.
        ACCEPT,      // See 'Accept' below.
        DECLINE,     // See 'Decline' below.
        ACCEPT_INVERSE_OFFERS,  // See 'AcceptInverseOffers' below.
        DECLINE_INVERSE_OFFERS, // See 'DeclineInverseOffers' below.
        REVIVE,      // Removes any previous filters set via ACCEPT or DECLINE.
        KILL,        // See 'Kill' below.
        SHUTDOWN,    // See 'Shutdown' below.
        ACKNOWLEDGE, // See 'Acknowledge' below.
        ACKNOWLEDGE_OPERATION_STATUS, // See message below.
        RECONCILE,   // See 'Reconcile' below.
        RECONCILE_OPERATIONS, // See 'ReconcileOperations' below.
        MESSAGE,    // See 'Message' below.
        REQUEST,    // See 'Request' below.
        SUPPRESS,   // Inform master to stop sending offers to the framework.
    }

    /**
     * Subscribes the scheduler with the master to receive events. A
     * scheduler must send other calls only after it has received the
     * SUBCRIBED event.
     */
    data class Subscribe(
            /**
             * See the comments below on 'framework_id' on the semantics for
             * 'framework_info.id'.
             */
            val frameworkInfo: FrameworkInfo,
            /**
             * List of suppressed roles for which the framework does not wish to be
             * offered resources. The framework can decide to suppress all or a subset
             * of roles the framework (re)registers as.
             */
            val suppressedRoles: List<String>?)

    /**
     * Accepts an offer, performing the specified operations
     * in a sequential manner.
     *
     * E.g. Launch a task with a newly reserved persistent volume:
     *
     *   Accept {
     *     offer_ids: [ ... ]
     *     operations: [
     *       { type: RESERVE,
     *         reserve: { resources: [ disk(role):2 ] } }
     *       { type: CREATE,
     *         create: { volumes: [ disk(role):1+persistence ] } }
     *       { type: LAUNCH,
     *         launch: { task_infos ... disk(role):1;disk(role):1+persistence } }
     *     ]
     *   }
     *
     * Note that any of the offer’s resources not used in the 'Accept'
     * call (e.g., to launch a task) are considered unused and might be
     * reoffered to other frameworks. In other words, the same OfferID
     * cannot be used in more than one 'Accept' call.
     */
    data class Accept(
            val offerIds: List<OfferID>?,
            val operations: List<Offer.Operation>?,
            val filters: Filters?)

    /**
     * Declines an offer, signaling the master to potentially reoffer
     * the resources to a different framework. Note that this is same
     * as sending an Accept call with no operations. See comments on
     * top of 'Accept' for semantics.
     */
    data class Decline(
            val offerIds: List<OfferID>?,
            val filters: Filters?)

    /**
     * Accepts an inverse offer. Inverse offers should only be accepted
     * if the resources in the offer can be safely evacuated before the
     * provided unavailability.
     */
    data class AcceptInverseOffers(
            val inverseOfferIds: List<OfferID>?,
            val filters: Filters?)

    /**
     * Declines an inverse offer. Inverse offers should be declined if
     * the resources in the offer might not be safely evacuated before
     * the provided unavailability.
     */
    data class DeclineInverseOffers(
            val inverseOfferIds: List<OfferID>?,
            val filters: Filters?)

    /**
     * Revive offers for the specified roles. If `roles` is empty,
     * the `REVIVE` call will revive offers for all of the roles
     * the framework is currently subscribed to.
     */
    data class Revive(val roles: List<String>?)


    /**
     * Kills a specific task. If the scheduler has a custom executor,
     * the kill is forwarded to the executor and it is up to the
     * executor to kill the task and send a TASK_KILLED (or TASK_FAILED)
     * update. Note that Mesos releases the resources for a task once it
     * receives a terminal update (See TaskState in v1/mesos.proto) for
     * it. If the task is unknown to the master, a TASK_LOST update is
     * generated.
     *
     * If a task within a task group is killed before the group is
     * delivered to the executor, all tasks in the task group are
     * killed. When a task group has been delivered to the executor,
     * it is up to the executor to decide how to deal with the kill.
     * Note The default Mesos executor will currently kill all the
     * tasks in the task group if it gets a kill for any task.
     */
    data class Kill(
            val taskId: TaskID,
            val agentId: AgentID?,
            /**
             * If set, overrides any previously specified kill policy for this task.
             * This includes 'TaskInfo.kill_policy' and 'Executor.kill.kill_policy'.
             * Can be used to forcefully kill a task which is already being killed.
             */
            val killPolicy: KillPolicy?)

    /**
     * Shuts down a custom executor. When the executor gets a shutdown
     * event, it is expected to kill all its tasks (and send TASK_KILLED
     * updates) and terminate. If the executor doesn’t terminate within
     * a certain timeout (configurable via
     * '--executor_shutdown_grace_period' agent flag), the agent will
     * forcefully destroy the container (executor and its tasks) and
     * transition its active tasks to TASK_LOST.
     */
    data class Shutdown(
            val executorId: ExecutorID,
            val agentId: AgentID)

    /**
     * Acknowledges the receipt of status update. Schedulers are
     * responsible for explicitly acknowledging the receipt of status
     * updates that have 'Update.status().uuid()' field set. Such status
     * updates are retried by the agent until they are acknowledged by
     * the scheduler.
     */
    data class Acknowledge(
            val agentId: AgentID,
            val taskId: TaskID,
            val uuid: String) {

        val uuidAsBytes: ByteArray
            get() = Base64.getDecoder().decode(uuid)
    }

    /**
     * Acknowledges the receipt of an operation status update. Schedulers
     * are responsible for explicitly acknowledging the receipt of updates
     * which have the 'UpdateOperationStatus.status().uuid()' field set.
     * Such status updates are retried by the agent or resource provider
     * until they are acknowledged by the scheduler.
     */
    data class AcknowledgeOperationStatus(
            val agentId: AgentID?,
            val resourceProviderId: ResourceProviderID?,
            val uuid: String,
            val operationId: OperationID) {
        val uuidAsBytes: ByteArray
            get() = Base64.getDecoder().decode(uuid)
    }

    /**
     * Allows the scheduler to query the status for non-terminal tasks.
     * This causes the master to send back the latest task status for
     * each task in 'tasks', if possible. Tasks that are no longer known
     * will result in a TASK_LOST, TASK_UNKNOWN, or TASK_UNREACHABLE update.
     * If 'tasks' is empty, then the master will send the latest status
     * for each task currently known.
     */
    data class Reconcile(val tasks: List<Task>?) {

        data class Task(
                val taskId: TaskID,
                val agentId: AgentID?)
    }

    /**
     * Allows the scheduler to query the status of operations. This causes
     * the master to send back the latest status for each operation in
     * 'operations', if possible. If 'operations' is empty, then the
     * master will send the latest status for each operation currently
     * known.
     */
    data class ReconcileOperations(
            val operation: List<Operation>?) {
        data class Operation(
                val operationId: OperationID,
                val agentId: AgentID?,
                val resourceProviderId: ResourceProviderID?)
    }

    /**
     * Sends arbitrary binary data to the executor. Note that Mesos
     * neither interprets this data nor makes any guarantees about the
     * delivery of this message to the executor.
     */
    data class Message(
            val agentId: AgentID,
            val executorId: ExecutorID,
            val data: String) {
        val dataAsBytes: ByteArray
            get() = Base64.getDecoder().decode(data)
    }

    /**
     * Requests a specific set of resources from Mesos's allocator. If
     * the allocator has support for this, corresponding offers will be
     * sent asynchronously via the OFFERS event(s).
     *
     * NOTE: The built-in hierarchical allocator doesn't have support
     * for this call and hence simply ignores it.
     */
    data class Request(val requests: List<net.oddpoet.mesos.http.dto.Request>?)

    /**
     * Suppress offers for the specified roles. If `roles` is empty,
     * the `SUPPRESS` call will suppress offers for all of the roles
     * the framework is currently subscribed to.
     */
    data class Suppress(val roles: List<String>?)

}