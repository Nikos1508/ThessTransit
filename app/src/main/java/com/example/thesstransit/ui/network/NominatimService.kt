package com.example.thesstransit.ui.network

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

data class NominatimAddress(
    val road: String? = null,
    val houseNumber: String? = null,
    val neighbourhood: String? = null,
    val suburb: String? = null,
    val village: String? = null,
    val town: String? = null,
    val city: String? = null,
    val municipality: String? = null,
    val county: String? = null,
    val state: String? = null
)

data class NominatimPlace(
    val displayName: String,
    val lat: String,
    val lon: String,
    val address: NominatimAddress? = null
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

        @Query("dedupe")
        dedupe: Int = 1,

        @Query("namedetails")
        nameDetails: Int = 1,

        @Query("extratags")
        extraTags: Int = 1,

        @Query("addressdetails")
        addressDetails: Int = 1,

        @Header("User-Agent")
        userAgent: String = "ThessTransit/0.9.1"

    ): List<NominatimPlace>


    @GET("reverse")
    suspend fun reverse(

        @Query("lat")
        lat: Double,

        @Query("lon")
        lon: Double,

        @Query("format")
        format:String = "json",

        @Query("addressdetails")
        addressDetails: Int = 1,

        @Header("User-Agent")
        userAgent:String = "ThessTransit/1.0"

    ): NominatimReverseResult

}

data class NominatimReverseResult(
    val displayName: String?,
    val address: NominatimAddress? = null
)