package net.oddpoet.mesos.http.dto

/**
 *
 * @author Yunsang Choi
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

    data class Scalar(val value: Double)

    data class Range(val begin: Long, val end: Long)

    data class Ranges(val range: List<Range>)

    data class Set(val itme: List<String>)

    data class Text(val value: String)
}