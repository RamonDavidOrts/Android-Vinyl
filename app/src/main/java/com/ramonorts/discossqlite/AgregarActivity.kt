package com.ramonorts.discossqlite

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ramonorts.discossqlite.databinding.ActivityAgregarBinding
import java.text.SimpleDateFormat
import java.util.*
import java.util.jar.Manifest

class AgregarActivity : AppCompatActivity() {

    private val REQUEST_GALLERY = 1001
    private val REQUEST_MAP = 1002
    private lateinit var binding: ActivityAgregarBinding
    private lateinit var dbHelper: SQLiteHelper
    private var uriImagen = "android.resource://com.ramonorts.discossqlite/drawable/vinyl"
    private var latitud : Double = 0.0
    private var longitud : Double = 0.0

    val formatos = arrayOf("CD", "LP", "SP", "Digital", "Cassette")
    val estilos = arrayOf("Rock", "Metal", "Soundtrack", "Pop", "Jazz", "Clásica")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgregarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = SQLiteHelper(this)

        val adaptadorFormato = ArrayAdapter(this,
                android.R.layout.simple_spinner_item, formatos)
        adaptadorFormato.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spFormato.adapter = adaptadorFormato

        val adaptadorEstilo = ArrayAdapter(this,
                android.R.layout.simple_spinner_item, estilos)
        adaptadorEstilo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spEstilo.adapter = adaptadorEstilo

        val id = intent.getIntExtra("id", 0)

        if (id != 0) {
            val cursor = dbHelper.obtenerPorId(SQLiteHelper.TABLA_DISCOS, id)
            uriImagen = cursor.getString(1)
            binding.ivAgregarPortada.setImageURI(Uri.parse(cursor.getString(1)))
            binding.etTitulo.setText(cursor.getString(2))
            binding.etArtista.setText(cursor.getString(3))
            binding.spFormato.setSelection(
                adaptadorFormato.getPosition(cursor.getString(4)))
            binding.spEstilo.setSelection(
                adaptadorEstilo.getPosition(cursor.getString(5)))
            binding.tvAgregarFecha.text = cursor.getString(6)
            binding.tvAgregarEstudio.text = cursor.getString(7)
            latitud = cursor.getDouble(8)
            longitud = cursor.getDouble(9)
            binding.btnAceptar.setText("Modificar")
            dbHelper.cerrarDB()
        } else {
            binding.ivAgregarPortada.setImageURI(Uri.parse(uriImagen))
        }

        binding.ivAgregarPortada.setOnClickListener {
            mostrarGaleria()
        }

        val formatter = SimpleDateFormat("dd/MM/yyyy")
        var fechaEscogida = Calendar.getInstance()
        val listenerFecha = DatePickerDialog.OnDateSetListener {
                datePicker, anyo, mes, dia ->
            fechaEscogida.clear()
            fechaEscogida.set(Calendar.YEAR, anyo)
            fechaEscogida.set(Calendar.MONTH, mes)
            fechaEscogida.set(Calendar.DAY_OF_MONTH, dia)
            binding.tvAgregarFecha.text = formatter.format(fechaEscogida.time)
        }
        binding.tvAgregarFecha.setOnClickListener {
            DatePickerDialog(this,
                listenerFecha,
                fechaEscogida.get(Calendar.YEAR),
                fechaEscogida.get(Calendar.MONTH),
                fechaEscogida.get(Calendar.DAY_OF_MONTH)).show()
        }

        binding.tvAgregarEstudio.setOnClickListener {
            val intentMapa = Intent(this, MapsActivity::class.java)
                .apply {
                    putExtra("editar", true)
                    putExtra("estudio", binding.tvAgregarEstudio.text.toString())
                    putExtra("latitud", latitud)
                    putExtra("longitud", longitud)
                }
            startActivityForResult(intentMapa, REQUEST_MAP)
        }

        binding.btnAceptar.setOnClickListener {
            if(binding.etTitulo.text.isNotBlank() &&
                    binding.etArtista.text.isNotBlank() &&
                    binding.tvAgregarFecha.text.isNotBlank() )
            {
                if ((id != 0)) {
                    dbHelper.editarDisco(id,
                        uriImagen,
                        binding.etTitulo.text.toString(),
                        binding.etArtista.text.toString(),
                        binding.spFormato.selectedItem.toString(),
                        binding.spEstilo.selectedItem.toString(),
                        binding.tvAgregarFecha.text.toString(),
                        binding.tvAgregarEstudio.text.toString(),
                        latitud,
                        longitud)
                } else {
                    dbHelper.agregarDisco(
                        uriImagen,
                        binding.etTitulo.text.toString(),
                        binding.etArtista.text.toString(),
                        binding.spFormato.selectedItem.toString(),
                        binding.spEstilo.selectedItem.toString(),
                        binding.tvAgregarFecha.text.toString(),
                        binding.tvAgregarEstudio.text.toString(),
                        latitud,
                        longitud)
                }
                binding.etTitulo.text.clear()
                binding.etArtista.text.clear()
                binding.tvAgregarFecha.text = ""
                binding.tvAgregarEstudio.text = ""
                latitud = 0.0
                longitud = 0.0
                Toast.makeText(this,
                        "Guardado", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(this,
                    "Hay campos vacíos", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun mostrarGaleria() {
        if (ContextCompat.checkSelfPermission(this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_GALLERY)
        } else {
            val intentGaleria = Intent(Intent.ACTION_GET_CONTENT)
            intentGaleria.type = "image/*"
            startActivityForResult(intentGaleria, REQUEST_GALLERY)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_GALLERY) {
            uriImagen = data?.data.toString()
            binding.ivAgregarPortada.setImageURI(data?.data)
        }

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_MAP) {
            binding.tvAgregarEstudio.text = data?.getStringExtra("estudio")
            latitud = data?.getDoubleExtra("latitud", 0.0)!!
            longitud = data?.getDoubleExtra("longitud", 0.0)
        }
    }
}