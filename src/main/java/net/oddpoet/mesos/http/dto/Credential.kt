package net.oddpoet.mesos.http.dto

/**
 * Credential used in various places for authentication and
 * authorization.
 *
 * NOTE: A 'principal' is different from 'FrameworkInfo.user'. The
 * former is used for authentication and authorization while the
 * latter is used to determine the default user under which the
 * framework's executors/tasks are run.
 */
data class Credential(
        val principal: String,
        val secret: String?)

