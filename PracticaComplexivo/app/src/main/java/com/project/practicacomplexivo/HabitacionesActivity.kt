package com.project.practicacomplexivo

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.project.practicacomplexivo.conexion.RetrofitClient
import com.project.practicacomplexivo.models.Categorias
import com.project.practicacomplexivo.models.Habitaciones
import com.project.practicacomplexivo.models.Usuarios
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HabitacionesActivity : AppCompatActivity() {

    private lateinit var linearLayoutHabitaciones: LinearLayout
    private lateinit var editTextBuscar: EditText
    private var habitaciones: List<Habitaciones> = emptyList()
    private var habitacionesFiltradas: List<Habitaciones> = emptyList()
    private var userId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_habitaciones)

        // Establecer el color de la barra de estado a #0A8ADC
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            window.statusBarColor = Color.parseColor("#0A8ADC")
        }

        // Obtener el ID del administrador desde el Intent
        userId = intent.getLongExtra("USER_ID", 0)

        linearLayoutHabitaciones = findViewById(R.id.linearLayoutHabitaciones)
        editTextBuscar = findViewById(R.id.editTextBuscar)

        // Cargar habitaciones y categorías desde la API
        cargarHabitacionesYcategorias()

        // Configurar el listener para el campo de búsqueda
        editTextBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                filtrarHabitaciones(s.toString())
            }

            override fun afterTextChanged(s: Editable) {}
        })

        val buttonTipoHabitaciones = findViewById<Button>(R.id.buttonTipoHabitaciones)
        buttonTipoHabitaciones.setOnClickListener {
            val intent = Intent(this, CategoriasActivity::class.java)
            startActivity(intent)
        }
    }

    private fun cargarHabitacionesYcategorias() {
        // Llamada para obtener la lista de habitaciones
        val callHabitaciones = RetrofitClient.instance.getHabitaciones()
        callHabitaciones.enqueue(object : Callback<List<Habitaciones>> {
            override fun onResponse(call: Call<List<Habitaciones>>, response: Response<List<Habitaciones>>) {
                if (response.isSuccessful) {
                    habitaciones = response.body() ?: emptyList()

                    // Llamada para obtener la lista de categorías
                    val callCategorias = RetrofitClient.instance.getCategorias()
                    callCategorias.enqueue(object : Callback<List<Categorias>> {
                        override fun onResponse(call: Call<List<Categorias>>, response: Response<List<Categorias>>) {
                            if (response.isSuccessful) {
                                val categorias = response.body() ?: emptyList()

                                // Asignar categorías a habitaciones
                                asignarCategoriasAHabitaciones(habitaciones, categorias)
                            }
                        }

                        override fun onFailure(call: Call<List<Categorias>>, t: Throwable) {
                            // Manejar el error de la llamada de categorías
                            Toast.makeText(this@HabitacionesActivity, "Error al cargar las categorías", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }

            override fun onFailure(call: Call<List<Habitaciones>>, t: Throwable) {
                // Manejar el error de la llamada de habitaciones
                Toast.makeText(this@HabitacionesActivity, "Error al cargar las habitaciones", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun asignarCategoriasAHabitaciones(habitaciones: List<Habitaciones>, categorias: List<Categorias>) {
        habitaciones.forEach { habitacion ->
            val categoria = categorias.find { it.idCategoria == habitacion.idCategoria }
            habitacion.categoria = categoria // Asignar la categoría completa a la habitación
        }

        // Inicializar la lista filtrada como la lista completa de habitaciones
        habitacionesFiltradas = habitaciones
        mostrarHabitaciones(habitacionesFiltradas)
    }

    private fun filtrarHabitaciones(filtro: String) {
        habitacionesFiltradas = habitaciones.filter { habitacion ->
            val textoBusqueda = """
                ${habitacion.numero}
                ${habitacion.categoria?.nombre ?: ""}
                ${habitacion.descripcion}
                ${habitacion.precio}
                ${habitacion.estado}
            """.trimIndent().toLowerCase()

            textoBusqueda.contains(filtro.toLowerCase())
        }
        mostrarHabitaciones(habitacionesFiltradas)
    }

    private fun mostrarHabitaciones(habitaciones: List<Habitaciones>) {
        linearLayoutHabitaciones.removeAllViews() // Limpiar la vista antes de mostrar las habitaciones filtradas

        val inflater = LayoutInflater.from(this)

        habitaciones.forEach { habitacion ->
            val cardView = inflater.inflate(R.layout.card_habitacion, linearLayoutHabitaciones, false)

            val textViewNumeroHabitacion = cardView.findViewById<TextView>(R.id.textViewNumeroHabitacion)
            val textViewCategoriaHabitacion = cardView.findViewById<TextView>(R.id.textViewCategoriaHabitacion)
            val textViewDescripcionHabitacion = cardView.findViewById<TextView>(R.id.textViewDescripcionHabitacion)
            val textViewPrecioHabitacion = cardView.findViewById<TextView>(R.id.textViewPrecioHabitacion)
            val textViewEstadoHabitacion = cardView.findViewById<TextView>(R.id.textViewEstadoHabitacion)

            // Asignar valores a los TextViews
            textViewNumeroHabitacion.text = "Número: ${habitacion.numero}"
            textViewCategoriaHabitacion.text = "Tipo: ${habitacion.categoria?.nombre ?: "Sin categoría"}"
            textViewDescripcionHabitacion.text = "Descripción: ${habitacion.descripcion}"
            textViewPrecioHabitacion.text = "Precio: $${habitacion.precio}"

            // Mostrar el estado directamente como un String
            textViewEstadoHabitacion.text = "Estado: ${habitacion.estado}"

            // Añadir la tarjeta al LinearLayout
            linearLayoutHabitaciones.addView(cardView)
        }
    }

    private fun crearSpannable(titulo: String, valor: String): SpannableString {
        val spannable = SpannableString("$titulo$valor")
        spannable.setSpan(
            StyleSpan(Typeface.BOLD),
            0,
            titulo.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return spannable
    }

    private fun crearSpannableConColor(titulo: String, valor: String, color: Int): SpannableString {
        val spannable = SpannableString("$titulo$valor")
        spannable.setSpan(
            StyleSpan(Typeface.BOLD),
            0,
            titulo.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            ForegroundColorSpan(color),
            titulo.length,
            spannable.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return spannable
    }

}
