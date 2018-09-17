package net.oddpoet.mesos.http.dto

/**
 * Describes a master. This will probably have more fields in the
 * future which might be used, for example, to link a framework webui
 * to a master webui.
 */
data class MasterInfo(
        val id: String,
        /**
         * The IP address (only IPv4) as a packed 4-bytes integer,
         * stored in network order.  Deprecated, use `address.ip` instead.
         */
        val ip: String,
        /**
         * The TCP port the Master is listening on for incoming
         * HTTP requests; deprecated, use `address.port` instead.
         */
        val port: Int,
        /**
         * In the default implementation, this will contain information
         * about both the IP address, port and Master name; it should really
         * not be relied upon by external tooling/frameworks and be
         * considered an "internal" implementation field.
         */
        val pid: String?,
        /**
         * The server's hostname, if available; it may be unreliable
         * in environments where the DNS configuration does not resolve
         * internal hostnames (eg, some public cloud providers).
         * Deprecated, use `address.hostname` instead.
         */
        val hostname: String?,
        /**
         * The running Master version, as a string; taken from the
         * generated "master/version.hpp".
         */
        val version: String?,
        /**
         * The full IP address (supports both IPv4 and IPv6 formats)
         * and supersedes the use of `ip`, `port` and `hostname`.
         * Since Mesos 0.24.
         */
        val address: Address?,
        /**
         * The domain that this master belongs to. All masters in a Mesos
         * cluster should belong to the same region.
         */
        val domain: DomainInfo?,
        val capabilities: List<Capability>) {

    data class Capability(
            val type: Type?) {
        enum class Type {
            UNKNOWN,
            /**
             * The master can handle slaves whose state
             * changes after reregistering.
             */
            AGENT_UPDATE
        }
    }
}