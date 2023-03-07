package org.n27.tado.data.api

import org.n27.tado.data.api.models.AccountDetails
import retrofit2.http.GET
import retrofit2.http.Header

interface TadoApi {

    @GET("api/v1/me")
    suspend fun getAccountDetails(@Header("Authorization") authorization: String?): AccountDetails
}
