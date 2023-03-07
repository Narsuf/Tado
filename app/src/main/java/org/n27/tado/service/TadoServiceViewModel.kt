package org.n27.tado.service

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import org.n27.tado.data.api.TadoApi
import org.n27.tado.data.api.TadoAuth
import org.n27.tado.data.api.models.AccountDetails
import org.n27.tado.data.api.models.LoginResponse
import javax.inject.Inject
import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success

class TadoServiceViewModel @Inject constructor(
    private val tadoAuth: TadoAuth,
    private val tadoApi: TadoApi
) : ViewModel() {

    private val _loginResult = MutableLiveData<Result<LoginResponse>>()
    val loginResult: LiveData<Result<LoginResponse>> = _loginResult

    private val _accountDetails = MutableLiveData<Result<AccountDetails>>()
    val accountDetails: LiveData<Result<AccountDetails>> = _accountDetails

    fun login(username: String, password: String) {
        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            _loginResult.value = failure(throwable)
        }

        viewModelScope.launch(exceptionHandler) {
            _loginResult.value = success(tadoAuth.login(username, password))
        }
    }

    fun getAccountDetails(token: String?) {
        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            _accountDetails.value = failure(throwable)
        }

        viewModelScope.launch(exceptionHandler) {
            _accountDetails.value = success(tadoApi.getAccountDetails("Bearer $token"))
        }
    }
}