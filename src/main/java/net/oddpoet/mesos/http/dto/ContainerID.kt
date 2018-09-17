package net.oddpoet.mesos.http.dto


/**
 * ID used to uniquely identify a container. If the `parent` is not
 * specified, the ID is a UUID generated by the agent to uniquely
 * identify the container of an executor run. If the `parent` field is
 * specified, it represents a nested container.
 */
data class ContainerID(
        val value: String,
        val parent: ContainerID?)
