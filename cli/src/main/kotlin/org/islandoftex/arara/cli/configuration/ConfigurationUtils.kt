// SPDX-License-Identifier: BSD-3-Clause
package org.islandoftex.arara.cli.configuration

import com.charleskorn.kaml.Yaml
import java.io.File
import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import org.islandoftex.arara.Arara
import org.islandoftex.arara.api.AraraException
import org.islandoftex.arara.core.localization.LanguageController
import org.islandoftex.arara.core.session.Environment
import org.islandoftex.arara.mvel.configuration.LocalConfiguration

/**
 * Implements configuration utilitary methods.
 *
 * @author Island of TeX
 * @version 5.0
 * @since 4.0
 */
object ConfigurationUtils {
    /**
     * The configuration file in use.
     *
     * Look for configuration files in the user's working directory first
     * if no configuration files are found in the user's working directory,
     * try to look up in a global directory, that is, the user home.
     */
    val configFile: Path?
        get() {
            val names = listOf(".araraconfig.yaml",
                    "araraconfig.yaml", ".arararc.yaml", "arararc.yaml")
            Arara.config[AraraSpec.Execution.currentProject].workingDirectory
                    .let { workingDir ->
                        val first = names
                                .map { workingDir.resolve(it) }
                                .firstOrNull { Files.exists(it) }
                        if (first != null)
                            return first
                    }
            return Environment.getSystemPropertyOrNull("user.home")
                    ?.let { userHome ->
                        names.map { Paths.get(userHome).resolve(it) }
                                .firstOrNull { Files.exists(it) }
                    }
        }

    /**
     * The canonical absolute application path.
     *
     * @throws AraraException Something wrong happened, to be caught in the
     * higher levels.
     */
    val applicationPath: Path
        @Throws(AraraException::class)
        get() {
            try {
                var path = Arara::class.java.protectionDomain.codeSource
                        .location.path
                path = URLDecoder.decode(path, "UTF-8")
                return Paths.get(File(path).toURI()).parent.toAbsolutePath()
            } catch (exception: UnsupportedEncodingException) {
                throw AraraException(
                        LanguageController.messages.ERROR_GETAPPLICATIONPATH_ENCODING_EXCEPTION,
                        exception
                )
            }
        }

    /**
     * Validates the configuration file.
     *
     * @param file The configuration file.
     * @return The configuration file as a resource.
     * @throws AraraException Something wrong happened, to be caught in the
     * higher levels.
     */
    @Throws(AraraException::class)
    fun loadLocalConfiguration(file: Path): LocalConfiguration {
        return file.runCatching {
            val text = file.toFile().readText()
            if (!text.startsWith("!config"))
                throw Exception("Configuration should start with !config")
            Yaml.default.parse(LocalConfiguration.serializer(),
                    text)
        }.getOrElse {
            throw AraraException(
                    LanguageController.messages.ERROR_CONFIGURATION_GENERIC_ERROR,
                    it
            )
        }
    }

    /**
     * Cleans the file name to avoid invalid entries.
     *
     * @param name The file name.
     * @return A cleaned file name.
     */
    fun cleanFileName(name: String): String {
        val result = File(name).name.trim()
        return if (result.isEmpty()) "arara" else result.trim()
    }
}
