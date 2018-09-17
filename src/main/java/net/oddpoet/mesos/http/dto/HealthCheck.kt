package net.oddpoet.mesos.http.dto

/**
 * Describes a health check for a task or executor (or any arbitrary
 * process/command). A type is picked by specifying one of the
 * optional fields. Specifying more than one type is an error.
 */
data class HealthCheck(
        /**
         * Amount of time to wait to start health checking the task after it
         */
        val deloaySeconds: Double?,
        /**
         * Interval between health checks, i.e., amount of time to wait after
         * the previous health check finished or timed out to start the next
         * health check.
         */
        val intervalSeconds: Double?,
        /**
         * Amount of time to wait for the health check to complete. After this
         * timeout, the health check is aborted and treated as a failure. Zero
         * means infinite timeout.
         */
        val timeoutSeconds: Double?,
        /**
         *  Number of consecutive failures until the task is killed by the executor.
         */
        val consecutiveFailures: Int?,
        /**
         * Amount of time after the task is launched during which health check
         * failures are ignored. Once a check succeeds for the first time,
         * the grace period does not apply anymore. Note that it includes
         * `delay_seconds`, i.e., setting `grace_period_seconds` < `delay_seconds`
         * has no effect.
         */
        val gracePeriodSeconds: Double?,
        /**
         * The type of health check.
         */
        val type: Type?,
        /**
         * Command health check.
         */
        val command: CommandInfo?,
        /**
         *  HTTP health check.
         */
        val http: HTTPCheckInfo?,
        /**
         *  TCP health check.
         */
        val tcp: TCPCheckInfo?) {

    enum class Type {
        UNKNOWN,
        COMMAND,
        HTTP,
        TCP
    }

    /**
     * Describes an HTTP health check. Sends a GET request to
     * scheme://<host>:port/path. Note that <host> is not configurable and is
     * resolved automatically, in most cases to 127.0.0.1. Default executors
     * treat return codes between 200 and 399 as success; custom executors
     * may employ a different strategy, e.g. leveraging the `statuses` field.
     */
    data class HTTPCheckInfo(
            val protocol: NetworkInfo.Protocol?,
            /**
             * Currently "http" and "https" are supported.
             */
            val scheme: String?,
            /**
             * Port to send the HTTP request.
             */
            val port: Int,
            /**
             * HTTP request path.
             */
            val path: String?,
            /**
             * NOTE: It is up to the custom executor to interpret and act on this
             * field. Setting this field has no effect on the default executors.
             * TODO(haosdent): Deprecate this field when we add better support for
             * success and possibly failure statuses, e.g. ranges of success and
             * failure statuses.
             */
            val statuses: List<Int>)

    /**
     * Describes a TCP health check, i.e. based on establishing
     * a TCP connection to the specified port.
     */
    data class TCPCheckInfo(
            val protocol: NetworkInfo.Protocol?,
            /**
             * Port expected to be open.
             */
            val port: Int)
}
