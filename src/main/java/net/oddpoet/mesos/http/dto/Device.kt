package net.oddpoet.mesos.http.dto

/**
 * Describes information about a device.
 */
class Device(
        val path: String?,
        val number: Number?) {
    data class Number(
            val majorNumber: Long,
            val minorNumber: Long)
}
