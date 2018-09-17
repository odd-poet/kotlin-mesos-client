package net.oddpoet.mesos.http.dto

/**
 * Describes the status of a check. Type and the corresponding field, i.e.,
 * `command` or `http` must be set. If the result of the check is not available
 * (e.g., the check timed out), these fields must contain empty messages, i.e.,
 * `exit_code` or `status_code` will be unset.
 *
 * NOTE: This API is subject to change and the related feature is experimental.
 */
data class CheckStatusInfo(
        /**
         * The type of the check this status corresponds to.
         */
        val type: CheckInfo.Type?,
        /**
         * Status of a command check.
         */
        val command: Command?,
        /**
         * Status of a HTTP check.
         */
        val http: Http?,
        /**
         * Status of a TCP check.
         */
        val tcp: Tcp?) {

    data class Command(
            /**
             * Exit code of a command check. It is the result of calling
             * `WEXITSTATUS()` on `waitpid()` termination information on
             * Posix and calling `GetExitCodeProcess()` on Windows.
             */
            val exitCode: Int?)

    data class Http(
            /**
             * HTTP status code of an HTTP check.
             */
            val statusCode: Int?)

    data class Tcp(
            /**
             * Whether a TCP connection succeeded.
             */
            val succeeded: Boolean?)

}
