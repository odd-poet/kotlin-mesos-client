package net.oddpoet.mesos.http.dto

/**
 * Describes a container configuration and allows extensible
 * configurations for different container implementations.
 *
 * NOTE: `ContainerInfo` may be specified, e.g., by a task, even if no
 * container image is provided. In this case neither `MesosInfo` nor
 * `DockerInfo` is set, the required `type` must be `MESOS`. This is to
 * address a case when a task without an image, e.g., a shell script
 * with URIs, wants to use features originally designed for containers,
 * for example custom network isolation via `NetworkInfo`.
 */
data class ContainerInfo(
        val type: Type,
        val volumes: List<Volume>?,
        val hostname: String?,
        /**
         * Only one of the following *Info messages should be set to match
         * the type.
         */
        val docker: DockerInfo?,
        val mesos: MesosInfo?,
        /**
         * A list of network requests. A framework can request multiple IP addresses
         * for the container.
         */
        val networkInfos: List<NetworkInfo>?,
        /**
         * Linux specific information for the container.
         */
        val linuxInfo: LinuxInfo?,
        /**
         * (POSIX only) rlimits of the container.
         */
        val rlimitInfo: RLimitInfo?,
        /**
         * If specified a tty will be attached to the container entrypoint.
         */
        val ttyInfo: TTYInfo?) {

    /**
     * All container implementation types.
     */
    enum class Type {
        DOCKER,
        MESOS
    }

    data class DockerInfo(
            /**
             * The docker image that is going to be passed to the registry.
             */
            val image: String,
            val network: Network?,
            val portMappings: List<PortMapping>?,
            val privileged: Boolean?,
            /**
             * Allowing arbitrary parameters to be passed to docker CLI.
             * Note that anything passed to this field is not guaranteed
             * to be supported moving forward, as we might move away from
             * the docker CLI.
             */
            val parameters: List<Parameter>?,
            /**
             * With this flag set to true, the docker containerizer will
             * pull the docker image from the registry even if the image
             * is already downloaded on the agent.
             */
            val forcePullImage: Boolean?,
            /**
             * the name of volume driver plugin.
             */
            val volumeDriver: String?) {

        /**
         * Network options.
         */
        enum class Network {
            HOST,
            BRIDGE,
            NONE,
            USER
        }

        data class PortMapping(
                val hostPort: Int,
                val containerPort: Int,
                /**
                 * Protocol to expose as (ie: tcp, udp).
                 */
                val protocol: String?)
    }

    data class MesosInfo(val image: Image?)
}
