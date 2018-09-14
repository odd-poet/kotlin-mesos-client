package net.oddpoet.mesos.http

import net.oddpoet.mesos.SchedulerAdapter
import net.oddpoet.mesos.SchedulerDriver
import org.junit.Before
import org.junit.Test

/**
 * @author mitchell.geek
 */
class HttpBasedSchedulerDriverDriverTest {


    lateinit var sut: SchedulerDriver

    @Before
    fun setUp() {
        sut = HttpBasedSchedulerDriver()
    }

    @Test
    fun stub() {
        sut.start(SchedulerAdapter())
    }
}