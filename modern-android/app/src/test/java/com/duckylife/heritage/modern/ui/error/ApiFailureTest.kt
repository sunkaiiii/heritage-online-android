package com.duckylife.heritage.modern.ui.error

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ApiFailureTest {

    @Test
    fun `parses 400 problem details into ApiFailure Http`() = runTest {
        val exception = createClientException(
            status = HttpStatusCode.BadRequest,
            body = """
                {
                    "type": "https://example.com/errors/invalid-request",
                    "title": "Invalid request",
                    "status": 400,
                    "detail": "Missing required parameter 'profileId'",
                    "traceId": "trace-400"
                }
            """.trimIndent(),
        )

        val failure = exception.toApiFailure()

        assertTrue(failure is ApiFailure.Http)
        val http = failure as ApiFailure.Http
        assertEquals(400, http.statusCode)
        assertEquals("Invalid request", http.problemTitle)
        assertEquals("Missing required parameter 'profileId'", http.detail)
        assertEquals("trace-400", http.traceId)
        assertEquals(ErrorKind.BadRequest, http.toErrorKind())
    }

    @Test
    fun `parses 404 without body into ApiFailure Http`() = runTest {
        val exception = createClientException(
            status = HttpStatusCode.NotFound,
            body = "",
            traceHeader = "trace-404",
        )

        val failure = exception.toApiFailure()

        assertTrue(failure is ApiFailure.Http)
        val http = failure as ApiFailure.Http
        assertEquals(404, http.statusCode)
        assertNull(http.problemTitle)
        assertNull(http.detail)
        assertEquals("trace-404", http.traceId)
        assertEquals(ErrorKind.NotFound, http.toErrorKind())
        assertFalse(http.isRetryable)
    }

    @Test
    fun `parses 503 into retryable ApiFailure Http`() = runTest {
        val exception = createClientException(
            status = HttpStatusCode.ServiceUnavailable,
            body = """
                {"title": "Neo4j unavailable", "status": 503}
            """.trimIndent(),
        )

        val failure = exception.toApiFailure()

        assertTrue(failure is ApiFailure.Http)
        val http = failure as ApiFailure.Http
        assertEquals(503, http.statusCode)
        assertEquals("Neo4j unavailable", http.problemTitle)
        assertEquals(ErrorKind.ServerError, http.toErrorKind())
        assertTrue(http.isRetryable)
    }

    @Test
    fun `maps 429 to TooManyRequests and retryable`() = runTest {
        val exception = createClientException(HttpStatusCode.TooManyRequests)

        val failure = exception.toApiFailure()

        assertEquals(ErrorKind.TooManyRequests, failure.toErrorKind())
        assertTrue((failure as ApiFailure.Http).isRetryable)
    }

    @Test
    fun `maps network exception to Network failure`() = runTest {
        val unknownHost = UnknownHostException("no address")
        val connect = ConnectException()

        assertTrue(unknownHost.toApiFailure() is ApiFailure.Network)
        assertTrue(connect.toApiFailure() is ApiFailure.Network)
        assertEquals(ErrorKind.NetworkUnavailable, unknownHost.toApiFailure().toErrorKind())
        assertTrue(unknownHost.toApiFailure().isRetryable)
    }

    @Test
    fun `maps socket timeout to Timeout failure`() = runTest {
        val timeout = SocketTimeoutException()

        val failure = timeout.toApiFailure()

        assertTrue(failure is ApiFailure.Timeout)
        assertEquals(ErrorKind.Timeout, failure.toErrorKind())
        assertTrue(failure.isRetryable)
    }

    @Test
    fun `maps generic exception to Unknown failure`() = runTest {
        val failure = IllegalStateException("boom").toApiFailure()

        assertTrue(failure is ApiFailure.Unknown)
        assertEquals(ErrorKind.Unknown, failure.toErrorKind())
        assertFalse(failure.isRetryable)
    }

    @Test
    fun `toUiErrorMessage uses correct string resource`() = runTest {
        val failure = createClientException(HttpStatusCode.NotFound).toApiFailure()

        val message = failure.toUiErrorMessage()

        assertEquals(ErrorKind.NotFound, message.kind)
        assertTrue(message.fallbackResId != 0)
    }

    private suspend fun createClientException(
        status: HttpStatusCode,
        body: String = "{}",
        traceHeader: String? = null,
    ): ResponseException {
        val headerPairs: Array<Pair<String, List<String>>> = buildList {
            add(HttpHeaders.ContentType to listOf("application/json"))
            if (traceHeader != null) {
                add("X-Trace-Id" to listOf(traceHeader))
            }
        }.toTypedArray()
        val mockEngine = MockEngine { _ ->
            respond(
                content = body,
                status = status,
                headers = headersOf(*headerPairs),
            )
        }
        val client = HttpClient(mockEngine) { expectSuccess = true }
        return try {
            client.get("/test")
            error("Expected exception")
        } catch (e: ResponseException) {
            e
        } finally {
            client.close()
        }
    }
}
