package org.n27.tado.ui.main

import org.n27.tado.data.api.models.Zone
import org.n27.tado.data.api.models.ZoneState

sealed class MainState {

    object Loading : MainState()
    data class Success(val acs: List<Zone>, val acsDetails: List<ZoneState>) : MainState()
    data class Failure(val error: Throwable) : MainState()
}
