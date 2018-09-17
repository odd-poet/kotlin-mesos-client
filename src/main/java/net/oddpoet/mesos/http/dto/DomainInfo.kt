package net.oddpoet.mesos.http.dto

/**
 * Describes a domain. A domain is a collection of hosts that have
 * similar characteristics. Mesos currently only supports "fault
 * domains", which identify groups of hosts with similar failure
 * characteristics.
 *
 * Frameworks can generally assume that network links between hosts in
 * the same fault domain have lower latency, higher bandwidth, and better
 * availability than network links between hosts in different domains.
 * Schedulers may prefer to place network-intensive workloads in the
 * same domain, as this may improve performance. Conversely, a single
 * failure that affects a host in a domain may be more likely to
 * affect other hosts in the same domain; hence, schedulers may prefer
 * to place workloads that require high availability in multiple
 * domains. (For example, all the hosts in a single rack might lose
 * power or network connectivity simultaneously.)
 *
 * There are two kinds of fault domains: regions and zones. Regions
 * offer the highest degree of fault isolation, but network latency
 * between regions is typically high (typically >50 ms). Zones offer a
 * modest degree of fault isolation along with reasonably low network
 * latency (typically <10 ms).
 *
 * The mapping from fault domains to physical infrastructure is up to
 * the operator to configure. In cloud environments, regions and zones
 * can be mapped to the "region" and "availability zone" concepts
 * exposed by most cloud providers, respectively. In on-premise
 * deployments, regions and zones can be mapped to data centers and
 * racks, respectively.
 *
 * Both masters and agents can be configured with domains. Frameworks
 * can compare the domains of two hosts to determine if the hosts are
 * in the same zone, in different zones in the same region, or in
 * different regions. Note that all masters in a given Mesos cluster
 * must be in the same region.
 *
 * ref: https://github.com/apache/mesos/blob/master/include/mesos/v1/mesos.proto#L829
 *
 */
data class DomainInfo(val faultDomain: FaultDomain?) {
    data class FaultDomain(
            val region: RegionInfo,
            val zone: ZoneInfo) {
        data class RegionInfo(val name: String)
        data class ZoneInfo(val name: String)
    }
}
