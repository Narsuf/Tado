package org.n27.test.generators

import org.n27.tado.data.api.models.Zone

fun getZones() = listOf(getZone())

fun getZone() = Zone(id = 1, name = "Bedroom")