package org.n27.tado.data.api.models

import java.io.Serializable

data class LoginResponse(
    val access_token: String,
    val token_type: String,
    val refresh_token: String,
    val expires_in: Int,
) : Serializable
