package org.n27.tado

import org.n27.tado.data.api.models.Mode
import org.n27.tado.data.api.models.Power
import java.util.*

class Utils {

    fun getDesiredTemperature(
        desiredTemperature: Float,
        mode: Mode,
        calendar: Calendar = Calendar.getInstance()
    ): Float = if (isNightTime(calendar))
        getNightTemperature(desiredTemperature, mode)
    else
        desiredTemperature

    internal fun isNightTime(calendar: Calendar): Boolean {
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        return hour >= 23 || hour < 7
    }

    internal fun getNightTemperature(desiredTemperature: Float, mode: Mode) = if (mode == Mode.HEAT)
        16f
    else
        desiredTemperature

    fun getACPowerStatus(
        currentTemperature: Float,
        desiredTemperature: Float,
        mode: Mode,
        power: Power
    ): Power? {
        val tooHot = currentTemperature > desiredTemperature + Constants.OFFSET
        val tooCold = currentTemperature < desiredTemperature - Constants.OFFSET

        val overHeated = mode == Mode.HEAT && tooHot
        val overCooled = mode != Mode.HEAT && tooCold

        val gettingCold = mode == Mode.HEAT && tooCold
        val gettingHot = mode != Mode.HEAT && tooHot

        return when {
            overHeated || overCooled -> Power.OFF.takeIf { power == Power.ON }
            gettingCold || gettingHot -> Power.ON.takeIf { power == Power.OFF }
            else -> null
        }
    }
}