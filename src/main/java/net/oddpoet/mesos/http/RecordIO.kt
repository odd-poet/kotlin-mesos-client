package net.oddpoet.mesos.http

import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset

/**
 * RecordIO data format reader.
 *
 * inputStream을 chunk 단위로 접근할 수 있게 해주는 클래스.
 *
 * 주의:
 *  - 이 클래스는 inputStream을 직접 닫지 않는다.
 *  - inputStream의 마지막까지 읽는다.
 *  - 리턴되는 byteArray들은 record부분만을 가진다. 즉, chunk size는 제거된다.
 *
 * 참고: http://mesos.apache.org/documentation/latest/recordio/
 *
 * @author Yunsang Choi
 */
class RecordIO(private val inputStream: InputStream) : Iterable<ByteArray> {

    override fun iterator(): Iterator<ByteArray> {
        return chunkIterator(inputStream)
    }

    private fun chunkIterator(inputStream: InputStream): Iterator<ByteArray> {
        return object : Iterator<ByteArray> {
            private var prefetch: ByteArray? = null

            override fun hasNext(): Boolean {
                prefetch = prefetch ?: readRecord(inputStream)
                return prefetch!!.isNotEmpty()
            }

            override fun next(): ByteArray {
                val nextChunk = this.prefetch ?: readRecord(inputStream)
                this.prefetch = null
                return nextChunk
            }
        }
    }

    private fun readRecord(inputStream: InputStream): ByteArray {
        val buf = ByteArray(16) // for chunk-size line
        var bufOffset = 0
        while (true) {
            val i = inputStream.read()
            if (i < 0 ) { // EOF
                if (bufOffset > 0) {
                    // wrong encoding!
                    throw IOException("Invalid chunk encoding!")
                } else {
                    return byteArrayOf() // for EOF
                }
            }
            val byte = i.toByte()
            if (byte.isLineFeed) {
                val size = String(buf.copyOfRange(0, bufOffset)).toInt()
                val data = ByteArray(size)
                inputStream.read(data, 0, size)
                return data
            } else {
                buf[bufOffset++] = byte
            }
        }
    }

    private val Byte.isLineFeed: Boolean
        get() = this == '\n'.toByte()

}