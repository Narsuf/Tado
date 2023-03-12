package org.n27.tado.ui.common.extensions

data class OpenFloatRange(val from: Float, val to: Float)
infix fun Float.open(to: Float) = OpenFloatRange(this, to)
operator fun OpenFloatRange.contains(f: Float) = from < f && f < to

val inRange = 10f in (0.0f open 20f)