package net.oddpoet.mesos.http.dto

/**
 * Describes some resources available on an agent. An offer only
 * contains resources from a single agent.
 *
 * ref: https://github.com/apache/mesos/blob/master/include/mesos/v1/mesos.proto#L1872
 */
data class Offer(
        val id: OfferID,
        val frameworkId: FrameworkID,
        val agentId: AgentID,
        val hostname: String,

        /**
         * URL for reaching the agent running on the host.
         */
        val url: URL?,
        /**
         * The domain of the agent.
         */
        val domain: DomainInfo?,

        val resources: List<Resource>?,
        val attributes: List<Attribute>?,
        /**
         * Executors of the same framework running on this agent.
         */
        val executorIds: List<ExecutorID>?,
        /**
         * Signifies that the resources in this Offer may be unavailable during
         * the given interval.  Any tasks launched using these resources may be
         * killed when the interval arrives.  For example, these resources may be
         * part of a planned maintenance schedule.
         *
         * This field only provides information about a planned unavailability.
         * The unavailability interval may not necessarily start at exactly this
         * interval, nor last for exactly the duration of this interval.
         * The unavailability may also be forever!  See comments in
         * `Unavailability` for more details.
         */
        val unavailability: Unavailability?,
        /**
         * An offer represents resources allocated to *one* of the
         * roles managed by the scheduler. (Therefore, each
         * `Offer.resources[i].allocation_info` will match the
         * top level `Offer.allocation_info`).
         */
        val allocationInfo: Resource.AllocationInfo?) {

    /**
     * Defines an operation that can be performed against offers.
     */
    data class Operation(
            /**
             * NOTE: The `id` field will allow frameworks to indicate that they wish to
             * receive feedback about an operation. Since this feature is not yet
             * implemented, the `id` field should NOT be set at present. See MESOS-8054.
             */
            val type: Type?,
            val id: OperationID?,       // EXPERIMENTAL.
            val launch: Launch?,
            val launchGroup: LaunchGroup?,
            val unreserve: Unreserve?,
            val create: Create?,
            val destory: Destroy?,
            val growVolume: GrowVolume?,        // EXPERIMENTAL.
            val shrinkVolume: ShrinkVolume?,    // EXPERIMENTAL.
            val createDisk: CreateDisk?,        // EXPERIMENTAL.
            val destoryDisk: DestroyDisk?       // EXPERIMENTAL.
    ) {
        enum class Type {
            UNKNOWN,
            LAUNCH,
            LAUNCH_GROUP,
            RESERVE,
            UNRESERVE,
            CREATE,
            DESTROY,
            GROW_VOLUME,        // EXPERIMENTAL
            SHRINK_VOLUME,      // EXPERIMENTAL
            CREATE_DISK,        // EXPERIMENTAL
            DESTORY_DISK        // EXPERIMENTAL
        }

        // TODO(vinod): Deprecate this in favor of `LaunchGroup` below.
        data class Launch(val taskInfos: List<TaskInfo>)

        /**
         * Unlike `Launch` above, all the tasks in a `task_group` are
         * atomically delivered to an executor.
         *
         * `NetworkInfo` set on executor will be shared by all tasks in
         * the task group.
         *
         * TODO(vinod): Any volumes set on executor could be used by a
         * task by explicitly setting `Volume.source` in its resources.
         */
        data class LaunchGroup(
                val executor: ExecutorInfo,
                val taskGroup: TaskGroupInfo)

        data class Reserve(val resources: List<Resource>)

        data class Unreserve(val resource: List<Resource>)

        data class Create(val volumes: List<Resource>)

        data class Destroy(val volumes: List<Resource>)

        /**
         * Grow a volume by an additional disk resource.
         * NOTE: This is currently experimental and only for persistent volumes
         * created on ROOT/PATH disk.
         */
        data class GrowVolume(
                val volume: Resource,
                val addition: Resource)

        /**
         * Shrink a volume by the size specified in the `subtract` field.
         * NOTE: This is currently experimental and only for persistent volumes
         * created on ROOT/PATH disk.
         */
        data class ShrinkVolume(
                val volume: Resource,
                /**
                 * See comments in [Value.Scalar] for maximum precision supported.
                 */
                val substract: Value.Scalar)

        /**
         * Create a `MOUNT` or `BLOCK` disk resource from a `RAW` disk resource.
         * NOTE: For the time being, this API is subject to change and the related
         * feature is experimental.
         */
        data class CreateDisk(
                val source: Resource,
                // NOTE: Only `MOUNT` or `BLOCK` is allowed in the `target_type` field.
                val targetType: Resource.DiskInfo.Source.Type)

        /**
         * Destroy a `MOUNT` or `BLOCK` disk resource. This will result in a `RAW`
         * disk resource.
         * NOTE: For the time being, this API is subject to change and the related
         * feature is experimental.
         */
        data class DestroyDisk(
                // NOTE: Only a `MOUNT` or `BLOCK` disk is allowed in the `source` field.
                val source: Resource)

    }
}

