package org.n27.tado

import org.junit.Assert.*
import org.junit.Test
import org.n27.tado.data.api.models.Mode
import java.util.*

class UtilsTest {

    private val utils = Utils()
    private val desiredTemperature = 19f
    private val expectedTemperatureAtNight = 16f

    @Test
    fun getDesiredTemperature() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 7)
        }
        assertEquals(
            utils.getDesiredTemperature(desiredTemperature, Mode.HEAT, calendar),
            desiredTemperature
        )

        calendar.apply { set(Calendar.HOUR_OF_DAY, 6) }
        assertEquals(
            utils.getDesiredTemperature(desiredTemperature, Mode.HEAT, calendar),
            expectedTemperatureAtNight
        )
    }

    @Test
    fun isNightTime() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 7)
        }
        assertFalse(utils.isNightTime(calendar))

        calendar.apply { set(Calendar.HOUR_OF_DAY, 6) }
        assertTrue(utils.isNightTime(calendar))

        calendar.apply { set(Calendar.HOUR_OF_DAY, 22) }
        assertFalse(utils.isNightTime(calendar))

        calendar.apply { set(Calendar.HOUR_OF_DAY, 23) }
        assertTrue(utils.isNightTime(calendar))
    }

    @Test
    fun getNightTemperature() {
        assertEquals(
            utils.getNightTemperature(desiredTemperature, Mode.HEAT),
            expectedTemperatureAtNight
        )

        assertEquals(
            utils.getNightTemperature(desiredTemperature, Mode.COOL),
            desiredTemperature
        )
    }
}