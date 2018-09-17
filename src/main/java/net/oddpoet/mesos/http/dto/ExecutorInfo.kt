package net.oddpoet.mesos.http.dto

import java.util.*

/**
 * Describes information about an executor.
 *
 * ref: https://github.com/apache/mesos/blob/master/include/mesos/v1/mesos.proto#L709
 */
data class ExecutorInfo(
        /**
         * For backwards compatibility, if this field is not set when using `LAUNCH` operation,
         * Mesos will infer the type by checking if `command` is set (`CUSTOM`) or unset (`DEFAULT`). `type` must be set
         * when using `LAUNCH_GROUP` operation.
         *
         */
        val type: Type? = Type.UNKNOWN,
        val executorId: ExecutorID,

        val frameworkId: FrameworkID?,
        val command: CommandInfo?,

        /**
         * Executor provided with a container will launch the container with the executor's CommandInfo and we expect
         * the container to act as a Mesos executor.
         */
        val container: ContainerInfo?,

        val resources: List<Resource>?,
        val name: String?,
        /**
         * 'source' is an identifier style string used by frameworks to
         * track the source of an executor. This is useful when it's
         * possible for different executor ids to be related semantically.
         *
         * NOTE: 'source' is exposed alongside the resource usage of the
         * executor via JSON on the agent. This allows users to import usage
         * information into a time series database for monitoring.
         *
         * This field is deprecated since 1.0. Please use labels for
         * free-form metadata instead.
         */
        val source: String?,
        /**
         * This field can be used to pass arbitrary bytes to an executor.
         */
        val data: String?,

        /**
         * Service discovery information for the executor. It is not
         * interpreted or acted upon by Mesos. It is up to a service
         * discovery system to use this information as needed and to handle
         * executors without service discovery information.
         */
        val discovery: DiscoveryInfo?,
        /**
         * When shutting down an executor the agent will wait in a
         * best-effort manner for the grace period specified here
         * before forcibly destroying the container. The executor
         * must not assume that it will always be allotted the full
         * grace period, as the agent may decide to allot a shorter
         * period and failures / forcible terminations may occur.
         */
        val shutdownGracePeriod: DurationInfo?,
        /**
         * Labels are free-form key value pairs which are exposed through
         * master and agent endpoints. Labels will not be interpreted or
         * acted upon by Mesos itself. As opposed to the data field, labels
         * will be kept in memory on master and agent processes. Therefore,
         * labels should be used to tag executors with lightweight metadata.
         * Labels should not contain duplicate key-value pairs.
         */
        val labels: Labels?) {

    enum class Type {
        UNKNOWN,
        /**
         * Mesos provides a simple built-in default executor that frameworks can leverage to run shell commands and containers.
         *
         * NOTES:
         *
         * 1) `command` must not be set when using a default executor.
         *
         * 2) Default executor only accepts a *single* `LAUNCH` or `LAUNCH_GROUP`
         *    operation.
         *
         * 3) If `container` is set, `container.type` must be `MESOS`
         *    and `container.mesos.image` must not be set.
         */
        DEFAULT,
        /**
         * For frameworks that need custom functionality to run tasks, a `CUSTOM`
         * executor can be used. Note that `command` must be set when using a
         * `CUSTOM` executor.
         */
        CUSTOM
    }

    val dataAsBytes: ByteArray?
        get() = data?.let { Base64.getDecoder().decode(it) }
}
