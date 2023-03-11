package org.n27.tado.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import org.n27.tado.data.api.TadoApi
import org.n27.tado.data.api.models.ZoneState
import org.n27.tado.ui.main.MainState.Failure
import org.n27.tado.ui.main.MainState.Loading
import org.n27.tado.ui.main.MainState.Success
import javax.inject.Inject

class MainViewModel @Inject constructor(private val tadoApi: TadoApi) : ViewModel() {

    private val state = MutableLiveData<MainState>(Loading)
    internal val viewState: LiveData<MainState> = state

    fun getACs(token: String?) {
        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            state.value = Failure(throwable)
        }

        viewModelScope.launch(exceptionHandler) {
            val bearerToken = "Bearer $token"

            val accountDetails = tadoApi.getAccountDetails(bearerToken)
            val zones = tadoApi.getZones(bearerToken, accountDetails.homeId)

            val zoneStates = mutableListOf<ZoneState>()

            zones.forEachIndexed { index, _ ->
                zoneStates.add(tadoApi.getZoneState(bearerToken, accountDetails.homeId, zones[index].id))
            }

            state.value = Success(zones, zoneStates)
        }
    }
}
