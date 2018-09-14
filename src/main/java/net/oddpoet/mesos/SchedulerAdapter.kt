package net.oddpoet.mesos

import net.oddpoet.mesos.data.MasterInfo
import net.oddpoet.mesos.data.Offer
import net.oddpoet.mesos.data.TaskStatus
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *
 * @author Yunsang Choi
 */
open class SchedulerAdapter : Scheduler {
    protected val log: Logger = LoggerFactory.getLogger(this.javaClass)

    override fun registered(driver: SchedulerDriver, frameworkId: String, masterInfo: MasterInfo?) {
        log.info("registered: $frameworkId, $masterInfo")
    }

    override fun resourceOffers(driver: SchedulerDriver, offers: List<Offer>) {
        log.info("offers: $offers")
    }

    override fun statusUpdated(driver: SchedulerDriver, taskStatus: TaskStatus) {
        log.info("status updated: $taskStatus")
    }

}