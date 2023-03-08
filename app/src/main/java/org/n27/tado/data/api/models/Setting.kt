package org.n27.tado.data.api.models

import java.io.Serializable

data class Setting(
    val type: String,
    val power: String,
    val temperature: Temperature
) : Serializable
