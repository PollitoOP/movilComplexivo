package com.project.practicacomplexivo.conexion

import com.project.practicacomplexivo.models.Categorias
import com.project.practicacomplexivo.models.Habitaciones
import com.project.practicacomplexivo.models.Productos
import com.project.practicacomplexivo.models.Usuarios
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // Endpoint para obtener la lista de administradores y verificar login
    @GET("administradores")
    fun getAdministradores(
        @Query("usuario") usuario: String,
        @Query("password") password: String
    ): Call<List<Usuarios>> // Cambia el modelo si es necesario a un modelo específico de Administradores

    @GET("administradores/{id}")
    fun getAdministradorById(@Path("id") id: Long): Call<Usuarios> // Cambia el modelo si es necesario a un modelo específico de Administradores

    // Endpoint para obtener la lista de habitaciones
    @GET("habitaciones")
    fun getHabitaciones(): Call<List<Habitaciones>>

    @GET("categorias")
    fun getCategorias(): Call<List<Categorias>>

    @GET("productos")
    fun getProductos(): Call<List<Productos>>

}
