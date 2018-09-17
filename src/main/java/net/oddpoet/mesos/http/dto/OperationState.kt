package net.oddpoet.mesos.http.dto

/**
 * Describes possible operation states.
 */
enum class OperationState {
    /**
     *  Default value if the enum is not set. See MESOS-4997.
     */
    OPERATION_UNSUPPORTED,

    /**
     * Initial state.
     */
    OPERATION_PENDING,

    /**
     *  TERMINAL: The operation was successfully applied.
     */
    OPERATION_FINISHED,

    /**
     *  TERMINAL: The operation failed to apply.
     */
    OPERATION_FAILED,

    /**
     *  TERMINAL: The operation description contains an error.
     */
    OPERATION_ERROR,

    /**
     *  TERMINAL: The operation was dropped due to a transient error.
     */
    OPERATION_DROPPED,

    /**
     * The operation affects an agent that has lost contact with the master,
     * typically due to a network failure or partition. The operation may or may
     * not still be pending.
     */
    OPERATION_UNREACHABLE,

    /**
     * The operation affected an agent that the master cannot contact;
     * the operator has asserted that the agent has been shutdown, but this has
     * not been directly confirmed by the master.
     *
     * If the operator is correct, the operation is not pending and this is a
     * terminal state; if the operator is mistaken, the operation may still be
     * pending and might return to a different state in the future.
     */
    OPERATION_GONE_BY_OPERATOR,

    /**
     * The operation affects an agent that the master recovered from its
     * state, but that agent has not yet re-registered.
     *
     * The operation can transition to `OPERATION_UNREACHABLE` if the
     * corresponding agent is marked as unreachable, and will transition to
     * another status if the agent re-registers.
     */
    OPERATION_RECOVERING,

    /**
     * The master has no knowledge of the operation. This is typically
     * because either (a) the master never had knowledge of the operation, or
     * (b) the master forgot about the operation because it garbage collected
     * its metadata about the operation. The operation may or may not still be
     * pending.
     */
    OPERATION_UNKNOWN,

}
