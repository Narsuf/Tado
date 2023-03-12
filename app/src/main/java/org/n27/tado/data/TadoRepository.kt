package org.n27.tado.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.n27.tado.data.api.TadoApi
import org.n27.tado.data.api.TadoAuth
import org.n27.tado.data.api.models.Mode
import org.n27.tado.data.api.models.Overlay
import org.n27.tado.data.api.models.Zone
import org.n27.tado.data.api.models.ZoneState
import org.n27.tado.data.room.AcConfig
import org.n27.tado.data.room.AcConfigDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TadoRepository @Inject constructor(
    private var authService: TadoAuth,
    private var service: TadoApi,
    private var dao: AcConfigDao,
) {

    internal suspend fun login(usr: String?, psw: String?) = withContext(Dispatchers.IO) {
        authService.login(usr, psw)
    }

    internal suspend fun getACsConfigs(token: String): List<AcConfig> {
        val accountDetails = getAccountDetails(token)
        val zones = getZones(token, accountDetails.homeId)

        return zones.map { it.getConfig(token, accountDetails.homeId) }
    }

    internal suspend fun getAccountDetails(token: String) = withContext(Dispatchers.IO) {
        service.getAccountDetails(token)
    }

    internal suspend fun getZones(token: String, homeId: Int) = withContext(Dispatchers.IO) {
        service.getZones(token, homeId)
    }

    private suspend fun Zone.getZoneState(token: String, homeId: Int) = withContext(Dispatchers.IO) {
        service.getZoneState(token, homeId, id)
    }

    internal suspend fun getZoneStates(token: String, homeId: Int, zones: List<Zone>): List<ZoneState> {
        return zones.map { it.getZoneState(token, homeId) }
    }

    private suspend fun Zone.getConfig(
        token: String,
        homeId: Int
    ) = getConfigFromDb(id) ?: getZoneState(token, homeId).run {
        val acConfig = AcConfig(
            id,
            name,
            setting.mode ?: Mode.COOL,
            setting.temperature?.celsius ?: 27f,
            serviceEnabled = false
        )

        insertConfigIntoDb(acConfig)

        acConfig
    }

    internal suspend fun getConfigFromDb(id: Int) = withContext(Dispatchers.IO) {
        dao.getAcConfig(id)
    }

    internal suspend fun getConfigsFromDb() = withContext(Dispatchers.IO) { dao.getAcConfigs() }

    internal suspend fun insertConfigIntoDb(acConfig: AcConfig) = withContext(Dispatchers.IO) {
        dao.insertAcConfig(acConfig)
    }

    internal suspend fun sendOrder(
        token: String,
        homeId: Int,
        id: Int,
        order: Overlay
    ) = withContext(Dispatchers.IO) { service.sendOrder(token, homeId, id, order) }
}