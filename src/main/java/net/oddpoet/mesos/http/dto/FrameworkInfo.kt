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
        val labels: Labels? = null
)
