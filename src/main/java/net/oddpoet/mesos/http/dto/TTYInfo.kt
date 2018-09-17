package net.oddpoet.mesos.http.dto


/**
 * Describes the information about (pseudo) TTY that can
 * be attached to a process running in a container.
 */
data class TTYInfo(val windowSize: WindowSize?) {
    data class WindowSize(val rows: Int, val columns: Int)
}
