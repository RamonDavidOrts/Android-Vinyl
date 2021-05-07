package com.ramonorts.discossqlite

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.ramonorts.discossqlite.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.layout_busca.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dbHelper: SQLiteHelper
    private val adaptador = RecyclerViewAdapterDiscos()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = SQLiteHelper(this)
        binding.rvDiscos.setHasFixedSize(true)
        binding.rvDiscos.layoutManager = LinearLayoutManager(this)

        binding.fbtnAgregar.setOnClickListener {
            val intentAgregar = Intent(this, AgregarActivity::class.java)
            startActivity(intentAgregar)
        }
    }

    override fun onResume() {
        super.onResume()
        cargaDiscos()
    }

    fun cargaDiscos() {
        val cursor = dbHelper.obtenerTodosDiscos(SQLiteHelper.TABLA_DISCOS, SQLiteHelper.CAMPO_TITULO)
        adaptador.RecyclerViewAdapterDiscos(this, cursor)
        binding.rvDiscos.adapter = adaptador
    }

    fun cargaBusqueda(campo: String, campoValor: String) {
        val cursor = dbHelper.obtenerCursor(SQLiteHelper.TABLA_DISCOS, campo,
            campoValor, campo)
        adaptador.RecyclerViewAdapterDiscos(this, cursor)
        binding.rvDiscos.adapter = adaptador
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.opBuscar -> {
                val adaptSpBusca = ArrayAdapter(this,
                    android.R.layout.simple_spinner_item, arrayOf(
                        SQLiteHelper.CAMPO_TITULO.capitalize(),
                        SQLiteHelper.CAMPO_ARTISTA.capitalize()))
                adaptSpBusca.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                val builder = AlertDialog.Builder(this)
                builder.setTitle("Buscar")
                val dView = layoutInflater.inflate(R.layout.layout_busca, null)
                builder.setView(dView)
                val dSpinner = dView.findViewById<Spinner>(R.id.spBusca)
                dSpinner.adapter = adaptSpBusca
                builder.setPositiveButton(android.R.string.ok) {
                    dialogo, _ ->
                        cargaBusqueda(
                            (dialogo as AlertDialog).spBusca.selectedItem.toString().toLowerCase(),
                            (dialogo as AlertDialog).etBusca.text.toString())
                }
                builder.setNegativeButton(android.R.string.cancel, null)
                builder.show()
                true
            }
            R.id.opMostrarTodos -> {
                cargaDiscos()
                true
            }
            R.id.opMostrar -> {
                true
            }
            else -> {
                cargaBusqueda(SQLiteHelper.CAMPO_ESTILO, item.title.toString())
                true
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        dbHelper.cerrarDB()
    }
}