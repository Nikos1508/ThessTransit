package com.example.thesstransit.ui.network

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query


data class NominatimPlace(
    val display_name: String,
    val lat: String,
    val lon: String
)


interface NominatimService {


    @GET("search")
    suspend fun search(

        @Query("q")
        query: String,

        @Query("format")
        format: String = "json",

        @Query("limit")
        limit: Int = 10,

        @Query("countrycodes")
        country: String = "gr",

        @Header("User-Agent")
        userAgent: String = "ThessTransit/1.0"

    ): List<NominatimPlace>



    @GET("reverse")
    suspend fun reverse(

        @Query("lat")
        lat: Double,

        @Query("lon")
        lon: Double,

        @Query("format")
        format:String = "json",

        @Header("User-Agent")
        userAgent:String = "ThessTransit/1.0"

    ): NominatimReverseResult

}

data class NominatimReverseResult(
    val display_name:String?
)