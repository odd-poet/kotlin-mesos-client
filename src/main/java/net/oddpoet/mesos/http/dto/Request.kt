package net.oddpoet.mesos.http.dto

/**
 * Describes a request for resources that can be used by a framework
 * to proactively influence the allocator.  If 'agent_id' is provided
 * then this request is assumed to only apply to resources on that
 * agent.
 */
data class Request(
        val agentId: AgentID?,
        val resources: List<Resource>?)
