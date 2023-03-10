package org.n27.tado.service

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import org.n27.tado.data.api.TadoApi
import org.n27.tado.data.api.TadoAuth
import org.n27.tado.data.api.models.AccountDetails
import org.n27.tado.data.api.models.FanLevel
import org.n27.tado.data.api.models.LoginResponse
import org.n27.tado.data.api.models.Mode
import org.n27.tado.data.api.models.Overlay
import org.n27.tado.data.api.models.Power
import org.n27.tado.data.api.models.Temperature
import javax.inject.Inject
import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success

class TadoServiceViewModel @Inject constructor(
    private val tadoAuth: TadoAuth,
    private val tadoApi: TadoApi
) : ViewModel() {

    private val _loginResult = MutableLiveData<Result<LoginResponse>>()
    val loginResult: LiveData<Result<LoginResponse>> = _loginResult

    private val _accountDetails = MutableLiveData<Result<AccountDetails>>()
    val accountDetails: LiveData<Result<AccountDetails>> = _accountDetails

    fun login(username: String, password: String) {
        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            _loginResult.value = failure(throwable)
        }

        viewModelScope.launch(exceptionHandler) {
            _loginResult.value = success(tadoAuth.login(username, password))
        }
    }

    fun turnHeatingOn(token: String?) {
        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            _accountDetails.value = failure(throwable)
        }

        viewModelScope.launch(exceptionHandler) {
            val bearerToken = "Bearer $token"

            val accountDetails = tadoApi.getAccountDetails(bearerToken)
            val zones = tadoApi.getZones(bearerToken, accountDetails.homeId)
            val zoneState = tadoApi.getZoneState(bearerToken, accountDetails.homeId, zones[0].id)

            val turnOnOrder = Overlay(
                setting = zoneState.setting.copy(
                    power = Power.ON,
                    temperature = Temperature(celsius = 20f),
                    mode = Mode.HEAT,
                    fanLevel = FanLevel.LEVEL1
                ),
                termination = Overlay.Termination("MANUAL")
            )

            val turnOffOrder = Overlay(
                setting = zoneState.setting.copy(
                    power = Power.OFF
                ),
                termination = Overlay.Termination("MANUAL")
            )

            val sendOrder = tadoApi.sendOrder(bearerToken, accountDetails.homeId, zones[0].id, turnOnOrder)
            println(sendOrder.toString())
        }
    }
}
