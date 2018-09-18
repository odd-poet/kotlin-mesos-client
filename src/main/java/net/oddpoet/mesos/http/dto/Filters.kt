package net.oddpoet.mesos.http.dto

/**
 * Describes possible filters that can be applied to unused resources
 * (see SchedulerDriver::launchTasks) to influence the allocator.
 */
data class Filters(
        /**
         * Time to consider unused resources refused. Note that all unused
         * resources will be considered refused and use the default value
         * (below) regardless of whether Filters was passed to
         * SchedulerDriver::launchTasks. You MUST pass Filters with this
         * field set to change this behavior (i.e., get another offer which
         * includes unused resources sooner or later than the default).

         * If this field is set to a number of seconds greater than 31536000
         * (365 days), then the resources will be considered refused for 365
         * days. If it is set to a negative number, then the default value
         * will be used.
         */
        val refuseSeconds: Double? = null
)

