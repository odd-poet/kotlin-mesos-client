package net.oddpoet.mesos.http.dto

/**
 * Named port used for service discovery.
 */
data class Port(
        val number: Int,
        val name: String?,
        val protocol: String?,
        val visibility: DiscoveryInfo.Visibility?,
        val labels: Labels?)