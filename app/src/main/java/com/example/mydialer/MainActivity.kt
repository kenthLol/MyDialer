package com.example.mydialer

import android.Manifest
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.ContactsContract
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity()
{
    private lateinit var pantalla: TextView
    private var numeroMarcado = ""
    private var specialCharPressed = false

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        pantalla = findViewById(R.id.pantalla)

        val botonesNumericos = Array<Button>(10) { i ->
            val id = resources.getIdentifier("num$i", "id", packageName)
            findViewById<Button>(id).apply {
                setOnClickListener { agregarDigito(i) }
                setOnLongClickListener {
                    when (i) {
                        0 -> agregarSignoMas()
                        1 -> llamarBuzonVozPredeterminado()
                    }
                    true
                }
            }
        }

        findViewById<ImageButton>(R.id.borrar).setOnClickListener { borrarUltimoDigito() }
        findViewById<Button>(R.id.Erase).setOnClickListener { numeroMarcado = ""; actualizarPantalla() }
        findViewById<Button>(R.id.llamada).setOnClickListener { realizarLlamada() }
        findViewById<Button>(R.id.SendMsj).setOnClickListener { enviarMensaje() }
        findViewById<Button>(R.id.addcontact).setOnClickListener { agregarAContactos() }

        findViewById<Button>(R.id.asterisco).setOnClickListener { agregarCaracterEspecial("*") }
        findViewById<Button>(R.id.numeral).setOnClickListener { agregarCaracterEspecial("#") }
    }

    private fun agregarCaracterEspecial(caracter: String) {
        numeroMarcado += caracter
        specialCharPressed = true
        actualizarPantalla()
    }

    private fun agregarDigito(digito: Int) {
        numeroMarcado += digito.toString()
        actualizarPantalla()
    }

    private fun agregarSignoMas() {
        numeroMarcado = "+$numeroMarcado"
        actualizarPantalla()
    }

    private fun llamarBuzonVozPredeterminado() {
        Toast.makeText(this, "Llamando al buzón de voz predeterminado", Toast.LENGTH_SHORT).show()
    }

    private fun borrarUltimoDigito() {
        if (numeroMarcado.isNotEmpty()) {
            numeroMarcado = numeroMarcado.dropLast(1)
            actualizarPantalla()
        }
    }

    private fun realizarLlamada() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), REQUEST_CALL_PHONE)
        } else {
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel:$numeroMarcado")
            startActivity(intent)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CALL_PHONE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                realizarLlamada()
            } else {
                Toast.makeText(this, "Permiso de llamada denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun enviarMensaje() {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("smsto:$numeroMarcado")
        startActivity(intent)
    }

    private fun agregarAContactos() {
        val intent = Intent(Intent.ACTION_INSERT)
        intent.type = ContactsContract.Contacts.CONTENT_TYPE
        intent.putExtra(ContactsContract.Intents.Insert.PHONE, numeroMarcado)
        startActivity(intent)
    }

    private fun actualizarPantalla() {
        pantalla.text = numeroMarcado
    }

    companion object {
        private const val REQUEST_CALL_PHONE = 1
    }

}