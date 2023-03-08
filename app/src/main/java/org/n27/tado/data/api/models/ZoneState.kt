package org.n27.tado.data.api.models

import java.io.Serializable

data class ZoneState(
    val setting: Setting,
    val sensorDataPoints: SensorDataPoints
) : Serializable {

    data class SensorDataPoints(val insideTemperature: Temperature) : Serializable

}
