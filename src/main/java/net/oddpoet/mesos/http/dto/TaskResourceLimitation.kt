package net.oddpoet.mesos.http.dto

/**
 * Describes a resource limitation that caused a task failure.
 */
data class TaskResourceLimitation(
        /**
         * This field contains the resource whose limits were violated.
         *
         * NOTE: 'Resources' is used here because the resource may span
         * multiple roles (e.g. `"mem(*):1;mem(role):2"`).
         */
        val resources: List<Resource>?
)
