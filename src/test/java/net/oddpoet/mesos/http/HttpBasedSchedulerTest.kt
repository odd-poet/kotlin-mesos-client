package net.oddpoet.mesos.http

import net.oddpoet.mesos.Scheduler
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * @author mitchell.geek
 */
class HttpBasedSchedulerTest {


    lateinit var sut: Scheduler

    @Before
    fun setUp() {
        sut = HttpBasedScheduler()
    }

    @Test
    fun stub() {
        sut.start()
    }
}