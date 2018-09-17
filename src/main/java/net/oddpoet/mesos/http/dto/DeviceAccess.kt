package net.oddpoet.mesos.http.dto

/**
 * Describes a device whitelist entry that expose from host to container.
 */
data class DeviceAccess(
        val device: Device,
        val access: Access) {

    data class Access(
            val read: Boolean?,
            val write: Boolean?,
            val mknod: Boolean?)
}