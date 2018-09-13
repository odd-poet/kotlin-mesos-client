package net.oddpoet.mesos.http

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.InputStream

/**
 * RecordIO data format reader.
 *
 * chunk encoding된 inputStream을 chunk 단위로 읽는 기능을 제공한다.
 *
 * 참고: http://mesos.apache.org/documentation/latest/recordio/
 *
 * @author Yunsang Choi
 */
class RecordIoDataReader(private val inputStream: InputStream) {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

    fun forEach(handler: (data: ByteArray) -> Unit) {
        val safeHandler = ignoreException(handler)
        do {
            val chunk = readChunk()
                    .takeIf { it.isNotEmpty() } ?: break
            safeHandler(chunk)
        } while (true)
    }

    private fun readChunk(): ByteArray {
        val buf = ByteArray(16) // for chunk-size line
        var bufOffset = 0
        while (true) {
            val i = inputStream.read()
            if (i < 0) {
                return byteArrayOf()
            }
            val byte = i.toByte()
            if (byte.isLF) {
                val size = String(buf.copyOfRange(0, bufOffset)).toInt()
                bufOffset = 0
                val data = ByteArray(size)
                inputStream.read(data, 0, size)
                return data
            } else {
                buf[bufOffset++] = byte
            }
        }
    }

    private val Byte.isLF: Boolean
        get() = this == '\n'.toByte()


    private fun ignoreException(handler: (ByteArray) -> Unit): (ByteArray) -> Unit {
        return { data: ByteArray ->
            try {
                handler(data)
            } catch (e: Exception) {
                log.error("error occurred!", e)
            }
        }
    }

}