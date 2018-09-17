package net.oddpoet.mesos.http.dto

import kotlin.reflect.jvm.internal.impl.protobuf.ByteString

/**
 * Secret used to pass privileged information. It is designed to provide
 * pass-by-value or pass-by-reference semantics, where the REFERENCE type can be
 * used by custom modules which interact with a secure back-end.
 */
data class Secret(
        val type: Type?,
        val reference: Reference?,
        val value: Value?) {

    enum class Type {
        UNKNOWN,
        REFERENCE,
        VALUE
    }


    /**
     * Can be used by modules to refer to a secret stored in a secure back-end.
     * The `key` field is provided to permit reference to a single value within a
     * secret containing arbitrary key-value pairs.
     *
     * For example, given a back-end secret store with a secret named
     * "my-secret" containing the following key-value pairs:
     *
     * ```
     *      {
     *          "username": "my-user",
     *          "password": "my-password
     *      }
     * ```
     * the username could be referred to in a `Secret` by specifying
     * "my-secret" for the `name` and "username" for the `key`.
     */
    data class Reference(
            val name: String,
            val key: String?)

    /**
     * Used to pass the value of a secret.
     */
    data class Value(val data: ByteString)
}
