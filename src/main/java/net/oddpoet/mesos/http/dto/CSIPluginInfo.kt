package net.oddpoet.mesos.http.dto

/**
 * Describes a CSI plugin.
 */
data class CSIPluginInfo(
        /**
         * The type of the CSI service. This uniquely identifies a CSI
         * implementation. For instance:
         *       org.apache.mesos.csi.test
         * Please follow to Java package naming convention
         * (https://en.wikipedia.org/wiki/Java_package#Package_naming_conventions)
         * to avoid conflicts on type names.
         */
        val type: String,
        /**
         * The name of the CSI service. There could be mutliple instances of a
         * type of CSI service. The name field is used to distinguish these
         * instances. It should be a legal Java identifier
         * (https://docs.oracle.com/javase/tutorial/java/nutsandbolts/variables.html)
         * to avoid conflicts on concatenation of type and name.
         */
        val name: String,
        /**
         * A list of container configurations to run CSI plugin components.
         * The controller service will be served by the first configuration
         * that contains `CONTROLLER_SERVICE`, and the node service will be
         * served by the first configuration that contains `NODE_SERVICE`.
         */
        val containers: List<CSIPluginContainerInfo>?)
