package org.n27.tado.data

import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.n27.tado.data.api.TadoApi
import org.n27.tado.data.api.TadoAuth
import org.n27.tado.data.room.AcConfigDao
import org.n27.test.generators.*
import org.robolectric.RobolectricTestRunner

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class TadoRepositoryTest {

    private lateinit var repository: TadoRepository
    private lateinit var authService: TadoAuth
    private lateinit var service: TadoApi
    private lateinit var dao: AcConfigDao
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        authService = mock(TadoAuth::class.java)
        service = mock(TadoApi::class.java)
        dao = mock(AcConfigDao::class.java)
        repository = TadoRepository(authService, service, dao)
        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun login() = runTest {
        val login = getLoginResponse()

        `when`(authService.login("u", "p")).thenReturn(login)

        assertEquals(repository.login("u", "p"), login)
    }

    @Test
    fun getAccountDetailsTest() = runTest {
        val accountDetails = getAccountDetails()

        `when`(service.getAccountDetails("token")).thenReturn(accountDetails)

        assertEquals(repository.getAccountDetails("token"), accountDetails)
    }

    @Test
    fun getZonesTest() = runTest {
        val zones = getZones()

        `when`(service.getZones("token", 1234)).thenReturn(zones)

        assertEquals(repository.getZones("token", 1234), zones)
    }

    @Test
    fun getZoneStatesTest() = runTest {
        val zoneStates = getZoneStates()

        `when`(service.getZoneState("token", 1234, 0))
            .thenReturn(zoneStates[0])

        assertEquals(repository.getZoneStates("token", 1234, getZones()), zoneStates)
    }

    @Test
    fun getConfigFromDb() = runTest {
        val acConfig = getAcConfig()

        `when`(dao.getAcConfig(0)).thenReturn(acConfig)

        assertEquals(repository.getConfigFromDb(0), acConfig)
    }
}
