package net.oddpoet.mesos.http.dto

/**
 * Describes a resource from a resource provider. The `name` field is
 * a string like "cpus" or "mem" that indicates which kind of resource
 * this is; the rest of the fields describe the properties of the
 * resource. A resource can take on one of three types: scalar
 * (double), a list of finite and discrete ranges (e.g., [1-10,
 * 20-30]), or a set of items. A resource is described using the
 * standard protocol buffer "union" trick.
 *
 * Note that "disk" and "mem" resources are scalar values expressed in
 * megabytes. Fractional "cpus" values are allowed (e.g., "0.5"),
 * which correspond to partial shares of a CPU.
 *
 * ref: https://github.com/apache/mesos/blob/master/include/mesos/v1/mesos.proto#L1167
 */
data class Resource(
        val providerId: ResourceProviderID?,
        val name: String,
        val type: Value.Type,
        val scalar: Value.Scalar?,
        val ranges: Value.Ranges?,
        val set: Value.Set?,
        /**
         * The role that this resource is reserved for. If "*", this indicates
         * that the resource is unreserved. Otherwise, the resource will only
         * be offered to frameworks that belong to this role.
         *
         * NOTE: Frameworks must not set this field if `reservations` is set.
         * See the 'Resource Format' section for more details.
         *
         * TODO(mpark): Deprecate once `reservations` is no longer experimental.
         */
        val role: String?,
        val allocationInfo: AllocationInfo?,
        /**
         * If this is set, this resource was dynamically reserved by an
         * operator or a framework. Otherwise, this resource is either unreserved
         * or statically reserved by an operator via the --resources flag.
         *
         * NOTE: Frameworks must not set this field if `reservations` is set.
         * See the 'Resource Format' section for more details.
         *
         * TODO(mpark): Deprecate once `reservations` is no longer experimental.
         */
        val reservation: ReservationInfo?,
        /**
         * The stack of reservations. If this field is empty, it indicates that this
         * resource is unreserved. Otherwise, the resource is reserved. The first
         * `ReservationInfo` may have type `STATIC` or `DYNAMIC`, but the rest must
         * have `DYNAMIC`. One can create a new reservation on top of an existing
         * one by pushing a new `ReservationInfo` to the back. The last
         * `ReservationInfo` in this stack is the "current" reservation. The new
         * reservation's role must be a child of the current reservation's role.
         *
         * NOTE: Frameworks must not set this field if `reservation` is set.
         * See the 'Resource Format' section for more details.
         *
         * TODO(mpark): Deprecate `role` and `reservation` once this is stable.
         */
        val reservations: List<ReservationInfo>?,
        val disk: DiskInfo?,

        /**
         * If this is set, the resources are revocable, i.e., any tasks or
         * executors launched using these resources could get preempted or
         * throttled at any time. This could be used by frameworks to run
         * best effort tasks that do not need strict uptime or performance
         * guarantees. Note that if this is set, 'disk' or 'reservation'
         * cannot be set.
         */
        val revocable: RevocableInfo?,
        /**
         * If this is set, the resources are shared, i.e. multiple tasks
         * can be launched using this resource and all of them shall refer
         * to the same physical resource on the cluster. Note that only
         * persistent volumes can be shared currently.
         */
        val shared: SharedInfo?) {

    /**
     * This was initially introduced to support MULTI_ROLE capable
     * frameworks. Frameworks that are not MULTI_ROLE capable can
     * continue to assume that the offered resources are allocated
     * to their role.
     */
    data class AllocationInfo(
            /**
             * If set, this resource is allocated to a role. Note that in the
             * future, this may be unset and the scheduler may be responsible
             * for allocating to one of its roles.
             */
            val role: String?
            // In the future, we may add additional fields here, e.g. priority
            // tier, type of allocation (quota / fair share).
    )

    /**
     * Resource Format:
     *
     * Frameworks receive resource offers in one of two formats, depending on
     * whether the RESERVATION_REFINEMENT capability is enabled.
     *
     * __WITHOUT__ the RESERVATION_REFINEMENT capability, the framework is offered
     * resources in the "pre-reservation-refinement" format. In this format, the
     * `Resource.role` and `Resource.reservation` fields are used in conjunction
     * to describe the reservation state of a `Resource` message.
     *
     * The following is an overview of the possible reservation states:
     *
     * +------------+------------------------------------------------------------+
     * | unreserved | {                                                          |
     * |            |   role: "*",                                               |
     * |            |   reservation: <not set>,                                  |
     * |            |   reservations: <unused>                                   |
     * |            | }                                                          |
     * +------------+------------------------------------------------------------+
     * | static     | {                                                          |
     * |            |   role: "eng",                                             |
     * |            |   reservation: <not set>,                                  |
     * |            |   reservations: <unused>                                   |
     * |            | }                                                          |
     * +------------+------------------------------------------------------------+
     * | dynamic    | {                                                          |
     * |            |   role: "eng",                                             |
     * |            |   reservation: {                                           |
     * |            |     type: <unused>,                                        |
     * |            |     role: <unused>,                                        |
     * |            |     principal: <optional>,                                 |
     * |            |     labels: <optional>                                     |
     * |            |   },                                                       |
     * |            |   reservations: <unused>                                   |
     * |            | }                                                          |
     * +------------+------------------------------------------------------------+
     *
     * __WITH__ the RESERVATION_REFINEMENT capability, the framework is offered
     * resources in the "post-reservation-refinement" format. In this format, the
     * reservation state of a `Resource` message is expressed solely in
     * `Resource.reservations` field.
     *
     * The following is an overview of the possible reservation states:
     *
     * +------------+------------------------------------------------------------+
     * | unreserved | {                                                          |
     * |            |   role: <unused>,                                          |
     * |            |   reservation: <unused>,                                   |
     * |            |   reservations: []                                         |
     * |            | }                                                          |
     * +------------+------------------------------------------------------------+
     * | static     | {                                                          |
     * |            |   role: <unused>,                                          |
     * |            |   reservation: <unused>,                                   |
     * |            |   reservations: [                                          |
     * |            |     {                                                      |
     * |            |       type: STATIC,                                        |
     * |            |       role: "eng",                                         |
     * |            |       principal: <optional>,                               |
     * |            |       labels: <optional>                                   |
     * |            |     }                                                      |
     * |            |   ]                                                        |
     * |            | }                                                          |
     * +------------+------------------------------------------------------------+
     * | dynamic    | {                                                          |
     * |            |   role: <unused>,                                          |
     * |            |   reservation: <unused>,                                   |
     * |            |   reservations: [                                          |
     * |            |     {                                                      |
     * |            |       type: DYNAMIC,                                       |
     * |            |       role: "eng",                                         |
     * |            |       principal: <optional>,                               |
     * |            |       labels: <optional>                                   |
     * |            |     }                                                      |
     * |            |   ]                                                        |
     * |            | }                                                          |
     * +------------+------------------------------------------------------------+
     *
     * We can also __refine__ reservations with this capability like so:
     *
     * +------------+------------------------------------------------------------+
     * | refined    | {                                                          |
     * |            |   role: <unused>,                                          |
     * |            |   reservation: <unused>,                                   |
     * |            |   reservations: [                                          |
     * |            |     {                                                      |
     * |            |       type: STATIC or DYNAMIC,                             |
     * |            |       role: "eng",                                         |
     * |            |       principal: <optional>,                               |
     * |            |       labels: <optional>                                   |
     * |            |     },                                                     |
     * |            |     {                                                      |
     * |            |       type: DYNAMIC,                                       |
     * |            |       role: "eng/front_end",                               |
     * |            |       principal: <optional>,                               |
     * |            |       labels: <optional>                                   |
     * |            |     }                                                      |
     * |            |   ]                                                        |
     * |            | }                                                          |
     * +------------+------------------------------------------------------------+
     *
     * NOTE: Each `ReservationInfo` in the `reservations` field denotes
     * a reservation that refines the previous `ReservationInfo`.
     *
     * Describes a reservation. A static reservation is set by the operator on
     * the command-line and they are immutable without agent restart. A dynamic
     * reservation is made by an operator via the '/reserve' HTTP endpoint
     * or by a framework via the offer cycle by sending back an
     * 'Offer::Operation::Reserve' message.
     *
     * NOTE: We currently do not allow frameworks with role "*" to make dynamic
     * reservations.
     */
    data class ReservationInfo(
            /**
             * The type of this reservation.
             *
             * NOTE: This field must not be set for `Resource.reservation`.
             * See the 'Resource Format' section for more details.
             */
            val type: Type?,
            /**
             * The role to which this reservation is made for.
             *
             * NOTE: This field must not be set for `Resource.reservation`.
             * See the 'Resource Format' section for more details.
             */
            val role: String,

            /**
             * Indicates the principal, if any, of the framework or operator
             * that reserved this resource. If reserved by a framework, the
             * field should match the `FrameworkInfo.principal`. It is used in
             * conjunction with the `UnreserveResources` ACL to determine
             * whether the entity attempting to unreserve this resource is
             * permitted to do so.
             */
            val principal: String?,
            /**
             * Labels are free-form key value pairs that can be used to
             * associate arbitrary metadata with a reserved resource.  For
             * example, frameworks can use labels to identify the intended
             * purpose for a portion of the resources the framework has
             * reserved at a given agent. Labels should not contain duplicate
             * key-value pairs.
             */
            val labels: Labels?) {

        enum class Type {
            UNKNOWN,
            STATIC,
            DYNAMIC
        }
    }

    data class DiskInfo(
            val persistence: Persistence?,
            /**
             * Describes how this disk resource will be mounted in the
             * container. If not set, the disk resource will be used as the
             * sandbox. Otherwise, it will be mounted according to the
             * 'container_path' inside 'volume'. The 'host_path' inside
             * 'volume' is ignored.
             * NOTE: If 'volume' is set but 'persistence' is not set, the
             * volume will be automatically garbage collected after
             * task/executor terminates. Currently, if 'persistence' is set,
             * 'volume' must be set.
             */
            val volume: Volume?,
            val source: Source?) {
        /**
         * Describes a persistent disk volume.
         *
         * A persistent disk volume will not be automatically garbage
         * collected if the task/executor/agent terminates, but will be
         * re-offered to the framework(s) belonging to the 'role'.
         *
         * NOTE: Currently, we do not allow persistent disk volumes
         * without a reservation (i.e., 'role' cannot be '*').
         */
        data class Persistence(
                /**
                 * A unique ID for the persistent disk volume. This ID must be
                 * unique per role on each agent. Although it is possible to use
                 * the same ID on different agents in the cluster and to reuse
                 * IDs after a volume with that ID has been destroyed, both
                 * practices are discouraged.
                 */
                val id: String,
                /**
                 * This field indicates the principal of the operator or
                 * framework that created this volume. It is used in conjunction
                 * with the "destroy" ACL to determine whether an entity
                 * attempting to destroy the volume is permitted to do so.
                 * NOTE: This field should match the FrameworkInfo.principal of
                 * the framework that created the volume.
                 */
                val principal: String?)

        /**
         * Describes where a disk originates from.
         */
        data class Source(
                val type: Type,
                val path: Path?,
                val mount: Mount?,
                /**
                 * An identifier for this source. This field maps onto CSI
                 * volume IDs and is not expected to be set by frameworks.
                 */
                val id: String?,            // EXPERIMENTAL.
                /**
                 * Additional metadata for this source. This field maps onto CSI
                 * volume metadata and is not expected to be set by frameworks.
                 */
                val metadata: Labels?,      // EXPERIMENTAL.
                /**
                 * This field serves as an indirection to a set of storage
                 * vendor specific disk parameters which describe the properties
                 * of the disk. The operator will setup mappings between a
                 * profile name to a set of vendor specific disk parameters. And
                 * the framework will do disk selection based on profile names,
                 * instead of vendor specific disk parameters.
                 * Also see the DiskProfileAdaptor module.
                 */
                val profile: String?        // EXPERIMENTAL.
        ) {
            enum class Type {
                UNKNOWN,
                PATH,
                MOUNT,
                BLOCK,
                RAW
            }

            /**
             * A folder that can be located on a separate disk device. This
             * can be shared and carved up as necessary between frameworks.
             */
            data class Path(
                    /**
                     * Path to the folder (e.g., /mnt/raid/disk0). If the path is a
                     * relative path, it is relative to the agent work directory.
                     */
                    val root: String?)

            /**
             * A mounted file-system set up by the Agent administrator. This
             * can only be used exclusively: a framework cannot accept a
             * partial amount of this disk.
             */
            data class Mount(
                    /**
                     * Path to mount point (e.g., /mnt/raid/disk0). If the path is a
                     * relative path, it is relative to the agent work directory.
                     */
                    val root: String?)
        }
    }

    class RevocableInfo

    /**
     * Allow the resource to be shared across tasks.
     */
    class SharedInfo

}
