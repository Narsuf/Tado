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
import org.mockito.Mockito.*
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

        `when`(service.getZoneState("token", 1234, 1))
            .thenReturn(getZoneState())

        assertEquals(repository.getZoneStates("token", 1234, getZones()), zoneStates)
    }

    @Test
    fun getACsConfigsFromDb() = runTest {
        val acsConfigs = getACsConfigs()

        `when`(service.getAccountDetails("token")).thenReturn(getAccountDetails())
        `when`(service.getZones("token", 1234)).thenReturn(getZones())
        `when`(dao.getAcConfig(1)).thenReturn(getAcConfig())

        assertEquals(repository.getACsConfigs("token"), acsConfigs)
    }

    @Test
    fun getACsConfigsRemotely() = runTest {
        val zoneState = getZoneState()
        val acsConfigs = getACsConfigs(
            getAcConfig(
                mode = zoneState.setting.mode!!,
                temperature = zoneState.setting.temperature!!.celsius
            )
        )

        `when`(service.getAccountDetails("token")).thenReturn(getAccountDetails())
        `when`(service.getZones("token", 1234)).thenReturn(getZones())
        `when`(dao.getAcConfig(1)).thenReturn(null)
        `when`(service.getZoneState("token", 1234, 1))
            .thenReturn(zoneState)

        assertEquals(repository.getACsConfigs("token"), acsConfigs)
        verify(dao, times(1)).insertAcConfig(acsConfigs[0])
    }

    @Test
    fun getConfigFromDb() = runTest {
        val acConfig = getAcConfig()

        `when`(dao.getAcConfig(0)).thenReturn(acConfig)

        assertEquals(repository.getConfigFromDb(0), acConfig)
    }

    @Test
    fun getConfigsFromDb() = runTest {
        val acsConfigs = getACsConfigs()

        `when`(dao.getAcConfigs()).thenReturn(acsConfigs)

        assertEquals(repository.getConfigsFromDb(), acsConfigs)
    }

    @Test
    fun insertConfigIntoDb() = runTest {
        val acConfig = getAcConfig()

        repository.insertConfigIntoDb(acConfig)

        verify(dao, times(1)).insertAcConfig(acConfig)
    }

    @Test
    fun sendOrder() = runTest {
        val overlay = getOverlay()

        `when`(service.sendOrder("token", 1234, 0 , overlay))
            .thenReturn(overlay)

        assertEquals(repository.sendOrder("token", 1234, 0, overlay), overlay)
    }
}
