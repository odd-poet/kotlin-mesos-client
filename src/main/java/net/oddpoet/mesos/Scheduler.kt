package net.oddpoet.mesos

import net.oddpoet.mesos.data.MasterInfo
import net.oddpoet.mesos.data.Offer
import net.oddpoet.mesos.data.TaskStatus

/**
 *
 * @author Yunsang Choi
 */
interface Scheduler {

    fun registered(driver: SchedulerDriver, frameworkId: String, masterInfo: MasterInfo?)

    fun resourceOffers(driver: SchedulerDriver, offers: List<Offer>)

    fun statusUpdated(driver: SchedulerDriver, taskStatus: TaskStatus)

}