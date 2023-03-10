package org.n27.tado.ui.login

import org.n27.tado.data.api.models.LoginResponse

sealed class LoginState {

    object Idle : LoginState()

    data class FormDataChanged(
        val usernameError: Int? = null,
        val passwordError: Int? = null,
        val isDataValid: Boolean = false
    ) : LoginState()

    data class Success(val result: LoginResponse) : LoginState()
    data class Failure(val error: Throwable) : LoginState()
}
