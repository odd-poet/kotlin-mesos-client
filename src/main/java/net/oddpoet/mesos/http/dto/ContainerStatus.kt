package net.oddpoet.mesos.http.dto

/**
 * Container related information that is resolved during container
 * setup. The information is sent back to the framework as part of the
 * TaskStatus message.
 */
data class ContainerStatus(
        val containerId: ContainerID?,
        /**
         * This field can be reliably used to identify the container IP address.
         */
        val networkInfos: List<NetworkInfo>?,
        /**
         * Information about Linux control group (cgroup).
         */
        val cgroupInfo: CgroupInfo,
        /**
         * Information about Executor PID.
         */
        val executorPid: Int?)
