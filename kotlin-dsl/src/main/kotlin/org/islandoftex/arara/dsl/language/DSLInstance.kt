// SPDX-License-Identifier: BSD-3-Clause
package org.islandoftex.arara.dsl.language

import org.islandoftex.arara.api.files.Project

/**
 * An object holding all public members that are available as top-level
 * objects in DSL files.
 */
object DSLInstance {
    /**
     * The DSL's main task is to provide an easy project configuration format.
     * All projects obtained from build configuration files are collected here.
     */
    internal val projects = mutableListOf<Project>()

    /**
     * Rules are specified in the DSL as well. This saves all rules.
     */
    internal val rules = mutableListOf<DSLRule>()

    /**
     * The DSL's way to specify a project.
     *
     * @param name The project's name.
     * @param configure Configure the project by applying methods and
     *   properties.
     * @return The configured project.
     */
    fun project(name: String, configure: DSLProject.() -> Unit): Project =
            DSLProject(name).apply(configure).toProject().also {
                projects.add(it)
            }

    /**
     * The DSL's way to specify a rule.
     *
     * @param id The rule's identifier.
     * @param label An optional more expressive label.
     * @param description An optional explanation as description.
     * @param authors A list of authors.
     * @param configure Configure the rules by applying methods and properties.
     * @return A configured DSLRule
     */
    // TODO: return a rule instead.
    fun rule(
        id: String,
        label: String = "",
        description: String = "",
        authors: List<String> = listOf(),
        configure: DSLRule.() -> Unit
    ): DSLRule = DSLRule(id).apply(configure).also {
        rules.add(it)
    }
}
