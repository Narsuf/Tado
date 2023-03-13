package org.n27.test.generators

import org.n27.tado.data.api.models.ZoneState

fun getZoneStates() = listOf(getZoneState())

fun getZoneState() = ZoneState(
    setting = getSetting(),
    sensorDataPoints = getSensorDataPoints()
)

fun getSensorDataPoints() = ZoneState.SensorDataPoints(
    insideTemperature = getTemperature(celsius = 22.56f)
)
