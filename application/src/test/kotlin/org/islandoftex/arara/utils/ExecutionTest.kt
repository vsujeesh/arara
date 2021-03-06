// SPDX-License-Identifier: BSD-3-Clause
package org.islandoftex.arara.utils

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.DoNotParallelize
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream
import java.nio.file.Paths
import kotlin.time.ExperimentalTime
import org.islandoftex.arara.Arara
import org.islandoftex.arara.configuration.AraraSpec
import org.islandoftex.arara.configuration.Configuration
import org.islandoftex.arara.filehandling.FileSearchingUtils
import org.islandoftex.arara.model.AraraException
import org.islandoftex.arara.model.Extractor
import org.islandoftex.arara.model.Interpreter
import org.islandoftex.arara.ruleset.DirectiveUtils

@ExperimentalTime
@DoNotParallelize
class ExecutionTest : ShouldSpec({
    fun getPathForTest(name: String): String = "src/test/resources/executiontests/$name"
    fun outputForTest(testName: String, fileName: String = "$testName.tex"):
            String {
        val sysout = System.out
        val output = ByteArrayOutputStream()
        try {
            System.setOut(PrintStream(output))
            Arara.config[AraraSpec.Execution.workingDirectory] =
                    Paths.get(getPathForTest(testName))
            Configuration.load()
            Arara.config[AraraSpec.Execution.verbose] = true
            FileSearchingUtils.registerFileAttributes(
                    FileSearchingUtils.resolveFile(fileName,
                            File(getPathForTest(testName)))
            )
            val directives = DirectiveUtils.process(Extractor.extract(
                    File("${getPathForTest(testName)}/$fileName")))
            Interpreter(directives).execute()
            return output.toByteArray().toString(Charsets.UTF_8)
        } catch (ex: Exception) {
            throw ex
        } finally {
            System.setOut(sysout)
            output.close()
        }
    }

    should("be able to store variables sessions") {
        outputForTest("sessions") shouldContain "MeowQuack"
    }

    should("honor falsy existence test") {
        val file = File(getPathForTest("conditionals") + "/conditionals.quack")
        if (file.exists()) file.delete()
        val output = outputForTest("conditionals")
        output shouldContain "QuackOne"
        output shouldContain "QuackFour"
        output shouldNotContain "QuackTwo"
        output shouldNotContain "QuackThree"
    }
    should("honor truthy existence (falsy contains) test") {
        val file = File(getPathForTest("conditionals") + "/conditionals.quack")
        file.writeText("Meow")
        val output = outputForTest("conditionals")
        file.delete()
        output shouldContain "QuackOne"
        output shouldContain "QuackFour"
        output shouldContain "QuackTwo"
        output shouldNotContain "QuackThree"
    }
    should("honor truthy contains test") {
        val file = File(getPathForTest("conditionals") + "/conditionals.quack")
        file.writeText("Duck")
        val output = outputForTest("conditionals")
        file.delete()
        output shouldContain "QuackOne"
        output shouldContain "QuackFour"
        output shouldContain "QuackTwo"
        output shouldContain "QuackThree"
    }

    should("track changes") {
        val file = File(getPathForTest("changes") + "/arara.yaml")
        if (file.exists()) file.delete()
        outputForTest("changes") shouldContain "QuackOne"
        outputForTest("changes") shouldNotContain "QuackOne"
    }

    should("gracefully halt on halt rule") {
        val output = outputForTest("halt")
        output shouldContain "QuackOne"
        output shouldNotContain "QuackTwo"
        CommonUtils.exitStatus shouldBe 0
    }
    should("forcefully halt on halt error rule") {
        val output = outputForTest("halt-error")
        output shouldContain "QuackOne"
        output shouldNotContain "QuackTwo"
        CommonUtils.exitStatus shouldNotBe 0
    }

    should("fail on invalid config") {
        val exception = shouldThrow<AraraException> {
            outputForTest("invalid-config")
        }
        exception.message shouldContain "could not parse the configuration"
    }

    should("read foreign extension") {
        val output = outputForTest("foreign-extension", "foreign-extension.my")
        output shouldContain "QuackOne"
    }
    should("fail on unknown extension") {
        shouldThrow<AraraException> {
            outputForTest("foreign-extension", "foreign-extension.xy")
        }
    }
    should("accept empty pattern on known extension") {
        val output = outputForTest("known-extension")
        output shouldContain "QuackOne"
    }

    should("execute option-less directives") {
        val output = outputForTest("simple-directive")
        output shouldContain "The echoer"
        output shouldContain "SUCCESS"
    }
    should("execute directive with options") {
        val output = outputForTest("directive-with-options")
        output shouldContain "The echoer"
        output shouldContain "batchmode"
        output shouldContain "SUCCESS"
        output shouldNotContain "FAILURE"
    }
})
