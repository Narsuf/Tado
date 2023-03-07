package org.n27.tado.service

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.n27.tado.data.TadoRepository
import org.n27.tado.data.api.models.LoginResponse
import javax.inject.Inject

class TadoServiceViewModel @Inject constructor(
    private val tadoRepository: TadoRepository
) : ViewModel() {

    private val _loginResult = MutableLiveData<Result<LoginResponse>>()
    val loginResult: LiveData<Result<LoginResponse>> = _loginResult

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _loginResult.value = tadoRepository.login(username, password)
        }
    }
}