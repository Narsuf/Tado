package org.n27.tado.data.api

import org.n27.tado.data.api.models.LoginResponse
import retrofit2.http.POST
import retrofit2.http.Query

interface TadoAuth {

    @POST("oauth/token")
    suspend fun login(
        @Query("username") username: String?,
        @Query("password") password: String?,
        @Query("client_id") clientId: String = "tado-web-app",
        @Query("grant_type") grantType: String = "password",
        @Query("scope") scope: String = "home.user",
        @Query("client_secret") clientSecret: String = "wZaRN7rpjn3FoNyF5IFuxg9uMzYJcvOoQ8QWiIqS3hfk6gLhVlG57j5YNoZL2Rtc",
    ): LoginResponse
}