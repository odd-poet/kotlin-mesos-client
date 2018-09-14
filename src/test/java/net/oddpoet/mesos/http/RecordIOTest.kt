package net.oddpoet.mesos.http

import net.oddpoet.expect.expect
import net.oddpoet.expect.extension.containAll
import net.oddpoet.expect.should
import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException

/**
 * @author Yunsang Choi
 */
class RecordIOTest {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

    @Test
    fun `chunk encoding된 데이터를 chunk단위로 읽을 수 있다`() {
        // Given
        val data = encodeChunks(
                "hello",
                "world"
        )
        log.debug("data: {}", String(data))

        // When
        val sut = RecordIO(data.inputStream())

        // Then
        sut.map { String(it) }.should.containAll("hello", "world")
    }

    @Test
    fun `chunk encodng된 데이터에서 개행문자들을 빼먹지 않고 리턴한다`() {
        // Given
        val data = encodeChunks(
                "hello world\n",
                "\n안녕하세요!\n",
                "   ")
        log.debug("data: {}", String(data))

        // When
        val sut = RecordIO(data.inputStream())

        // Then
        sut.map { String(it) }.should.containAll("hello world\n", "\n안녕하세요!\n", "   ")
    }

    @Test
    fun `잘못된 chunk encoding에 대해서는 예외가 발생한다`() {
        // Given
        val data = "hello world"

        // When
        val sut = RecordIO(data.byteInputStream())

        // Then
        expect {
            sut.toList() // cunsume iterable
        }.throws(IOException::class)
    }


    fun encodeChunks(vararg data: String): ByteArray {
        return data.joinToString(separator = "") { "${it.toByteArray().size}\n$it" }
                .toByteArray()
    }
}