package net.oddpoet.mesos.http.dto

/**
 * Encapsulation for POSIX rlimits, see
 * http://pubs.opengroup.org/onlinepubs/009695399/functions/getrlimit.html.
 * Note that some types might only be defined for Linux.
 * We use a custom prefix to avoid conflict with existing system macros
 * (e.g., `RLIMIT_CPU` or `NOFILE`).
 */
data class RLimitInfo(
        val rlimits: List<RLimit>?) {

    data class RLimit(
            val type: Type?,
            /*
             * Either both are set or both are not set.
             * If both are not set, it represents unlimited.
             * If both are set, we require `soft` <= `hard`.
             */
            val hard: Long,
            val soft: Long) {

        enum class Type {
            UNKNOWN,
            RLMT_AS,
            RLMT_CORE,
            RLMT_CPU,
            RLMT_DATA,
            RLMT_FSIZE,
            RLMT_LOCKS,
            RLMT_MEMLOCK,
            RLMT_MSGQUEUE,
            RLMT_NICE,
            RLMT_NOFILE,
            RLMT_NPROC,
            RLMT_RSS,
            RLMT_RTPRIO,
            RLMT_RTTIME,
            RLMT_SIGPENDING,
            RLMT_STACK,
        }
    }
}
