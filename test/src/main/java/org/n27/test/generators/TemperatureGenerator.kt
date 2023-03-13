package org.n27.test.generators

import org.n27.tado.data.api.models.Temperature

fun getTemperature(celsius: Float = 19f) = Temperature(celsius)
