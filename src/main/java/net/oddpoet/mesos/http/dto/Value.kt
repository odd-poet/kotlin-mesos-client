package net.oddpoet.mesos.http.dto

/**
 * Describes an Attribute or Resource "value". A value is described
 * using the standard protocol buffer "union" trick.
 */
class Value(
        val type: Type,
        val scalar: Scalar?,
        val ranges: Ranges?,
        val set: Set?,
        val text: Text?) {

    enum class Type {
        SCALAR,
        RANGES,
        SET,
        TEXT
    }

    data class Scalar(
            /**
             * Scalar values are represented using floating point. To reduce
             * the chance of unpredictable floating point behavior due to
             * roundoff error, Mesos only supports three decimal digits of
             * precision for scalar resource values. That is, floating point
             * values are converted to a fixed point format that supports
             * three decimal digits of precision, and then converted back to
             * floating point on output. Any additional precision in scalar
             * resource values is discarded (via rounding).
             */
            val value: Double)

    data class Range(val begin: Long, val end: Long)

    data class Ranges(val range: List<Range>)

    data class Set(val itme: List<String>)

    data class Text(val value: String)
}