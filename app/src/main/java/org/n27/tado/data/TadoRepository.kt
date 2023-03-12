package org.n27.tado.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.n27.tado.data.api.TadoApi
import org.n27.tado.data.api.models.Mode
import org.n27.tado.data.api.models.Zone
import org.n27.tado.data.room.AcConfig
import org.n27.tado.data.room.AcConfigDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TadoRepository @Inject constructor(
    private var service: TadoApi,
    private var dao: AcConfigDao,
) {

    internal suspend fun getAcsConfigs(token: String): List<AcConfig> {
        val accountDetails = getAccountDetails(token)
        val zones = getZones(token, accountDetails.homeId)

        return zones.map { it.getConfig(token, accountDetails.homeId) }
    }

    private suspend fun getAccountDetails(token: String) = withContext(Dispatchers.IO) {
        service.getAccountDetails(token)
    }

    private suspend fun getZones(token: String, homeId: Int) = withContext(Dispatchers.IO) {
        service.getZones(token, homeId)
    }

    private suspend fun Zone.getZoneState(token: String, homeId: Int) = withContext(Dispatchers.IO) {
        service.getZoneState(token, homeId, id)
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

    internal suspend fun insertConfigIntoDb(acConfig: AcConfig) = withContext(Dispatchers.IO) {
        dao.insertAcConfig(acConfig)
    }
}