package net.oddpoet.mesos.http.dto

data class Unavailability(
        val start: TimeInfo,
        val duration: DurationInfo?)