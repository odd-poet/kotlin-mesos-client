package net.oddpoet.mesos.http.dto

/**
 * A unique ID assigned to a resource provider. Currently, a resource
 * provider gets a new ID whenever it (re)registers with Mesos.
 */
data class ResourceProviderID(val value: String)