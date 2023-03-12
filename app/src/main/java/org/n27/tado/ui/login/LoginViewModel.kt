package org.n27.tado.ui.login

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import org.n27.tado.R
import org.n27.tado.data.TadoRepository
import org.n27.tado.ui.login.LoginState.*
import javax.inject.Inject

class LoginViewModel @Inject constructor(private val repository: TadoRepository) : ViewModel() {

    private val state = MutableLiveData<LoginState>(Idle)
    internal val viewState: LiveData<LoginState> = state

    fun login(username: String, password: String) {
        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            state.value = Failure(throwable)
        }

        viewModelScope.launch(exceptionHandler) {
            state.value = Success(repository.login(username, password))
        }
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            state.value = FormDataChanged(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            state.value = FormDataChanged(passwordError = R.string.invalid_password)
        } else {
            state.value = FormDataChanged(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}
