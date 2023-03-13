package org.n27.tado.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import org.n27.tado.data.TadoRepository
import org.n27.tado.data.api.models.Mode
import org.n27.tado.ui.main.MainState.ConfigUpdated
import org.n27.tado.ui.main.MainState.DbConfigRetrieved
import org.n27.tado.ui.main.MainState.Failure
import org.n27.tado.ui.main.MainState.Loading
import org.n27.tado.ui.main.MainState.Success
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val repository: TadoRepository
) : ViewModel() {

    private val state = MutableLiveData<MainState>(Loading)
    internal val viewState: LiveData<MainState> = state

    fun getAcsConfigs(token: String?) {
        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            state.value = Failure(throwable)
        }

        viewModelScope.launch(exceptionHandler) {
            val bearerToken = "Bearer $token"

            val acsConfigs = repository.getACsConfigs(bearerToken)

            state.value = Success(acsConfigs)
        }
    }

    fun getAcsConfigsFromDb() {
        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            state.value = Failure(throwable)
        }

        viewModelScope.launch(exceptionHandler) {
            val acsConfigs = repository.getConfigsFromDb()
            state.value = DbConfigRetrieved(acsConfigs)
        }
    }

    fun postConfigChanges(
        id: Int,
        mode: Mode? = null,
        temperature: Float? = null,
        serviceEnabled: Boolean? = null
    ) {
        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            state.value = Failure(throwable)
        }

        viewModelScope.launch(exceptionHandler) {
            val acConfig = repository.getConfigFromDb(id)

            acConfig?.let { config ->
                mode?.let { repository.insertConfigIntoDb(config.copy(mode = it)) }
                temperature?.let { repository.insertConfigIntoDb(config.copy(temperature = it)) }
                serviceEnabled?.let {
                    repository.insertConfigIntoDb(config.copy(serviceEnabled = it))
                }
            }

            state.value = ConfigUpdated
        }
    }
}
