package org.n27.tado.data

import org.n27.tado.data.api.TadoApi
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

@Singleton
class TadoRepository @Inject constructor(private val tadoApi: TadoApi) {

    // in-memory cache of the loggedInUser object
    //var loggedInUser: LoginResponse? = null

    /*fun logout() {
        user = null
        dataSource.logout()
    }*/

    suspend fun login(username: String, password: String) = runCatching {
        success(tadoApi.login(username, password))
    }.getOrElse {
        failure(it)
    }
}
