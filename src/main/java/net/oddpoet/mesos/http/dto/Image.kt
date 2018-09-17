package net.oddpoet.mesos.http.dto

/**
 * Describe an image used by tasks or executors. Note that it's only
 * for tasks or executors launched by MesosContainerizer currently.
 */
data class Image(
        val type: Type,
        /*
        Only one of the following image messages should be set to match
        the type.
        */
        val appc: Appc?,
        val docker: Docker?,
        /**
         * With this flag set to false, the mesos containerizer will pull
         * the docker/appc image from the registry even if the image is
         * already downloaded on the agent.
         */
        val cached: Boolean?) {
    enum class Type {
        APPC,
        DOCKER
    }

    /**
     * Protobuf for specifying an Appc container image. See:
     *https://github.com/appc/spec/blob/master/spec/aci.md
     */
    data class Appc(
            /**
             * The name of the image.
             */
            val name: String,
            /**
             * An image ID is a string of the format "hash-value", where
             * "hash" is the hash algorithm used and "value" is the hex
             * encoded string of the digest. Currently the only permitted
             * hash algorithm is sha512.
             */
            val id: String?,
            /**
             * Optional labels. Suggested labels: "version", "os", and "arch".
             */
            val labels: Labels?)

    data class Docker(
            /**
             * The name of the image. Expected format:
             * [REGISTRY_HOST[:REGISTRY_PORT]/]REPOSITORY[:TAG|@TYPE:DIGEST]
             *
             * See: https://docs.docker.com/reference/commandline/pull/
             */
            val name: String,
            /**
             * Credential to authenticate with docker registry.
             * NOTE: This is not encrypted, therefore framework and operators
             * should enable SSL when passing this information.
             *
             * This field has never been used in Mesos before and is
             * deprecated since Mesos 1.3. Please use `config` below
             * (see MESOS-7088 for details).
             */
            val credential: Credential?,
            /**
             * Docker config containing credentials to authenticate with
             * docker registry. The secret is expected to be a docker
             * config file in JSON format with UTF-8 character encoding.
             */
            val config: Secret?)

}
