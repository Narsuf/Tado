package org.n27.tado.service

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import org.n27.tado.Constants.OFFSET
import org.n27.tado.data.TadoRepository
import org.n27.tado.data.api.models.*
import javax.inject.Inject

class TadoServiceViewModel @Inject constructor(
    private val repository: TadoRepository
) : ViewModel() {

    fun manageTemperature(username: String?, password: String?) {
        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            println(throwable.message)
        }

        viewModelScope.launch(exceptionHandler) {
            val login = repository.login(username, password)
            val token = "Bearer ${login.access_token}"

            val accountDetails = repository.getAccountDetails(token)
            val homeId = accountDetails.homeId

            val zones = repository.getZones(token, homeId)

            val zoneStates = repository.getZoneStates(token, homeId, zones)
            val acsConfigs = repository.getConfigsFromDb()

            zoneStates.forEachIndexed { index, zoneState ->
                val acsConfig = acsConfigs[index]
                val currentTemperature = zoneState.sensorDataPoints.insideTemperature.celsius
                val desiredTemperature = acsConfig.temperature
                val power = getACPowerStatus(
                    currentTemperature,
                    desiredTemperature,
                    zoneState.setting.mode,
                    zoneState.setting.power
                )

                power?.let {
                    val order = Overlay(
                        setting = zoneState.setting.copy(
                            power = it,
                            temperature = Temperature(acsConfig.temperature),
                            mode = acsConfig.mode,
                            fanLevel = FanLevel.LEVEL1,
                            verticalSwing = VerticalSwing.MID
                        ),
                        termination = Overlay.Termination("MANUAL")
                    )

                    if (acsConfig.serviceEnabled)
                        repository.sendOrder(token, homeId, zones[index].id, order)
                }
            }
        }
    }

    private fun getACPowerStatus(
        currentTemperature: Float,
        desiredTemperature: Float,
        mode: Mode?,
        power: Power
    ): Power? {
        val tooHot = currentTemperature > desiredTemperature + OFFSET
        val tooCold = currentTemperature < desiredTemperature - OFFSET

        val overHeated = mode == Mode.HEAT && tooHot
        val overCooled = mode != Mode.HEAT && tooCold

        val gettingCold = mode == Mode.HEAT && tooCold
        val gettingHot = mode != Mode.HEAT && tooHot

        return when {
            overHeated || overCooled -> Power.OFF.takeIf { power == Power.ON }
            gettingCold || gettingHot -> Power.ON.takeIf { power == Power.OFF }
            else -> null
        }
    }
}
