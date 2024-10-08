package nl.jaysh.recipe.core.data.network.service

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLProtocol
import io.ktor.http.fullPath
import io.ktor.utils.io.errors.IOException
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import nl.jaysh.recipe.core.data.network.KtorClient
import nl.jaysh.recipe.core.domain.model.failure.Failure
import nl.jaysh.recipe.core.domain.model.failure.NetworkFailure
import nl.jaysh.recipe.core.domain.model.failure.ParseFailure
import nl.jaysh.recipe.core.utils.Constants.API_HOST
import nl.jaysh.recipe.helper.MockHttpEngine
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class RecipeRemoteDataSourceTest {

    private lateinit var remoteDataSource: RecipeRemoteDataSource
    private lateinit var httpClient: HttpClient
    private lateinit var engine: MockEngine

    private val testQuery = "Pasta"
    private val testRecipeId = 23557L

    @BeforeEach
    fun setup() {
        engine = MockHttpEngine.successEngine
        httpClient = KtorClient.createHttpClient(engine)
        remoteDataSource = RecipeRemoteDataSourceImpl(httpClient = httpClient)
    }

    @AfterEach
    fun teardown() {
        engine.close()
    }

    @Test
    fun testSearchRecipesPath() = runTest {
        remoteDataSource.search(query = testQuery)

        verifyHttpRequest { path ->
            assertThat(path).contains("/recipes/complexSearch?query=$testQuery")
            assertThat(path).contains("&addRecipeInformation=true")
            assertThat(path).contains("&apiKey=")
        }
    }

    @Test
    fun testFetchRecipeDetailPath() = runTest {
        remoteDataSource.getDetails(recipeId = testRecipeId)

        verifyHttpRequest { path ->
            assertThat(path).contains("/recipes/$testRecipeId")
            assertThat(path).contains("/information")
            assertThat(path).contains("?apiKey=")
        }
    }

    @Test
    fun testSearchRecipesSuccess() = runTest {
        val response = remoteDataSource.search(query = testQuery)

        val result = response.getOrNull() ?: throw Exception()
        assertThat(result.results.size).isEqualTo(3)
        assertThat(result.offset).isEqualTo(0)
        assertThat(result.number).isEqualTo(3)
        assertThat(result.totalResults).isEqualTo(24)

        val ids = result.results.map { it.id }
        val expectedIds = listOf(640864L, 636581L, 649293L)
        assertThat(ids).isEqualTo(expectedIds)
    }

    @Test
    fun testFetchRecipeDetailSuccess() = runTest {
        val response = remoteDataSource.getDetails(recipeId = testRecipeId)

        val result = response.getOrNull() ?: throw Exception()

        assertThat(result.title).isEqualTo("Crock Pot Lasagna")
        assertThat(result.readyInMinutes).isEqualTo(45)
        assertThat(result.analyzedInstructions.size).isEqualTo(1)
        assertThat(result.analyzedInstructions.first().steps.size).isEqualTo(3)
    }

    @Test
    fun `search serialization error should ParseFailure`() = runTest {
        val jsonObject = JsonObject(mapOf("invalid" to JsonPrimitive("json")))
        val content = Json.encodeToString(jsonObject)

        val engine = MockEngine { respond(content = content, status = HttpStatusCode.OK) }
        val client = KtorClient.createHttpClient(engine)
        val service = RecipeRemoteDataSourceImpl(httpClient = client)

        val response = service.search(query = testQuery)

        assertThat(response.leftOrNull()).isEqualTo(ParseFailure.JsonParse)
    }

    @Test
    fun `getDetail serialization error should ParseFailure`() = runTest {
        val jsonObject = JsonObject(mapOf("invalid" to JsonPrimitive("json")))
        val content = Json.encodeToString(jsonObject)

        val engine = MockEngine { respond(content = content, status = HttpStatusCode.OK) }
        val client = KtorClient.createHttpClient(engine)
        val service = RecipeRemoteDataSourceImpl(httpClient = client)

        val response = service.getDetails(recipeId = testRecipeId)

        assertThat(response.leftOrNull()).isEqualTo(ParseFailure.JsonParse)
    }

    @Test
    fun `IOException should NetworkFailure NO_INTERNET`() = testFailureWithException(
        exception = IOException(),
        expectedFailure = NetworkFailure.NO_INTERNET,
    )

    @Test
    fun `HttpRequestTimeoutException should NetworkFailure TIMEOUT`() = testFailureWithException(
        exception = mockk<HttpRequestTimeoutException>(relaxed = true),
        expectedFailure = NetworkFailure.TIMEOUT,
    )

    @Test
    fun `ConnectTimeoutException should NetworkFailure TIMEOUT`() = testFailureWithException(
        exception = mockk<ConnectTimeoutException>(relaxed = true),
        expectedFailure = NetworkFailure.TIMEOUT,
    )

    @Test
    fun `SocketTimeoutException should NetworkFailure TIMEOUT`() = testFailureWithException(
        exception = mockk<SocketTimeoutException>(relaxed = true),
        expectedFailure = NetworkFailure.TIMEOUT,
    )

    @ParameterizedTest
    @CsvSource("300", "301", "302", "303", "304", "307", "308")
    fun `300 range should NetworkFailure UNKNOWN`(statusCode: Int) = testFailureWithStatusCode(
        statusCode = statusCode,
        expectedFailure = NetworkFailure.UNKNOWN,
    )

    @Test
    fun `401 should NetworkFailure UNAUTHORIZED`() = testFailureWithStatusCode(
        statusCode = 401,
        expectedFailure = NetworkFailure.UNAUTHORIZED,
    )

    @Test
    fun `402 should NetworkFailure PAYMENT_REQUIRED`() = testFailureWithStatusCode(
        statusCode = 402,
        expectedFailure = NetworkFailure.PAYMENT_REQUIRED,
    )

    @Test
    fun `404 should NetworkFailure NOT_FOUND`() = testFailureWithStatusCode(
        statusCode = 404,
        expectedFailure = NetworkFailure.NOT_FOUND,
    )

    @ParameterizedTest
    @CsvSource("403", "405", "408", "409", "429")
    fun `unspecified 400 range should NetworkFailure UNKNOWN`(statusCode: Int) =
        testFailureWithStatusCode(
            statusCode = statusCode,
            expectedFailure = NetworkFailure.UNKNOWN,
        )

    @ParameterizedTest
    @CsvSource("500", "501", "502", "503", "504", "505", "506", "507")
    fun `500 range should NetworkFailure UNKNOWN`(statusCode: Int) = testFailureWithStatusCode(
        statusCode = statusCode,
        expectedFailure = NetworkFailure.UNKNOWN,
    )

    // Helpers
    private fun verifyHttpRequest(expectedPath: (String) -> Unit) {
        // 💩 TODO .last() because engine history is not reset after each test execution 💩
        val requestHistoryUrl = engine.requestHistory.last().url

        val protocol = requestHistoryUrl.protocol
        assertThat(protocol).isEqualTo(URLProtocol.HTTPS)

        val host = requestHistoryUrl.host
        assertThat(host).isEqualTo(API_HOST)

        val path = requestHistoryUrl.fullPath
        expectedPath(path)
    }

    // TODO: code duplication
    private fun testFailureWithStatusCode(statusCode: Int, expectedFailure: Failure) = runTest {
        val service = failingRecipeServiceWithStatusCode(statusCode = statusCode)

        val search = service.search(query = testQuery)
        val detail = service.getDetails(recipeId = testRecipeId)

        assertThat(search.leftOrNull()).isEqualTo(expectedFailure)
        assertThat(detail.leftOrNull()).isEqualTo(expectedFailure)
    }

    // TODO: code duplication
    private fun testFailureWithException(exception: Exception, expectedFailure: Failure) = runTest {
        val engine = MockEngine { _ -> throw exception }
        val client = KtorClient.createHttpClient(engine)
        val service = RecipeRemoteDataSourceImpl(httpClient = client)

        val search = service.search(query = testQuery)
        val detail = service.getDetails(recipeId = testRecipeId)

        assertThat(search.leftOrNull()).isEqualTo(expectedFailure)
        assertThat(detail.leftOrNull()).isEqualTo(expectedFailure)
    }

    private fun failingRecipeServiceWithStatusCode(statusCode: Int): RecipeRemoteDataSourceImpl {
        val status = HttpStatusCode.fromValue(statusCode)
        val engine = MockEngine { respond(content = "", status = status) }
        val client = KtorClient.createHttpClient(engine)
        return RecipeRemoteDataSourceImpl(httpClient = client)
    }
}