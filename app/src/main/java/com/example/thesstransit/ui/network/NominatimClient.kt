package com.example.thesstransit.ui.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NominatimClient {

    val api: NominatimService by lazy {

        Retrofit.Builder()
            .baseUrl("https://nominatim.openstreetmap.org/")

            .addConverterFactory( GsonConverterFactory.create() )

            .build()

            .create( NominatimService::class.java )

    }
}