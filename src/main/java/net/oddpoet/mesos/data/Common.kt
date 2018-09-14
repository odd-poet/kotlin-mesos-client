package net.oddpoet.mesos.data

/**
 *
 *
 *
 */


data class Offer(
        val id: String,
        val frameworkId: String,
        val agentId: String,
        val executor_ids: List<String>,
        val hostname: String,
        val resources: List<Resource>,
        val attributes: Map<String, Any>,
        val allocationInfo: Map<String, String>
)

class MasterInfo()

class TaskStatus()