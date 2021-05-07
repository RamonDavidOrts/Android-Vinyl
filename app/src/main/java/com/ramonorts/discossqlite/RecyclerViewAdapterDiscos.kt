package com.ramonorts.discossqlite


import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.ramonorts.discossqlite.databinding.ItemDiscosBinding

class RecyclerViewAdapterDiscos : RecyclerView.Adapter<RecyclerViewAdapterDiscos.ViewHolder>() {

    lateinit var context: Context
    lateinit var cursor: Cursor

    fun RecyclerViewAdapterDiscos(context: Context, cursor: Cursor) {
        this.context = context
        this.cursor = cursor
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_discos, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        cursor.moveToPosition(position)

        holder.id = cursor.getInt(0)
        holder.ivPortada.setImageURI(Uri.parse(cursor.getString(1)))
        holder.tvTitulo.text = cursor.getString(2)
        holder.tvArtista.text = cursor.getString(3)
        holder.tvFormato.text = cursor.getString(4)
        holder.tvEstilo.text = cursor.getString(5)
        holder.tvFecha.text = cursor.getString(6)
        holder.tvEstudio.text = cursor.getString(7)
        holder.latitud = cursor.getDouble(8)
        holder.longitud = cursor.getDouble(9)
    }

    override fun getItemCount(): Int {
        if (cursor == null)
            return 0
        else
            return cursor.count
    }

    inner class ViewHolder: RecyclerView.ViewHolder, PopupMenu.OnMenuItemClickListener {
        var id : Int = 0
        val ivPortada : ImageView
        val tvTitulo : TextView
        val tvArtista : TextView
        val tvFormato : TextView
        val tvEstilo : TextView
        val tvFecha : TextView
        val tvEstudio : TextView
        var latitud: Double = 0.0
        var longitud: Double = 0.0

        constructor(view: View) : super(view) {
            val bindingItemsDisco = ItemDiscosBinding.bind(view)
            ivPortada = bindingItemsDisco.ivPortada
            tvTitulo = bindingItemsDisco.tvTitulo
            tvArtista = bindingItemsDisco.tvArtista
            tvFormato = bindingItemsDisco.tvFormato
            tvEstilo = bindingItemsDisco.tvEstilo
            tvFecha = bindingItemsDisco.tvFecha
            tvEstudio = bindingItemsDisco.tvEstudio

            view.setOnClickListener{
                Toast.makeText(context,
                    "Mantenga pulsado para opciones\n ${tvTitulo.text}",
                    Toast.LENGTH_SHORT).show()
            }

            view.setOnLongClickListener {
                val popupMenu = PopupMenu(view.context, view)
                popupMenu.inflate(R.menu.menu_contextual)
                popupMenu.setOnMenuItemClickListener(this)
                popupMenu.show()
                true
            }

            tvEstudio.setOnClickListener {
                val intentMapa = Intent(context, MapsActivity::class.java)
                    .apply {
                        putExtra("estudio", tvEstudio.text.toString())
                        putExtra("latitud", latitud)
                        putExtra("longitud", longitud)
                    }
                context.startActivity(intentMapa)
                true
            }
        }

        override fun onMenuItemClick(item: MenuItem?): Boolean {
            return when (item!!.itemId) {
                R.id.opEditar -> {
                    val intentEditar = Intent(context, AgregarActivity::class.java)
                        .apply { putExtra("id", id) }
                    context.startActivity(intentEditar)
                    true
                }
                R.id.opBorrar -> {
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("Borrar")
                    builder.setMessage("¿Estás seguro de borrar '${tvTitulo.text}'?")
                    builder.setPositiveButton(android.R.string.ok) {
                        dialogo, which ->
                            val dbHelper = SQLiteHelper(context)
                            dbHelper.borrarDisco(id)
                            (context as MainActivity).cargaDiscos()
                    }
                    builder.setNegativeButton(android.R.string.cancel, null)
                    builder.show()
                    true
                }
                else -> false
            }
        }


    }
}