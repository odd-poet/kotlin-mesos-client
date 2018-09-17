package net.oddpoet.mesos.http.dto

/**
 * Represents an interval, from a given start time over a given duration.
 * This interval pertains to an unavailability event, such as maintenance,
 * and is not a generic interval.
 */
data class Unavailability(
        val start: TimeInfo,
        /**
         * When added to `start`, this represents the end of the interval.
         * If unspecified, the duration is assumed to be infinite.
         */
        val duration: DurationInfo?)