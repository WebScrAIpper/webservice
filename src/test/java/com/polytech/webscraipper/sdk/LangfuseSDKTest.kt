package com.polytech.webscraipper.sdk

import com.polytech.webscraipper.sdk.responses.PromptResponse
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class PromptsLiveTest {

 @Autowired
 private lateinit var langfuseSDK: LangfuseSDK

 private val validPromptName = "default-prompt"

 @Test
 fun `should fetch all prompts successfully`() {
  val res = langfuseSDK.prompts.getAll()
  assertNotNull(res)
  assertTrue(res.isNotEmpty(), "Expected non-empty prompt list")
 }

 @Test
 fun `should fetch a specific prompt by Name`() {
  val prompt: PromptResponse = langfuseSDK.prompts.getByName(validPromptName)
  assertNotNull(prompt)
  assertEquals(validPromptName, prompt.name, "Prompt ID should match the requested ID")
 }

 @Test
 fun `variables get nicely extracted from a text`() {
    val text = "Hello {{name}}, how are you doing today? I hope you are {{mood}}."
    val variables = langfuseSDK.prompts.extractVariablesFromText(text)
    assertEquals(2, variables.size, "Expected 2 variables")
    assertTrue(variables.contains("name"), "Expected 'name' variable")
    assertTrue(variables.contains("mood"), "Expected 'mood' variable")
 }

    @Test
    fun `variables get nicely replaced in a text`() {
        val text = "Hello {{name}}, how are you doing today? I hope you are {{mood}}."
        val variables = mapOf("name" to "John", "mood" to "happy")
        val newText = langfuseSDK.prompts.replacesVariablesInText(text, variables)
        assertEquals("Hello John, how are you doing today? I hope you are happy.", newText, "Expected text with replaced variables")
    }

 @Test
 fun `post a trace`() {
  langfuseSDK.traces.postTrace(
   mapOf(
    "name" to "LLM Request",
    "url" to "none",
    "prompt" to "default-prompt",
    "response" to "Hello, how are you doing today?",
    "sessionId" to "Test Session"
   )
  )
 }
}
