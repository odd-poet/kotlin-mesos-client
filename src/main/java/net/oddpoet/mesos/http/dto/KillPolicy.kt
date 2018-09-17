package net.oddpoet.mesos.http.dto

/**
 * Describes a kill policy for a task. Currently does not express
 * different policies (e.g. hitting HTTP endpoints), only controls
 * how long to wait between graceful and forcible task kill:
 *
 *     graceful kill --------------> forcible kill
 *                    grace_period
 *
 * Kill policies are best-effort, because machine failures / forcible
 * terminations may occur.
 *
 * NOTE: For executor-less command-based tasks, the kill is performed
 * via sending a signal to the task process: SIGTERM for the graceful
 * kill and SIGKILL for the forcible kill. For the docker executor-less
 * tasks the grace period is passed to 'docker stop --time'.
 */
data class KillPolicy(
        /**
         * The grace period specifies how long to wait before forcibly
         * killing the task. It is recommended to attempt to gracefully
         * kill the task (and send TASK_KILLING) to indicate that the
         * graceful kill is in progress. Once the grace period elapses,
         * if the task has not terminated, a forcible kill should occur.
         * The task should not assume that it will always be allotted
         * the full grace period. For example, the executor may be
         * shutdown more quickly by the agent, or failures / forcible
         * terminations may occur.
         */
        val gracePeriod: DurationInfo?
)
