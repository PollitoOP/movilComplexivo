package com.project.practicacomplexivo.models

data class Habitaciones(
    val idHabitacion: Int = 0,
    val numero: Int = 0,
    val descripcion: String = "",
    val precio: Double = 0.0,
    val estado: String = "",
    val idCategoria: Int = 0,
    var categoria: Categorias? = null
)