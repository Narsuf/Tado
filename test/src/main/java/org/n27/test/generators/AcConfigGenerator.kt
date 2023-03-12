package org.n27.test.generators

import org.n27.tado.data.api.models.Mode
import org.n27.tado.data.room.AcConfig

fun getAcConfig() = AcConfig(
    id = 0,
    name = "Bedroom",
    mode = Mode.HEAT,
    temperature = 19f,
    serviceEnabled = false
)