package net.oddpoet.mesos.http.dto

/**
 * Describes a network request from a framework as well as network resolution
 * provided by Mesos.
 *
 * A framework may request the network isolator on the Agent to isolate the
 * container in a network namespace and create a virtual network interface.
 * The `NetworkInfo` message describes the properties of that virtual
 * interface, including the IP addresses and network isolation policy
 * (network group membership).
 *
 * The NetworkInfo message is not interpreted by the Master or Agent and is
 * intended to be used by Agent and Master modules implementing network
 * isolation. If the modules are missing, the message is simply ignored. In
 * future, the task launch will fail if there is no module providing the
 * network isolation capabilities (MESOS-3390).
 *
 * An executor, Agent, or an Agent module may append NetworkInfos inside
 * TaskStatus::container_status to provide information such as the container IP
 * address and isolation groups.
 */
data class NetworkInfo(
        /**
         * When included in a ContainerInfo, each of these represent a
         * request for an IP address. Each request can specify an explicit address
         * or the IP protocol to use.
         *
         * When included in a TaskStatus message, these inform the framework
         * scheduler about the IP addresses that are bound to the container
         * interface. When there are no custom network isolator modules installed,
         * this field is filled in automatically with the Agent IP address.
         */
        val ipAddresses: List<IPAddress>?,
        /**
         * Name of the network which will be used by network isolator to determine
         * the network that the container joins. It's up to the network isolator
         * to decide how to interpret this field.
         */
        val name: String?,
        /**
         * A group is the name given to a set of logically-related interfaces that
         * are allowed to communicate among themselves. Network traffic is allowed
         * between two container interfaces that share at least one network group.
         * For example, one might want to create separate groups for isolating dev,
         * testing, qa and prod deployment environments.
         */
        val groups: List<String>?,
        /**
         * To tag certain metadata to be used by Isolator/IPAM, e.g., rack, etc.
         */
        val labels: Labels?,
        val portMappings: List<PortMapping>?) {


    enum class Protocol { IPv4, IPv6 }

    /**
     * Specifies a request for an IP address, or reports the assigned container
     * IP address.
     *
     * Users can request an automatically assigned IP (for example, via an
     * IPAM service) or a specific IP by adding a NetworkInfo to the
     * ContainerInfo for a task.  On a request, specifying neither `protocol`
     * nor `ip_address` means that any available address may be assigned.
     */
    data class IPAddress(
            /**
             * Specify IP address requirement. Set protocol to the desired value to
             * request the network isolator on the Agent to assign an IP address to the
             * container being launched. If a specific IP address is specified in
             * ip_address, this field should not be set.
             */
            val protocol: Protocol?,
            /**
             * Statically assigned IP provided by the Framework. This IP will be
             * assigned to the container by the network isolator module on the Agent.
             * This field should not be used with the protocol field above.
             *
             * If an explicit address is requested but is unavailable, the network
             * isolator should fail the task.
             */
            val ipAddress: String?)

    /**
     * Specifies a port mapping request for the task on this network.
     */
    data class PortMapping(
            val hostPort: Int,
            val containerPort: Int,
            /**
             * Protocol to expose as (ie: tcp, udp).
             */
            val protocol: String)

}
