package org.n27.tado

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.n27.tado.Constants.OFFSET
import org.n27.tado.data.api.models.Mode
import org.n27.tado.data.api.models.Power
import java.util.Calendar

class UtilsTest {

    private val utils = Utils()
    private val desiredTemperature = 19f
    private val expectedTemperatureAtNight = 16f
    private lateinit var calendar: Calendar
    private lateinit var mode: Mode
    private lateinit var power: Power

    @Before
    fun init() {
        calendar = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, 7) }
        mode = Mode.HEAT
        power = Power.ON
    }

    @Test
    fun getDesiredTemperature() {
        assertEquals(
            utils.getDesiredTemperature(desiredTemperature, mode, calendar),
            desiredTemperature
        )

        calendar.apply { set(Calendar.HOUR_OF_DAY, 6) }
        assertEquals(
            utils.getDesiredTemperature(desiredTemperature, mode, calendar),
            expectedTemperatureAtNight
        )
    }

    @Test
    fun isNightTime() {
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
            utils.getNightTemperature(desiredTemperature, mode),
            expectedTemperatureAtNight
        )

        assertEquals(
            utils.getNightTemperature(desiredTemperature, Mode.COOL),
            desiredTemperature
        )
    }

    @Test
    fun getACPowerStatusOnHeatMode() {
        var currentTmp = desiredTemperature + OFFSET
        // AC powered on with heat mode, but still not > desired temperature + offset.
        assertNull(utils.getACPowerStatus(currentTmp, desiredTemperature, mode, power))

        // AC powered on with heat mode and current temperature > desired temperature + offset.
        currentTmp += 0.1f
        assertEquals(
            utils.getACPowerStatus(currentTmp, desiredTemperature, mode, power),
            Power.OFF
        )

        // AC powered off with heat mode, but still not < desired temperature - offset.
        power = Power.OFF
        currentTmp = desiredTemperature - OFFSET
        assertNull(utils.getACPowerStatus(currentTmp, desiredTemperature, mode, power))

        // AC powered off with heat mode and current temperature < desired temperature - offset.
        currentTmp -= 0.1f
        assertEquals(
            utils.getACPowerStatus(currentTmp, desiredTemperature, mode, power),
            Power.ON
        )
    }

    @Test
    fun getACPowerStatus() {
        getACPowerStatusOnMode()
        getACPowerStatusOnMode(Mode.DRY)
    }

    private fun getACPowerStatusOnMode(mode: Mode = Mode.COOL) {
        val desiredTmp = 27f
        var currentTmp = desiredTmp - OFFSET
        // AC powered on, but still not < desired temperature - offset.
        assertNull(utils.getACPowerStatus(currentTmp, desiredTmp, mode, power))

        // AC powered on and current temperature < desired temperature - offset.
        currentTmp -= 0.1f
        assertEquals(
            utils.getACPowerStatus(currentTmp, desiredTmp, mode, power),
            Power.OFF
        )

        // AC powered off, but still not > desired temperature + offset.
        currentTmp = desiredTmp + OFFSET
        assertNull(utils.getACPowerStatus(currentTmp, desiredTmp, mode, Power.OFF))

        // AC powered off and current temperature > desired temperature + offset.
        currentTmp += 0.1f
        assertEquals(
            utils.getACPowerStatus(currentTmp, desiredTmp, mode, Power.OFF),
            Power.ON
        )
    }
}
