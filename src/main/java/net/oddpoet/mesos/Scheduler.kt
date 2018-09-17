package net.oddpoet.mesos

import net.oddpoet.mesos.http.dto.MasterInfo
import net.oddpoet.mesos.http.dto.Offer
import net.oddpoet.mesos.http.dto.TaskStatus

/**
 *
 * @author Yunsang Choi
 */
interface Scheduler {

    fun registered(driver: SchedulerDriver, frameworkId: String, masterInfo: MasterInfo?)

    fun resourceOffers(driver: SchedulerDriver, offers: List<Offer>)

    fun statusUpdated(driver: SchedulerDriver, taskStatus: TaskStatus)

}