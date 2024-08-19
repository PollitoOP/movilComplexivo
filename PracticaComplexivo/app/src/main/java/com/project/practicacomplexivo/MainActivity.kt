package com.project.practicacomplexivo

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.project.practicacomplexivo.conexion.RetrofitClient
import com.project.practicacomplexivo.models.Usuarios
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var editTextCorreo: EditText
    private lateinit var editTextContrasenia: EditText
    private lateinit var buttonLogin: Button
    private lateinit var buttonTogglePassword: ImageButton
    private var isPasswordVisible: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Establecer el color de la barra de estado a #0A8ADC
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            window.statusBarColor = android.graphics.Color.parseColor("#0A8ADC")
        }

        editTextCorreo = findViewById(R.id.editTextCorreo)
        editTextContrasenia = findViewById(R.id.editTextContrasenia)
        buttonLogin = findViewById(R.id.buttonLogin)
        buttonTogglePassword = findViewById(R.id.buttonTogglePassword)

        // Listener para el botón de inicio de sesión
        buttonLogin.setOnClickListener {
            val usuario = editTextCorreo.text.toString().trim()
            val password = editTextContrasenia.text.toString().trim()

            if (usuario.isNotEmpty() && password.isNotEmpty()) {
                login(usuario, password)
            } else {
                Toast.makeText(this, "Por favor, ingrese todos los datos", Toast.LENGTH_SHORT).show()
            }
        }

        // Listener para el botón de mostrar/ocultar contraseña
        buttonTogglePassword.setOnClickListener {
            togglePasswordVisibility()
        }
    }

    private fun togglePasswordVisibility() {
        if (isPasswordVisible) {
            // Ocultar contraseña
            editTextContrasenia.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            buttonTogglePassword.setImageResource(R.drawable.ic_eye) // Cambia al icono de ojo cerrado
        } else {
            // Mostrar contraseña
            editTextContrasenia.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            buttonTogglePassword.setImageResource(R.drawable.ic_eye_off) // Cambia al icono de ojo abierto
        }
        // Mover el cursor al final del texto
        editTextContrasenia.setSelection(editTextContrasenia.text.length)
        isPasswordVisible = !isPasswordVisible
    }

    private fun login(usuario: String, password: String) {
        val call = RetrofitClient.instanceL.getAdministradores(usuario, password) // Cambiado a getAdministradores
        call.enqueue(object : Callback<List<Usuarios>> { // Puedes renombrar Usuarios a Administradores si es necesario
            override fun onResponse(
                call: Call<List<Usuarios>>,
                response: Response<List<Usuarios>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val administradores = response.body() // Renombrado a administradores
                    if (administradores != null && administradores.isNotEmpty()) {
                        val administradorEncontrado = administradores.find { it.usuario == usuario && it.password == password }
                        if (administradorEncontrado != null) {
                            val intent = Intent(this@MainActivity, HabitacionesActivity::class.java)
                            intent.putExtra("USER_ID", administradorEncontrado.idUsuario)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@MainActivity, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                            Toast.makeText(this@MainActivity, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Error en la autenticación", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Usuarios>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
