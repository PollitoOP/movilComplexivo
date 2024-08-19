package com.project.practicacomplexivo.models

data class Productos (
    val idHabitacion: Int = 0,
    val numero: Int = 0,
    val descripcion: String = "",
    val precio: Double = 0.0,
    val estado: Boolean = true,
    val idCategoria: Int = 0,
    var categoria: Categorias? = null // Cambiado a var para permitir la reasignación
)




