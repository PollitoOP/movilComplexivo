package com.project.practicacomplexivo.conexion

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://67.205.158.66:9096/api/"

    // Instancia de Retrofit para el login
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Instancia para las solicitudes relacionadas con el login
    val instanceL: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    // Instancia para las solicitudes generales
    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ApiService::class.java)
    }
}
