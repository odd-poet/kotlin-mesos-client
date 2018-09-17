package net.oddpoet.mesos.http.dto

/**
 * Scheduler event API.
 *
 * An event is described using the standard protocol buffer "union"
 * trick, see:
 * https://developers.google.com/protocol-buffers/docs/techniques#union.
 *
 * ref: https://github.com/apache/mesos/blob/master/include/mesos/v1/scheduler/scheduler.proto#L34
 */
data class Event(
        val type: Type? = Type.UNKNOWN,
        val subscribed: Subscribed? = null,
        val offers: Offers? = null) {

    /**
     * Possible event types, followed by message definitions if applicable.
     */
    enum class Type {
        /**
         * This must be the first enum value in ths list,
         * to ensure that if 'type' is not set, the default value is UNKNOWN.
         * This enables enum values to be added in a backwards-compatible way. See: MESOS-4997
         */
        UNKNOWN,
        SUBSCRIBED,
        OFFERS,
        INVERSE_OFFERS,
        RESCIND,
        RESCIND_INVERSE_OFFER,
        UPDATE,
        UPDATE_OPERATION_STATUS,
        MESSAGE,
        FAILURE,
        ERROR,

        /**
         * Periodic message sent by the Mesos master according to
         * 'Subscribed.heartbeat_interval_seconds'. If the scheduler does
         * not receive any events (including heartbeats) for an extended
         * period of time (e.g., 5 x heartbeat_interval_seconds), there is
         * likely a network partition. In such a case the scheduler should
         * close the existing subscription connection and resubscribe
         * using a backoff strategy.
         */
        HEARTBEAT
    }


    /**
     * First event received when the scheduler subscribes.
     */
    data class Subscribed(
            val frameworkId: FrameworkID,
            /**
             * This value will be set if the master is sending heartbeats. See the comment above on 'HEARTBEAT'
             * for more details.
             */
            val heartbeatIntervalSeconds: Double?,
            /**
             * Since Mesos 1.1.
             */
            val masterInfo: MasterInfo?)

    /**
     * Received whenever there are new resources that are offered to the
     * scheduler. Each offer corresponds to a set of resources on an
     * agent. Until the scheduler accepts or declines an offer the
     * resources are considered allocated to the scheduler.
     */
    data class Offers(val offers: List<Offer>)

}

