package com.project.practicacomplexivo

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.project.practicacomplexivo.conexion.RetrofitClient
import com.project.practicacomplexivo.models.Categorias
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoriasActivity : AppCompatActivity() {

    private lateinit var linearLayoutCategorias: LinearLayout
    private lateinit var editTextBuscar: EditText
    private var categorias: List<Categorias> = emptyList()
    private var categoriasFiltradas: List<Categorias> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categorias)

        // Establecer el color de la barra de estado a #0A8ADC
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            window.statusBarColor = Color.parseColor("#0A8ADC")
        }

        linearLayoutCategorias = findViewById(R.id.linearLayoutCategorias)
        editTextBuscar = findViewById(R.id.editTextBuscar)

        // Cargar las categorías desde la API
        cargarCategorias()

        // Configurar el listener para el campo de búsqueda
        editTextBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                filtrarCategorias(s.toString())
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun cargarCategorias() {
        val callCategorias = RetrofitClient.instance.getCategorias()
        callCategorias.enqueue(object : Callback<List<Categorias>> {
            override fun onResponse(call: Call<List<Categorias>>, response: Response<List<Categorias>>) {
                if (response.isSuccessful) {
                    categorias = response.body() ?: emptyList()
                    categoriasFiltradas = categorias
                    mostrarCategorias(categoriasFiltradas)
                } else {
                    Toast.makeText(this@CategoriasActivity, "Error al cargar las categorías", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Categorias>>, t: Throwable) {
                Toast.makeText(this@CategoriasActivity, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun filtrarCategorias(filtro: String) {
        categoriasFiltradas = categorias.filter { categoria ->
            categoria.nombre.toLowerCase().contains(filtro.toLowerCase())
        }
        mostrarCategorias(categoriasFiltradas)
    }

    private fun mostrarCategorias(categorias: List<Categorias>) {
        linearLayoutCategorias.removeAllViews() // Limpiar la vista antes de mostrar las categorías filtradas

        val inflater = LayoutInflater.from(this)

        categorias.forEach { categoria ->
            val cardView = inflater.inflate(R.layout.card_categoria, linearLayoutCategorias, false)

            val textViewNombreCategoria = cardView.findViewById<TextView>(R.id.textViewNombreCategoria)

            // Asignar valores a los TextViews
            textViewNombreCategoria.text = "Tipo: ${categoria.nombre}"

            // Añadir la tarjeta al LinearLayout
            linearLayoutCategorias.addView(cardView)
        }
    }
}
