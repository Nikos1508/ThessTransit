package com.example.thesstransit.ui.network

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

data class NominatimResult(

    @SerializedName("display_name")
    val displayName: String,
    val lat: String,
    val lon: String

)

interface NominatimApi {

    @GET("search")

    suspend fun search(
        @Query("q")
        query: String,

        @Query("format")
        format: String = "jsonv2",

        @Query("limit")
        limit: Int = 8, //This might get to 6

        @Query("countrycodes")
        country: String = "gr"

    ): List<NominatimResult>
}