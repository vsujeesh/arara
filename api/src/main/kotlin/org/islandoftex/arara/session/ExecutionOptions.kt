// SPDX-License-Identifier: BSD-3-Clause
package org.islandoftex.arara.session

import java.nio.file.Path
import java.nio.file.Paths
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

/**
 * arara's different execution modes. They have to be chosen before a run is
 * initialized.
 */
enum class ExecutionMode {
    /**
     * In dry run mode, arara performs syntax checks of the rules and outputs
     * debug information. No command will be called.
     */
    DRY_RUN,

    /**
     * In safe run mode, arara will take measures not to execute potentially
     * unwanted actions like cascading out of the working directory or
     * executing arbitrary commands.
     * (NIY)
     */
    SAFE_RUN,

    /**
     * In normal run mode, arara executes everything it is able to giving the
     * run the full flexibility.
     */
    NORMAL_RUN
}

@ExperimentalTime
data class ExecutionOptions(
    /**
     * This is the maximum number of loops arara will run for a tool.
     * You cannot allow infinite runs (while true) with this.
     */
    val maxLoops: Int = 10,
    /**
     * After how long a run should time out. Choose 0 milliseconds to
     * indicate you do not want a timeout.
     */
    val timeoutValue: Duration = 0.milliseconds,
    /**
     * Whether arara will halt on errors.
     */
    val haltOnErrors: Boolean = true,
    /**
     * The database name. The database is used to track changes for files
     * in a project. If it is not absolute it will be resolved against the
     * project's working directory.
     */
    val databaseName: Path = Paths.get("arara.yaml"),
    /**
     * Whether arara will output the commands' standard out.
     */
    val verbose: Boolean = false,
    /**
     * arara's current execution mode. This is used to restrict arara's
     * abilities.
     */
    val executionMode: ExecutionMode = ExecutionMode.NORMAL_RUN,
    /**
     * Whether only to parse a contiguous block of comments at the start
     * of a file.
     */
    val parseOnlyHeader: Boolean = false
)
