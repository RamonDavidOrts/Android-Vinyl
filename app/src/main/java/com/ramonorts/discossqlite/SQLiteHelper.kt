package com.ramonorts.discossqlite

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SQLiteHelper(context: Context) :SQLiteOpenHelper(
            context, "discos.db", null, 1) {

    private lateinit var db : SQLiteDatabase

    companion object {
        val TABLA_DISCOS = "discos"
        val CAMPO_ID = "_id"
        val CAMPO_PORTADA = "portada"
        val CAMPO_TITULO = "titulo"
        val CAMPO_ARTISTA = "artista"
        val CAMPO_FORMATO = "formato"
        val CAMPO_ESTILO = "estilo"
        val CAMPO_FECHA = "fecha"
        val CAMPO_ESTUDIO = "estudio"
        val CAMPO_LATITUD = "latitud"
        val CAMPO_LONGITUD = "longitud"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val ordenCreacion = "CREATE TABLE $TABLA_DISCOS " +
                "($CAMPO_ID INTEGER PRIMARY KEY AUTOINCREMENT, $CAMPO_PORTADA TEXT, " +
                "$CAMPO_TITULO TEXT, $CAMPO_ARTISTA TEXT, $CAMPO_FORMATO TEXT, " +
                "$CAMPO_ESTILO TEXT, $CAMPO_FECHA TEXT, $CAMPO_ESTUDIO TEXT, " +
                "$CAMPO_LATITUD REAL, $CAMPO_LONGITUD REAL)"
        db!!.execSQL(ordenCreacion)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val ordenBorrado = "DROP TABLE IF EXISTS discos"
        db!!.execSQL(ordenBorrado)
        onCreate(db)
    }

    fun obtenerTodosDiscos(tabla: String, campoOrden: String): Cursor {
        db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $tabla ORDER BY $campoOrden", null)
        return cursor
    }

    fun obtenerPorId(tabla: String, id: Int) : Cursor {
        db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $tabla WHERE $CAMPO_ID = $id", null)
        cursor.moveToFirst()
        return cursor
    }

    fun obtenerCursor(tabla: String, campo: String, campoValor: String,
                      campoOrden: String): Cursor {
        db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $tabla " +
                                "WHERE $campo LIKE '%$campoValor%' " +
                                "ORDER BY $campoOrden", null)
        return cursor
    }

    fun agregarDisco(portada: String, titulo: String, artista: String, formato: String,
            estilo: String, fecha: String, estudio: String, latitud: Double, longitud: Double) {
        val datos = ContentValues()
        datos.put(CAMPO_PORTADA, portada)
        datos.put(CAMPO_TITULO, titulo)
        datos.put(CAMPO_ARTISTA, artista)
        datos.put(CAMPO_FORMATO,formato)
        datos.put(CAMPO_ESTILO, estilo)
        datos.put(CAMPO_FECHA, fecha)
        datos.put(CAMPO_ESTUDIO, estudio)
        datos.put(CAMPO_LATITUD, latitud)
        datos.put(CAMPO_LONGITUD, longitud)

        val db = this.writableDatabase
        db.insert(TABLA_DISCOS, null, datos)
        db.close()
    }

    fun editarDisco(id: Int, portada: String, titulo: String, artista: String, formato: String,
            estilo: String, fecha: String, estudio: String, latitud: Double, longitud: Double) {
        val args = arrayOf(id.toString())
        val datos = ContentValues()
        datos.put(CAMPO_PORTADA, portada)
        datos.put(CAMPO_TITULO, titulo)
        datos.put(CAMPO_ARTISTA, artista)
        datos.put(CAMPO_FORMATO,formato)
        datos.put(CAMPO_ESTILO, estilo)
        datos.put(CAMPO_FECHA, fecha)
        datos.put(CAMPO_ESTUDIO, estudio)
        datos.put(CAMPO_LATITUD, latitud)
        datos.put(CAMPO_LONGITUD, longitud)
        val db = this.writableDatabase
        db.update(TABLA_DISCOS, datos, "$CAMPO_ID = ?", args)
        db.close()
    }

    fun borrarDisco(id: Int) : Int {
        val args = arrayOf(id.toString())
        val db = this.writableDatabase
        val borrados = db.delete(TABLA_DISCOS, "$CAMPO_ID = ?", args)
        db.close()
        return borrados
    }

    fun cerrarDB() {
        db.close()
    }


}