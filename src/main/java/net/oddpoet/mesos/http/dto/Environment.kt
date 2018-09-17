package net.oddpoet.mesos.http.dto

/**
 * Describes a collection of environment variables. This is used with
 * CommandInfo in order to set environment variables before running a
 * command. The contents of each variable may be specified as a string
 * or a Secret; only one of `value` and `secret` must be set.
 *
 * ref: https://github.com/apache/mesos/blob/master/include/mesos/v1/mesos.proto#L2580
 */
data class Environment(val variables: List<Variable> = listOf()) {
    data class Variable(
            val name: String,
            /**
             * In Mesos 1.2, the `Environment.variables.value` message was made
             * optional. The default type for `Environment.variables.type` is now VALUE,
             * which requires `value` to be set, maintaining backward compatibility.
             *
             * TODO(greggomann): The default can be removed in Mesos 2.1 (MESOS-7134).
             */
            val type: Type? = Type.VALUE,
            val value: String?,
            val secret: Secret?) {

        enum class Type {
            UNKNOWN,
            VALUE,
            SECRET
        }
    }
}
