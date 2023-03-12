package org.n27.test.generators

import org.n27.tado.data.api.models.Mode
import org.n27.tado.data.room.AcConfig

fun getACsConfigs(acConfig: AcConfig = getAcConfig()) = listOf(acConfig)

fun getAcConfig(
    mode: Mode = Mode.HEAT,
    temperature: Float = 19f
) = AcConfig(
    id = 1,
    name = "Bedroom",
    mode = mode,
    temperature = temperature,
    serviceEnabled = false
)