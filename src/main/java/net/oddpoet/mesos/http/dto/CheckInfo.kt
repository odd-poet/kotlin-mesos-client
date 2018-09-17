package net.oddpoet.mesos.http.dto

/**
 * Describes a general non-interpreting non-killing check for a task or
 * executor (or any arbitrary process/command). A type is picked by
 * specifying one of the optional fields. Specifying more than one type
 * is an error.
 *
 * NOTE: This API is subject to change and the related feature is experimental.
 */
data class CheckInfo(
        /**
         * The type of the check.
         */
        val type: Type?,
        /**
         * Command check.
         */
        val command: Command?,
        /**
         * HTTP check.
         */
        val http: Http?,
        /**
         * TCP check.
         */
        val tcp: Tcp?,
        /**
         * Amount of time to wait to start checking the task after it
         * transitions to `TASK_RUNNING` or `TASK_STARTING` if the latter
         * is used by the executor.
         */
        val delaySeconds: Double?,
        /**
         * Interval between check attempts, i.e., amount of time to wait after
         * the previous check finished or timed out to start the next check.
         */
        val intervalSeconds: Double?,
        /**
         * Amount of time to wait for the check to complete. Zero means infinite
         * timeout.
         *
         * After this timeout, the check attempt is aborted and no result is
         * reported. Note that this may be considered a state change and hence
         * may trigger a check status change delivery to the corresponding
         * scheduler. See `CheckStatusInfo` for more details.
         */
        val timeoutSeconds: Double?) {

    enum class Type {
        UNKNOWN,
        COMMAND,
        HTTP,
        TCP
    }

    /**
     * Describes a command check. If applicable, enters mount and/or network
     * namespaces of the task.
     */
    data class Command(
            val command: CommandInfo)

    /**
     * Describes an HTTP check. Sends a GET request to
     * http://<host>:port/path. Note that <host> is not configurable and is
     * resolved automatically to 127.0.0.1.
     */
    data class Http(
            /**
             * Port to send the HTTP request.
             */
            val port: Int,
            /**
             * HTTP request path.
             */
            val path: String?)

    /**
     * Describes a TCP check, i.e. based on establishing a TCP connection to
     * the specified port. Note that <host> is not configurable and is resolved
     * automatically to 127.0.0.1.
     */
    data class Tcp(
            val port: Int)


}
