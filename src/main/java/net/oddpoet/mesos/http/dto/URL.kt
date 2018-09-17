package net.oddpoet.mesos.http.dto

data class URL(
        val scheme: String,
        val address: Address,
        val path: String?,
        val query: List<Parameter>?,
        val fragment: String?
)
