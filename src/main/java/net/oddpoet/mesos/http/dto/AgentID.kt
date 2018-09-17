package net.oddpoet.mesos.http.dto

/**
 * A unique ID assigned to an agent. Currently, an agent gets a new ID
 * whenever it (re)registers with Mesos. Framework writers shouldn't
 * assume any binding between an agent ID and and a hostname.
 */
data class AgentID(val value: String)