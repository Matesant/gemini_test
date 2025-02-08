package com.banana

import io.micronaut.core.io.ResourceResolver
import org.apache.commons.io.IOUtils
import java.nio.charset.StandardCharsets
import java.io.BufferedReader
import java.io.InputStreamReader

class JsonHelper {
    companion object {
        fun getJsonContentFromPath(jsonPath: String, fieldToReplace: String, parameter: String): String {
            val jsonStream = ResourceResolver().getResourceAsStream(jsonPath)

            return IOUtils.toString(jsonStream.get(), StandardCharsets.UTF_8).replace(fieldToReplace, parameter)
        }

        fun getJsonContentFromPath(jsonPath: String, replacements: Map<String, String>): String {
            val jsonStream = ResourceResolver().getResourceAsStream(jsonPath)
                ?: throw IllegalArgumentException("Prompt file not found: $jsonPath")

            val content = BufferedReader(InputStreamReader(jsonStream.get(), StandardCharsets.UTF_8)).use { it.readText() }

            var processedContent = content
            replacements.forEach { (key, value) ->
                processedContent = processedContent.replace("$key", value)
            }

            println(processedContent)
            return processedContent
        }

    }

}