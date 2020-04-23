// SPDX-License-Identifier: BSD-3-Clause
package org.islandoftex.arara.cli.configuration

import com.uchuhimo.konf.ConfigSpec
import java.nio.file.Paths
import org.islandoftex.arara.api.files.FileType
import org.islandoftex.arara.api.files.Project
import org.islandoftex.arara.api.session.ExecutionOptions
import org.islandoftex.arara.api.session.LoggingOptions
import org.islandoftex.arara.api.session.UserInterfaceOptions
import org.islandoftex.arara.cli.localization.Language
import org.islandoftex.arara.cli.model.ProjectFile
import org.islandoftex.arara.cli.model.UNKNOWN_TYPE

/**
 * Configuration hierarchy for arara
 *
 * @author Island of TeX
 * @version 5.0
 * @since 5.0
 */
@Suppress("MagicNumber")
object AraraSpec : ConfigSpec() {
    val version by optional(AraraSpec::class.java.`package`.implementationVersion
            ?: "DEVELOPMENT BUILD")

    val executionOptions by optional<ExecutionOptions>(
            org.islandoftex.arara.core.session.ExecutionOptions()
    )
    val loggingOptions by optional<LoggingOptions>(
            org.islandoftex.arara.core.session.LoggingOptions()
    )
    val userInterfaceOptions by optional<UserInterfaceOptions>(
            org.islandoftex.arara.core.session.UserInterfaceOptions()
    )

    object Execution : ConfigSpec() {
        val verbose by lazy { it[executionOptions].verbose }
        val language by lazy { Language(it[userInterfaceOptions].languageCode) }
        val exitCode by optional(0)
        val rulePaths by lazy {
            it[executionOptions].rulePaths.plus(
                    ConfigurationUtils
                            .applicationPath.resolve("rules")
            )
        }

        // TODO: this is a runtime value which should be properly
        // initialized and tested (maybe move it into its own
        // Spec or session)
        val reference by optional<org.islandoftex.arara.api.files.ProjectFile>(
                ProjectFile(Paths.get("/tmp/"), FileType.UNKNOWN_TYPE)
        )
        val currentProject by optional<Project>(
                org.islandoftex.arara.core.files.Project("", Paths.get(""), setOf())
        )
    }
}
