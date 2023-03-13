package org.n27.tado.data.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.buffer
import okio.source
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.n27.test.generators.getAccountDetails
import org.n27.test.generators.getOverlay
import org.n27.test.generators.getZoneState
import org.n27.test.generators.getZones
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.IOException
import java.nio.charset.StandardCharsets

class TadoApiTest {

    private lateinit var service: TadoApi
    private lateinit var mockWebServer: MockWebServer

    @Before
    fun init() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        service = Retrofit.Builder().client(OkHttpClient.Builder().build())
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(
                MoshiConverterFactory.create(
                    Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                )
            )
            .build().create(TadoApi::class.java)
    }

    @Test
    fun getAccountDetailsTest() = runBlocking {
        val accountDetails = getAccountDetails()

        enqueueResponse("account-details.json")

        val response = service.getAccountDetails("token")

        assertEquals(response, accountDetails)
    }

    @Test
    fun getZonesTest() = runBlocking {
        val zones = getZones()

        enqueueResponse("zones.json")

        val response = service.getZones("token", 1234)

        assertEquals(response, zones)
    }

    @Test
    fun getZoneStateTest() = runBlocking {
        val zoneState = getZoneState()

        enqueueResponse("zone-state.json")

        val response = service.getZoneState("token", 1234, 1)

        assertEquals(response, zoneState)
    }

    @Test
    fun sendOrder() = runBlocking {
        val overlay = getOverlay()

        enqueueResponse("overlay.json")

        val response = service.sendOrder("token", 1234, 1, overlay)

        assertEquals(response, overlay)
    }

    private fun enqueueResponse(resource: String) {
        val inputStream = javaClass.classLoader!!.getResourceAsStream(resource)
        val source = inputStream.source().buffer()
        val mockResponse = MockResponse()
        mockWebServer.enqueue(mockResponse.setBody(source.readString(StandardCharsets.UTF_8)))
    }

    @After
    @Throws(IOException::class)
    fun teardown() { mockWebServer.shutdown() }
}
