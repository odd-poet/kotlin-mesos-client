package net.oddpoet.mesos.http.dto

/**
 * A unique ID assigned to a framework. A framework can reuse this ID
 * in order to do failover (see MesosSchedulerDriver).
 */
data class FrameworkID(val value: String)