package net.oddpoet.mesos.data

/**
 * http://mesos.apache.org/documentation/latest/scheduler-http-api/
 * https://github.com/apache/mesos/blob/master/include/mesos/v1/mesos.proto
 * https://github.com/apache/mesos/blob/master/include/mesos/v1/scheduler/scheduler.proto
 *
 */

sealed class Event(val type: Type) {
    enum class Type {
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
        HEARTBEAT
    }

    data class Subscribe(
            val frameworkId: String,
            val heartbeatIntervalSeconds: Double?,
            val masterInfo: MasterInfo?
    ) : Event(Type.SUBSCRIBED)

    data class Offers(
            val offers: List<Offer>
    ) : Event(Type.OFFERS)

    data class Rescind(
            val offerId: String
    ) : Event(Type.RESCIND)

    data class Update(
            val taskId: String,
            val status: String,
            val source: String,
            val uuid: String,
            val bytes: String
    ) : Event(Type.UPDATE)

    data class Message(
            val agentId: String,
            val executorId: String,
            val data: String
    ) : Event(Type.MESSAGE)

    data class Failure(
            val agentId: String?,
            val executorId: String?,
            val status: Int?
    ) : Event(Type.FAILURE)

    data class Error(
            val message: String
    ) : Event(Type.ERROR)

    class Heartbeat : Event(Type.HEARTBEAT)
}


data class Resource(
        val name: String,
        val type: Type
) {
    enum class Type {
        SCALAR,
    }
}
