package net.oddpoet.mesos.http.dto

/**
 * A request to return some resources occupied by a framework.
 */
data class InverseOffer(
        /**
         * This is the same OfferID as found in normal offers, which allows
         * re-use of some of the OfferID-only messages.
         */
        val id: OfferID,
        /**
         * URL for reaching the agent running on the host.  This enables some
         * optimizations as described in MESOS-3012, such as allowing the
         * scheduler driver to bypass the master and talk directly with an agent.
         */
        val url: URL?,
        /**
         * The framework that should release its resources.
         * If no specifics are provided (i.e. which agent), all the framework's
         * resources are requested back.
         */
        val frameworkId: FrameworkID,
        /**
         * Specified if the resources need to be released from a particular agent.
         * All the framework's resources on this agent are requested back,
         * unless further qualified by the `resources` field.
         */
        val agentId: AgentID?,
        /**
         * This InverseOffer represents a planned unavailability event in the
         * specified interval.  Any tasks running on the given framework or agent
         * may be killed when the interval arrives.  Therefore, frameworks should
         * aim to gracefully terminate tasks prior to the arrival of the interval.

         * For reserved resources, the resources are expected to be returned to the
         * framework after the unavailability interval.  This is an expectation,
         * not a guarantee.  For example, if the unavailability duration is not set,
         * the resources may be removed permanently.

         * For other resources, there is no guarantee that requested resources will
         * be returned after the unavailability interval.  The allocator has no
         * obligation to re-offer these resources to the prior framework after
         * the unavailability.
         */
        val unavailability: Unavailability,
        /**
         * A list of resources being requested back from the framework,
         * on the agent identified by `agent_id`.  If no resources are specified
         * then all resources are being requested back.  For the purpose of
         * maintenance, this field is always empty (maintenance always requests
         * all resources back).
         */
        val resources: List<Resource>)