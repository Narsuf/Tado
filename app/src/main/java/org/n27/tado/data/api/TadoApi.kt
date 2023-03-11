package org.n27.tado.data.api

import org.n27.tado.data.api.models.AccountDetails
import org.n27.tado.data.api.models.Overlay
import org.n27.tado.data.api.models.Zone
import org.n27.tado.data.api.models.ZoneState
import retrofit2.http.*

interface TadoApi {

    @GET("v1/me")
    suspend fun getAccountDetails(@Header("Authorization") authorization: String?): AccountDetails

    @GET("v2/homes/{homeId}/zones")
    suspend fun getZones(
        @Header("Authorization") authorization: String?,
        @Path("homeId") homeId: Int
    ): List<Zone>

    @GET("v2/homes/{homeId}/zones/{zoneId}/state")
    suspend fun getZoneState(
        @Header("Authorization") authorization: String?,
        @Path("homeId") homeId: Int,
        @Path("zoneId") zoneId: Int
    ): ZoneState

    @PUT("v2/homes/{homeId}/zones/{zoneId}/overlay")
    suspend fun sendOrder(
        @Header("Authorization") authorization: String?,
        @Path("homeId") homeId: Int,
        @Path("zoneId") zoneId: Int,
        @Body body: Overlay
    ): Overlay
}
