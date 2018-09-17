package net.oddpoet.mesos.http.dto

/**
 * Collection of labels. Labels should not contain duplicate key-value
 * pairs.
 */
data class Labels(val labels: List<Label>)