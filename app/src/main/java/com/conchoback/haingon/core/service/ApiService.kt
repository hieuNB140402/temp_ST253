package com.conchoback.haingon.core.service
import com.conchoback.haingon.data.model.PathAPI
import retrofit2.Response
import retrofit2.http.GET
interface ApiService {
    @GET("/api/ST253_Clothes_Skins_Maker_for_RBX_v2")
    suspend fun getAllData(): Response<PathAPI>
}