package net.oddpoet.mesos.http.dto

/**
 *
 * @author Yunsang Choi
 */
class Volume(
        val mode: Mode,
        val containerPath: String,
        val hostPath: String?,
        val image: Image?,
        val source: Source?) {
    enum class Mode { RW, RO }

    data class Source(
            val type: Type?,
            val dockerVolume: DockerVolume?,
            val hostPath: HostPath?,
            val sandboxPath: SandboxPath?,
            val secret: Secret?) {

        enum class Type {
            UNKNOWN,
            DOCKER_VOLUME,
            HOST_PATH,
            SANDBOX_PATH,
            SECRET
        }

        data class DockerVolume(
                val driver: String?,
                val name: String,
                val driverOptions: Parameters?
        )

        data class HostPath(val path: String, val mountPropagation: MountPropagation?)

        data class SandboxPath(
                val type: Type?,
                val path: String) {
            enum class Type {
                UNKNOWN,
                SELF,
                PARENT
            }
        }
    }


}