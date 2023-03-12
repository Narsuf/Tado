package org.n27.test.generators

import org.n27.tado.data.api.models.*

fun getSetting() = Setting(
    type = Type.AIR_CONDITIONING,
    power = Power.OFF,
    temperature = getTemperature(),
    mode = Mode.COOL,
    fanLevel = FanLevel.LEVEL1,
    verticalSwing = VerticalSwing.MID,
    horizontalSwing = HorizontalSwing.AUTO
)