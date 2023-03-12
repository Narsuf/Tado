package org.n27.tado.ui.main

import org.n27.tado.data.room.AcConfig

sealed class MainState {

    object Loading : MainState()
    data class Success(val acsConfigs: List<AcConfig>) : MainState()
    data class Failure(val error: Throwable) : MainState()
}
