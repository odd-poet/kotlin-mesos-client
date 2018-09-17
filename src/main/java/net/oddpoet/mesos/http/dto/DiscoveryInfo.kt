package net.oddpoet.mesos.http.dto


/**
 * Service discovery information.
 * The visibility field restricts discovery within a framework (FRAMEWORK),
 * within a Mesos cluster (CLUSTER), or places no restrictions (EXTERNAL).
 * Each port in the ports field also has an optional visibility field.
 * If visibility is specified for a port, it overrides the default service-wide
 * DiscoveryInfo.visibility for that port.
 * The environment, location, and version fields provide first class support for
 * common attributes used to differentiate between similar services. The
 * environment may receive values such as PROD/QA/DEV, the location field may
 * receive values like EAST-US/WEST-US/EUROPE/AMEA, and the version field may
 * receive values like v2.0/v0.9. The exact use of these fields is up to each
 * service discovery system.
 */
data class DiscoveryInfo(
        val visibility: Visibility,
        val name: String?,
        val environment: String?,
        val location: String?,
        val version: String?,
        val ports: Ports?,
        val labels: Labels?) {

    enum class Visibility {
        FRAMEWORK,
        CLUSTER,
        EXTERNAL
    }

}
