package com.banana

import io.micronaut.context.ApplicationContext
import kotlinx.coroutines.runBlocking
import java.util.Base64
import java.nio.file.Files
import java.nio.file.Paths

fun main() {
    val context = ApplicationContext.run()
    val geminiClient = context.getBean(GeminiClient::class.java)

    // 🔹 Caminho da imagem que será enviada
    val imagePath = "images/garrafa.jpg" // ⚠ Substitua pelo caminho real da sua imagem!

    // 🔹 Converte a imagem para Base64
    val base64Image = encodeImageToBase64(imagePath)

    runBlocking {
        // ✅ Teste 1: Detecção do Material do Objeto
        testGemini(geminiClient, "main_material.json", mapOf("BASE64_IMAGE" to base64Image))

        // ✅ Teste 2: Detecção do Objeto Principal
        testGemini(geminiClient, "main_object.json", mapOf("BASE64_IMAGE" to base64Image))

        // ✅ Teste 3: Subclassificação do Objeto
        testGemini(geminiClient, "sub_classifications.json", mapOf("PLACEHOLDER" to "garrafa"))

        // ✅ Teste 4: Instruções de Descarte
        testGemini(geminiClient, "disposal_instructions.json", mapOf("PLACEHOLDER" to "garrafa"))

        // ✅ Teste 5: Classificação do Objeto
        testGemini(geminiClient, "classification.json", mapOf("PLACEHOLDER" to "garrafa"))

        // ✅ Teste 6: Local de Descarte
        val isDisposalPlace = detectDisposalPlaceGemini(geminiClient, base64Image)
        println("🔍 O objeto pode ser descartado neste local? $isDisposalPlace")
    }

    context.close()
}

// ✅ Função para converter imagem em Base64
fun encodeImageToBase64(imagePath: String): String {
    val imageBytes = Files.readAllBytes(Paths.get(imagePath))
    return Base64.getEncoder().encodeToString(imageBytes)
}

// ✅ Testa um prompt específico no Gemini
suspend fun testGemini(geminiClient: GeminiClient, promptFile: String, replacements: Map<String, String>) {
    val promptPathPrefix = "classpath:prompts/gemini_" // ⚠ Certifique-se de que este caminho está correto!
    val fullPromptPath = promptPathPrefix + promptFile

    println("\n🔹 Testando prompt: $fullPromptPath")

    val response = geminiClient.getChatCompletion(fullPromptPath, replacements)
    println("✅ Resposta do Gemini:\n$response")
}

// ✅ Testa se um local pode ser um ponto de descarte
suspend fun detectDisposalPlaceGemini(geminiClient: GeminiClient, base64Image: String): Boolean {
    val promptPathPrefix = "classpath:prompts/gemini_"
    val promptPath = promptPathPrefix + "detect_disposal_place.json"
    val replacements = mapOf("BASE64_IMAGE" to base64Image)

    val response = geminiClient.getChatCompletion(promptPath, replacements)
    return response.contains("yes", ignoreCase = true)
}
