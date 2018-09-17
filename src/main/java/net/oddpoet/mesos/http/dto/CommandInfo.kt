package net.oddpoet.mesos.http.dto


/**
 * Describes a command, executed via: '/bin/sh -c value'. Any URIs specified
 * are fetched before executing the command.  If the executable field for an
 * uri is set, executable file permission is set on the downloaded file.
 * Otherwise, if the downloaded file has a recognized archive extension
 * (currently [compressed] tar and zip) it is extracted into the executor's
 * working directory. This extraction can be disabled by setting `extract` to
 * false. In addition, any environment variables are set before executing
 * the command (so they can be used to "parameterize" your command).
 */
data class CommandInfo(
        val uris: List<URI>,
        val environment: Environment?,
        /**
         * There are two ways to specify the command:
         *
         * 1) If 'shell == true', the command will be launched via shell
         *          (i.e., /bin/sh -c 'value'). The 'value' specified will be
         *          treated as the shell command. The 'arguments' will be ignored.
         * 2) If 'shell == false', the command will be launched by passing
         *          arguments to an executable. The 'value' specified will be
         *          treated as the filename of the executable. The 'arguments'
         *          will be treated as the arguments to the executable. This is
         *          similar to how POSIX exec families launch processes (i.e.,
         *          execlp(value, arguments(0), arguments(1), ...)).
         *
         * NOTE: The field 'value' is changed from 'required' to 'optional'
         * in 0.20.0. It will only cause issues if a new framework is
         * connecting to an old master.
         */
        val shell: Boolean?,
        val value: String?,
        val arguments: List<String>?,
        /**
         * Enables executor and tasks to run as a specific user. If the user
         * field is present both in FrameworkInfo and here, the CommandInfo
         * user value takes precedence.
         */
        val user: String?) {

    data class URI(
            val value: String,
            val executable: Boolean?,
            /**
             * In case the fetched file is recognized as an archive, extract
             * its contents into the sandbox. Note that a cached archive is
             * not copied from the cache to the sandbox in case extraction
             * originates from an archive in the cache.
             */
            val extract: Boolean?,

            /**
             * If this field is "true", the fetcher cache will be used. If not,
             * fetching bypasses the cache and downloads directly into the
             * sandbox directory, no matter whether a suitable cache file is
             * available or not. The former directs the fetcher to download to
             * the file cache, then copy from there to the sandbox. Subsequent
             * fetch attempts with the same URI will omit downloading and copy
             * from the cache as long as the file is resident there. Cache files
             * may get evicted at any time, which then leads to renewed
             * downloading. See also "docs/fetcher.md" and
             * "docs/fetcher-cache-internals.md".
             */
            val cache: Boolean?,
            /**
             * The fetcher's default behavior is to use the URI string's basename to
             * name the local copy. If this field is provided, the local copy will be
             * named with its value instead. If there is a directory component (which
             * must be a relative path), the local copy will be stored in that
             * subdirectory inside the sandbox.
             */
            val outputFile: String?)


}
