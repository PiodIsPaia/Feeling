package com.github.feeling.src.systens

import com.aallam.openai.api.chat.*
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import kotlin.time.Duration.Companion.seconds

class ChatGpt(private val apiKey: String) {
    suspend fun askQuestion(context: String, question: String): String {

        val config = OpenAIConfig(
            token = apiKey,
            timeout = Timeout(socket = 60.seconds)
        )

        val openAI = OpenAI(config)

        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId("gpt-3.5-turbo"),
            messages = listOf(
                ChatMessage(
                    role = ChatRole.System,
                    content = context,
                ),
                ChatMessage(
                    role = ChatRole.User,
                    content = question
                )
            ),
            maxTokens = 500,
            temperature = 0.8,
        )
        val completion: ChatCompletion = openAI.chatCompletion(chatCompletionRequest)

        val response = completion.choices[0].message.content

        return response as String
    }
}