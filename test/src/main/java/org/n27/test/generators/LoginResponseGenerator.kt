package org.n27.test.generators

import org.n27.tado.data.api.models.LoginResponse

fun getLoginResponse() = LoginResponse(
    access_token = "abc",
    token_type = "bearer",
    refresh_token = "def",
    expires_in = 599
)