package net.oddpoet.mesos.http.dto

/**
 * A network address.
 *
 * ref: https://github.com/apache/mesos/blob/master/include/mesos/v1/mesos.proto#L137
 */
data class Address(
        val hostname: String?,
        val ip: String?,
        val port: Int)
