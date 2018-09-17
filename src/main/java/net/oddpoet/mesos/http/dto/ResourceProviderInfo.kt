package net.oddpoet.mesos.http.dto

/**
 * Describes a resource provider. Note that the 'id' field is only available
 * after a resource provider is registered with the master, and is made
 * available here to facilitate re-registration.
 */
data class ResourceProviderInfo(
        val id: ResourceProviderID,
        val attributes: List<Attribute>?,
        /**
         * The type of the resource provider. This uniquely identifies a
         * resource provider implementation. For instance:
         * org.apache.mesos.rp.local.storage
         * Please follow to Java package naming convention
         * (https://en.wikipedia.org/wiki/Java_package#Package_naming_conventions)
         * to avoid conflicts on type names.
         */
        val type: String,
        /**
         * The name of the resource provider. There could be multiple
         * instances of a type of resource provider. The name field is used
         * to distinguish these instances. It should be a legal Java identifier
         * (https://docs.oracle.com/javase/tutorial/java/nutsandbolts/variables.html)
         * to avoid conflicts on concatenation of type and name.
         */
        val name: String,
        /**
         * The stack of default reservations. If this field is not empty, it
         * indicates that resources from this resource provider are reserved
         * by default, except for the resources that have been reserved or
         * unreserved through operations. The first `ReservationInfo`
         * may have type `STATIC` or `DYNAMIC`, but the rest must have
         * `DYNAMIC`. One can create a new reservation on top of an existing
         * one by pushing a new `ReservationInfo` to the back. The last
         * `ReservationInfo` in this stack is the "current" reservation. The
         * new reservation's role must be a child of the current one.
         */
        val defaultReservations: List<Resource.ReservationInfo>?,   // EXPERIMENTAL.
        val sotrage: Storage?       // EXPERIMENTAL.
) {

    /**
     * Storage resource provider related information.
     */
    data class Storage(
            val plugin: CSIPluginInfo
    )
}