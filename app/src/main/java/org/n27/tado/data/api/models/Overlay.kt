package org.n27.tado.data.api.models

import java.io.Serializable

data class Overlay(val setting: Setting, val termination: Termination) : Serializable {

    data class Termination(val type: String) : Serializable
}
