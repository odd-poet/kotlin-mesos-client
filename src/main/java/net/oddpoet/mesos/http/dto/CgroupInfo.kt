package net.oddpoet.mesos.http.dto

/**
 * Linux control group (cgroup) information.
 */
data class CgroupInfo(val netCls: NetCls?) {

    /**
     * Configuration of a blkio cgroup subsystem.
     */
    class Blkio {

        enum class Operation {
            UNKNOWN,
            TOTAL,
            READ,
            WRITE,
            SYNC,
            ASYNC
        }

        /**
         * Describes a stat value without the device descriptor part.
         */
        data class Value(
                val op: Operation?, // Required.
                val value: Long?)   // Required.

        class CFQ {
            data class Statistics(
                    /**
                     * Stats are grouped by block devices. If `device` is not
                     * set, it represents `Total`.
                     */
                    val device: Device.Number?,
                    val sectors: Long?,
                    val time: Long?,
                    val ioServiced: List<Value>?,
                    val ioServiceBytes: List<Value>?,
                    val ioServiceTime: List<Value>?,
                    val ioWaitTime: List<Value>?,
                    val ioMerged: List<Value>?,
                    val ioQueued: List<Value>?)
        }

        class Throttling {
            data class Statistics(
                    val device: Device.Number?,
                    val ioServiced: List<Value>?,
                    val ioServiceBytes: List<Value>?)
        }

        data class Statistics(
                val cfq: List<CFQ.Statistics>?,
                val cfqRecursive: List<CFQ.Statistics>?,
                val throttling: List<Throttling.Statistics>?)

    }

    /**
     * Configuration of a net_cls cgroup subsystem.
     */
    data class NetCls(
            /**
             * The 32-bit classid consists of two parts, a 16 bit major handle
             * and a 16-bit minor handle. The major and minor handle are
             * represented using the format 0xAAAABBBB, where 0xAAAA is the
             * 16-bit major handle and 0xBBBB is the 16-bit minor handle.
             */
            val classid: Int?)

}
