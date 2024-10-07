package nl.jaysh.recipe.helper

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.fullPath
import io.ktor.http.headersOf

object MockHttpEngine {
    val successEngine = MockEngine { request ->
        when {
            request.url.fullPath.contains("/recipes/complexSearch") -> {
                val content = loadJson("search.json")
                respond(
                    content = content,
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }

            request.url.fullPath.contains("/information") -> {
                val content = loadJson("information.json")
                respond(
                    content = content,
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }

            else -> error("Error: ${request.url.fullPath}")
        }
    }

    private fun loadJson(fileName: String): String = this.javaClass
        .classLoader
        ?.getResourceAsStream(fileName)
        ?.bufferedReader()
        ?.use { it.readText() }
        ?: throw IllegalArgumentException("$fileName does not exist")
}
