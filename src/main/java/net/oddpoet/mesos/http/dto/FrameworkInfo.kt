package net.oddpoet.mesos.http.dto

/**
 *
 * @author Yunsang Choi
 */
data class FrameworkInfo(
        /**
         * Used to determine the Unix user that an executor or task should be
         * launched as.
         *
         * When using the MesosSchedulerDriver, if the field is set to an
         * empty string, it will automagically set it to the current user.
         *
         * When using the HTTP Scheduler API, the user has to be set
         * explicitly.
         */
        val user: String,
        /**
         * Name of the framework that shows up in the Mesos Web UI.
         */
        val name: String,
        /**
         * Note that 'id' is only available after a framework has
         * registered, however, it is included here in order to facilitate
         * scheduler failover (i.e., if it is set then the
         * MesosSchedulerDriver expects the scheduler is performing failover).
         */
        val id: FrameworkID? = null,

        /**
         * NOTE: To avoid accidental destruction of tasks, production
         * frameworks typically set this to a large value (e.g., 1 week).
         */
        val failoverTimeout: Double? = null, // default: 0.0

        /**
         * If set, agents running tasks started by this framework will write
         * the framework pid, executor pids and status updates to disk. If
         * the agent exits (e.g., due to a crash or as part of upgrading
         * Mesos), this checkpointed data allows the restarted agent to
         * reconnect to executors that were started by the old instance of
         * the agent. Enabling checkpointing improves fault tolerance, at
         * the cost of a (usually small) increase in disk I/O.
         */
        val checkpoint: Boolean? = null, // default: false


        /**
         * Roles are the entities to which allocations are made.
         * The framework must have at least one role in order to
         * be offered resources. Note that `role` is deprecated
         * in favor of `roles` and only one of these fields must
         * be used. Since we cannot distinguish between empty
         * `roles` and the default unset `role`, we require that
         * frameworks set the `MULTI_ROLE` capability if
         * setting the `roles` field.
         */
        val roles: List<String>? = null,

        /**
         * This field should match the credential's principal the framework
         * uses for authentication. This field is used for framework API
         * rate limiting and dynamic reservations. It should be set even
         * if authentication is not enabled if these features are desired.
         */
        val principal: String? = null,

        /**
         * This field allows a framework to advertise its web UI, so that
         * the Mesos web UI can link to it. It is expected to be a full URL,
         * for example http://my-scheduler.example.com:8080/.
         */
        val webuiUrl: String? = null,

        /**
         * This field allows a framework to advertise its set of
         * capabilities (e.g., ability to receive offers for revocable
         * resources).
         */
        val capabilities: List<Capability>? = null,

        /**
         * Labels are free-form key value pairs supplied by the framework
         * scheduler (e.g., to describe additional functionality offered by
         * the framework). These labels are not interpreted by Mesos itself.
         * Labels should not contain duplicate key-value pairs.
         */
        val labels: Labels? = null) {
    data class Capability(val type: Type) {
        enum class Type {
            /**
             * This must be the first enum value in this list, to
             * ensure that if 'type' is not set, the default value
             * is UNKNOWN. This enables enum values to be added
             * in a backwards-compatible way. See: MESOS-4997.
             */
            UNKNOWN,

            /**
             * Receive offers with revocable resources. See 'Resource'
             * message for details.
             */
            REVOCABLE_RESOURCES,

            /**
             * Receive the TASK_KILLING TaskState when a task is being
             * killed by an executor. The executor will examine this
             * capability to determine whether it can send TASK_KILLING.
             */
            TASK_KILLING_STATE,

            /**
             * Indicates whether the framework is aware of GPU resources.
             * Frameworks that are aware of GPU resources are expected to
             * avoid placing non-GPU workloads on GPU agents, in order
             * to avoid occupying a GPU agent and preventing GPU workloads
             * from running! Currently, if a framework is unaware of GPU
             * resources, it will not be offered *any* of the resources on
             * an agent with GPUs. This restriction is in place because we
             * do not have a revocation mechanism that ensures GPU workloads
             * can evict GPU agent occupants if necessary.
             *
             * TODO(bmahler): As we add revocation we can relax the
             * restriction here. See MESOS-5634 for more information.
             */
            GPU_RESOURCES,

            /**
             *  Receive offers with resources that are shared.
             */
            SHARED_RESOURCES,

            /**
             * Indicates that (1) the framework is prepared to handle the
             * following TaskStates: TASK_UNREACHABLE, TASK_DROPPED,
             * TASK_GONE, TASK_GONE_BY_OPERATOR, and TASK_UNKNOWN, and (2)
             * the framework will assume responsibility for managing
             * partitioned tasks that reregister with the master.
             *
             * Frameworks that enable this capability can define how they
             * would like to handle partitioned tasks. Frameworks will
             * receive TASK_UNREACHABLE for tasks on agents that are
             * partitioned from the master.
             *
             * Without this capability, frameworks will receive TASK_LOST
             * for tasks on partitioned agents.
             * NOTE: Prior to Mesos 1.5, such tasks will be killed by Mesos
             * when the agent reregisters (unless the master has failed over).
             * However due to the lack of benefit in maintaining different
             * behaviors depending on whether the master has failed over
             * (see MESOS-7215), as of 1.5, Mesos will not kill these
             * tasks in either case.
             */
            PARTITION_AWARE,

            /**
             * This expresses the ability for the framework to be
             * "multi-tenant" via using the newly introduced `roles`
             * field, and examining `Offer.allocation_info` to determine
             * which role the offers are being made to. We also
             * expect that "single-tenant" schedulers eventually
             * provide this and move away from the deprecated
             * `role` field.
             */
            MULTI_ROLE,

            /**
             * This capability has two effects for a framework.
             *
             * (1) The framework is offered resources in a new format.
             *
             * The offered resources have the `Resource.reservations` field set
             * rather than `Resource.role` and `Resource.reservation`. In short,
             * an empty `reservations` field denotes unreserved resources, and
             * each `ReservationInfo` in the `reservations` field denotes a
             * reservation that refines the previous one.
             *
             * See the 'Resource Format' section for more details.
             *
             * (2) The framework can create refined reservations.
             *
             * A framework can refine an existing reservation via the
             * `Resource.reservations` field. For example, a reservation for role
             * `eng` can be refined to `eng/front_end`.
             *
             * See `ReservationInfo.reservations` for more details.
             *
             * NOTE: Without this capability, a framework is not offered resources
             * that have refined reservations. A resource is said to have refined
             * reservations if it uses the `Resource.reservations` field, and
             * `Resource.reservations_size() > 1`.
             */
            RESERVATION_REFINEMENT, // EXPERIMENTAL.

            /**
             * Indicates that the framework is prepared to receive offers
             * for agents whose region is different from the master's
             * region. Network links between hosts in different regions
             * typically have higher latency and lower bandwidth than
             * network links within a region, so frameworks should be
             * careful to only place suitable workloads in remote regions.
             * Frameworks that are not region-aware will never receive
             * offers for remote agents; region-aware frameworks are assumed
             * to implement their own logic to decide which workloads (if
             * any) are suitable for placement on remote agents.
             */
            REGION_AWARE;
        }
    }
}
