package net.oddpoet.mesos.http.dto

/**
 * Describes how the mount will be propagated for a volume. See the
 * following doc for more details about mount propagation:
 * https://www.kernel.org/doc/Documentation/filesystems/sharedsubtree.txt
 */
data class MountPropagation(val mode: Mode = Mode.UNKNOWN) {
    enum class Mode {
        UNKNOWN,
        /**
         * The volume in a container will receive new mounts from the host
         * or other containers, but filesystems mounted inside the
         * container won't be propagated to the host or other containers.
         * This is currently the default behavior for all volumes.
         */
        HOST_TO_CONTAINER,
        /**
         * The volume in a container will receive new mounts from the host
         * or other containers, and its own mounts will be propagated from
         * the container to the host or other containers.
         */
        BIDIRECTIONAL
    }
}
