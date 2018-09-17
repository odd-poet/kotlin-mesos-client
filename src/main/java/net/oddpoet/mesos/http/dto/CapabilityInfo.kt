package net.oddpoet.mesos.http.dto

/**
 * Encapsulation of `Capabilities` supported by Linux.
 * Reference: http://linux.die.net/man/7/capabilities.
 */
data class CapabilityInfo(val capabilities: List<Capability>) {

    /**
     * We start the actual values at an offset(1000) because Protobuf 2
     * uses the first value as the default one. Separating the default
     * value from the real first value helps to disambiguate them. This
     * is especially valuable for backward compatibility.
     * See: MESOS-4997.
     */
    enum class Capability {
        UNKNOWN,
        CHOWN,
        DAC_OVERRIDE,
        DAC_READ_SEARCH,
        FOWNER,
        FSETID,
        KILL,
        SETGID,
        SETUID,
        SETPCAP,
        LINUX_IMMUTABLE,
        NET_BIND_SERVICE,
        NET_BROADCAST,
        NET_ADMIN,
        NET_RAW,
        IPC_LOCK,
        IPC_OWNER,
        SYS_MODULE,
        SYS_RAWIO,
        SYS_CHROOT,
        SYS_PTRACE,
        SYS_PACCT,
        SYS_ADMIN,
        SYS_BOOT,
        SYS_NICE,
        SYS_RESOURCE,
        SYS_TIME,
        SYS_TTY_CONFIG,
        MKNOD,
        LEASE,
        AUDIT_WRITE,
        AUDIT_CONTROL,
        SETFCAP,
        MAC_OVERRIDE,
        MAC_ADMIN,
        SYSLOG,
        WAKE_ALARM,
        BLOCK_SUSPEND,
        AUDIT_READ,
    }
}
