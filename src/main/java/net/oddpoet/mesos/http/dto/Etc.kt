package net.oddpoet.mesos.http.dto

/**
 *
 * @author Yunsang Choi
 */

/**
 * A unique ID assigned to a framework. A framework can reuse this ID
 * in order to do failover (see MesosSchedulerDriver).
 */
data class FrameworkID(val value: String)


/**
 * Collection of labels. Labels should not contain duplicate key-value
 * pairs.
 */
data class Labels(val labels: List<Label>)

/**
 * Key, value pair used to store free form user-data.
 */
data class Label(val key: String, val value: String?)

