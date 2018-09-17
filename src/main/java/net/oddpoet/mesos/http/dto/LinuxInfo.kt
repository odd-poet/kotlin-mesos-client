package net.oddpoet.mesos.http.dto

/**
 * Encapsulation for Linux specific configuration.
 * E.g, capabilities, limits etc.
 */
data class LinuxInfo(
        /**
         * Since 1.4.0, deprecated in favor of `effective_capabilities`.
         */
        val capabilityInfo: CapabilityInfo?,
        /**
         * The set of capabilities that are allowed but not initially
         * granted to tasks.
         */
        val boundingCapabilities: CapabilityInfo?,
        /**
         * Represents the set of capabilities that the task will
         * be executed with.
         */
        val effectiveCapabilities: CapabilityInfo?,

        /**
         * If set as 'true', the container shares the pid namespace with
         * its parent. If the container is a top level container, it will
         * share the pid namespace with the agent. If the container is a
         * nested container, it will share the pid namespace with its
         * parent container. This field will be ignored if 'namespaces/pid'
         * isolator is not enabled.
         */
        val sharePidNamespace: Boolean?)
