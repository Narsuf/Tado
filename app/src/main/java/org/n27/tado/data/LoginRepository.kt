package org.n27.tado.data

import org.n27.tado.data.api.TadoApi
import org.n27.tado.data.api.models.LoginResponse
import org.n27.tado.data.model.LoggedInUser
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

@Singleton
class LoginRepository @Inject constructor(val tadoApi: TadoApi) {

    // in-memory cache of the loggedInUser object
    var user: LoggedInUser? = null
        private set

    val isLoggedIn: Boolean
        get() = user != null

    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        user = null
    }

    /*fun logout() {
        user = null
        dataSource.logout()
    }*/

    suspend fun login(username: String, password: String) = runCatching {
        success(tadoApi.login(username, password))
    }.getOrElse {
        failure(it)
    }

    private fun setLoggedInUser(loggedInUser: LoggedInUser) {
        this.user = loggedInUser
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }
}
