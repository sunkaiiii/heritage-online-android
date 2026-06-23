package com.duckylife.heritage.modern.core.network

import org.junit.Assert.assertEquals
import org.junit.Test

class HeritageApiConfigTest {

    @Test
    fun `release allows valid https config`() {
        val config = HeritageApiConfig(
            baseUrl = "https://example.com",
            trustSelfSignedCertificates = false,
            requireHttpsForRelease = true,
        )
        assertEquals("https://example.com", config.baseUrl)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `release rejects http base url`() {
        HeritageApiConfig(
            baseUrl = "http://example.com",
            trustSelfSignedCertificates = false,
            requireHttpsForRelease = true,
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `release rejects trust self signed certificates`() {
        HeritageApiConfig(
            baseUrl = "https://example.com",
            trustSelfSignedCertificates = true,
            requireHttpsForRelease = true,
        )
    }

    @Test
    fun `debug allows http config when not requiring https`() {
        HeritageApiConfig(
            baseUrl = "http://10.0.2.2:5078",
            trustSelfSignedCertificates = true,
            requireHttpsForRelease = false,
        )
    }
}
