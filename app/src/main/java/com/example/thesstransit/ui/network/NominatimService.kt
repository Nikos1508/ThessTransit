package com.example.thesstransit.ui.network

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

data class NominatimAddress(
    @SerializedName("road")
    val road: String? = null,
    @SerializedName("house_number")
    val houseNumber: String? = null,
    @SerializedName("neighbourhood")
    val neighbourhood: String? = null,
    @SerializedName("suburb")
    val suburb: String? = null,
    @SerializedName("village")
    val village: String? = null,
    @SerializedName("town")
    val town: String? = null,
    @SerializedName("city")
    val city: String? = null,
    @SerializedName("municipality")
    val municipality: String? = null,
    @SerializedName("county")
    val county: String? = null,
    @SerializedName("state")
    val state: String? = null
)

data class NominatimPlace(
    @SerializedName("display_name")
    val displayName: String,
    @SerializedName("lat")
    val lat: String,
    @SerializedName("lon")
    val lon: String,
    @SerializedName("address")
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
        userAgent: String = "ThessTransit/1.0 (Android)"

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
        userAgent: String = "ThessTransit/1.0 (Android)"

    ): NominatimReverseResult

}

data class NominatimReverseResult(
    @SerializedName("display_name")
    val displayName: String?,
    @SerializedName("address")
    val address: NominatimAddress? = null
)