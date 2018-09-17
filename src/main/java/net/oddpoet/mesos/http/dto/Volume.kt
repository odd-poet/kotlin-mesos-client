package net.oddpoet.mesos.http.dto


/**
 * Describes a volume mapping either from host to container or vice
 * versa. Both paths can either refer to a directory or a file.
 */
class Volume(
        val mode: Mode,
        /**
         * Path pointing to a directory or file in the container. If the
         * path is a relative path, it is relative to the container work
         * directory. If the path is an absolute path, that path must
         * already exist.
         */
        val containerPath: String,
        /**
         * Absolute path pointing to a directory or file on the host or a
         * path relative to the container work directory.
         */
        val hostPath: String?,
        /**
         * The source of the volume is an Image which describes a root
         * filesystem which will be provisioned by Mesos.
         */
        val image: Image?,
        val source: Source?) {
    enum class Mode { RW, RO }

    /**
     * Describes where a volume originates from.
     */
    data class Source(
            /**
             * Enum fields should be optional, see: MESOS-4997.
             */
            val type: Type?,
            /**
             * The source of the volume created by docker volume driver.
             */
            val dockerVolume: DockerVolume?,
            val hostPath: HostPath?,
            val sandboxPath: SandboxPath?,
            /**
             * The volume/secret isolator uses the secret-fetcher module (third-party or
             * internal) downloads the secret and makes it available at container_path.
             */
            val secret: Secret?) {

        enum class Type {
            /**
             * This must be the first enum value in this list, to
             * ensure that if 'type' is not set, the default value
             * is UNKNOWN. This enables enum values to be added
             * in a backwards-compatible way. See: MESOS-4997.
             */
            UNKNOWN,
            DOCKER_VOLUME,
            HOST_PATH,
            SANDBOX_PATH,
            SECRET
        }

        data class DockerVolume(
                /**
                 * Driver of the volume, it can be flocker, convoy, raxrey etc.
                 */
                val driver: String?,
                /**
                 * Name of the volume.
                 */
                val name: String,
                /**
                 * Volume driver specific options.
                 */
                val driverOptions: Parameters?)

        /**
         * Absolute path pointing to a directory or file on the host.
         */
        data class HostPath(
                val path: String,
                val mountPropagation: MountPropagation?)

        /**
         * Describe a path from a container's sandbox. The container can
         * be the current container (SELF), or its parent container
         * (PARENT). PARENT allows all child containers to share a volume
         * from their parent container's sandbox. It'll be an error if
         * the current container is a top level container.
         */
        data class SandboxPath(
                val type: Type?,
                /**
                 * A path relative to the corresponding container's sandbox.
                 * Note that upwards traversal (i.e. ../../abc) is not allowed.
                 */
                val path: String) {
            enum class Type {
                UNKNOWN,
                SELF,
                PARENT
            }
        }
    }


}