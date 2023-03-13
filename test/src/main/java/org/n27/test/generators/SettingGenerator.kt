package org.n27.test.generators

import org.n27.tado.data.api.models.FanLevel
import org.n27.tado.data.api.models.HorizontalSwing
import org.n27.tado.data.api.models.Mode
import org.n27.tado.data.api.models.Power
import org.n27.tado.data.api.models.Setting
import org.n27.tado.data.api.models.Type
import org.n27.tado.data.api.models.VerticalSwing

fun getSetting() = Setting(
    type = Type.AIR_CONDITIONING,
    power = Power.OFF,
    temperature = getTemperature(),
    mode = Mode.COOL,
    fanLevel = FanLevel.LEVEL1,
    verticalSwing = VerticalSwing.MID,
    horizontalSwing = HorizontalSwing.AUTO
)
