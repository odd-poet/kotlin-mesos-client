package net.oddpoet.mesos.http.dto

import java.util.*

data class UUID(val value: String) {

    val asBytes: ByteArray?
        get() = Base64.getDecoder().decode(value)
}

