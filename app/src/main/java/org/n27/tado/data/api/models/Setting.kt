package org.n27.tado.data.api.models

import java.io.Serializable

data class Setting(
    val type: Type,
    val power: Power,
    val temperature: Temperature?,
    val mode: Mode?,
    val fanLevel: FanLevel?
) : Serializable

enum class Type { AIR_CONDITIONING }
enum class Power { ON, OFF }
enum class Mode { HEAT, AUTO, COOL, FAN, DRY }
enum class FanLevel { LEVEL1, LEVEL2, LEVEL3, LEVEL4, LEVEL5, AUTO }
