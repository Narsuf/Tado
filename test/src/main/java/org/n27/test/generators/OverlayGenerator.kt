package org.n27.test.generators

import org.n27.tado.data.api.models.Overlay

fun getOverlay() = Overlay(
    setting = getSetting(),
    termination = getTermination()
)

fun getTermination() = Overlay.Termination(type = "MANUAL")