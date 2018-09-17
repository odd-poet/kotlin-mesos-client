package net.oddpoet.mesos.http.dto

/**
 * Describes the container configuration to run a CSI plugin component.
 */
data class CSIPluginContainerInfo(
        val services: List<Service>?,
        val command: CommandInfo?,
        val resources: List<Resource>?,
        val container: ContainerInfo?) {

    enum class Service {
        UNKNOWN,
        CONTROLLER_SERVICE,
        NODE_SERVICE
    }
}