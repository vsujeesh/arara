// SPDX-License-Identifier: BSD-3-Clause
package org.islandoftex.arara.core.session

import java.nio.file.Path
import java.nio.file.Paths
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds
import org.islandoftex.arara.api.files.FileType
import org.islandoftex.arara.api.session.ExecutionMode
import org.islandoftex.arara.api.session.ExecutionOptions
import org.islandoftex.arara.core.configuration.ConfigurationUtils

@ExperimentalTime
data class ExecutionOptions(
    override val maxLoops: Int = 10,
    override val timeoutValue: Duration = 0.milliseconds,
    override val parallelExecution: Boolean = true,
    override val haltOnErrors: Boolean = true,
    override val databaseName: Path = Paths.get("arara.yaml"),
    override val verbose: Boolean = false,
    override val executionMode: ExecutionMode = ExecutionMode.NORMAL_RUN,
    override val rulePaths: Set<Path> = setOf(),
    override val fileTypes: List<FileType> = ConfigurationUtils.defaultFileTypes,
    override val parseOnlyHeader: Boolean = false
) : ExecutionOptions {
    companion object {
        /**
         * As the interface does not have a copy method, we provide this
         * conversion method.
         */
        fun from(options: ExecutionOptions):
                org.islandoftex.arara.core.session.ExecutionOptions {
            return ExecutionOptions(
                    maxLoops = options.maxLoops,
                    timeoutValue = options.timeoutValue,
                    parallelExecution = options.parallelExecution,
                    haltOnErrors = options.haltOnErrors,
                    databaseName = options.databaseName,
                    verbose = options.verbose,
                    executionMode = options.executionMode,
                    rulePaths = options.rulePaths,
                    fileTypes = options.fileTypes,
                    parseOnlyHeader = options.parseOnlyHeader
            )
        }
    }
}
