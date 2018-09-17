package net.oddpoet.mesos.http.dto

/**
 * Describes an attribute that can be set on a machine. For now,
 * attributes and resources share the same "value" type, but this may
 * change in the future and attributes may only be string based.
 *
 * ref: https://github.com/apache/mesos/blob/master/include/mesos/v1/mesos.proto#L1144
 */
data class Attribute(
        val name: String,
        val type: Value.Type,
        val scalar: Value.Scalar?,
        val ranges: Value.Ranges?,
        val set: Value.Set?,
        val text: Value.Text?)
