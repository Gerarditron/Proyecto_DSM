package com.example.login_dsm

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.example.login_dsm.datos.Supplier
import android.content.res.Resources

class AdapterSupplier(private val context: Activity, var suppliers: List<Supplier>) :
    ArrayAdapter<Supplier?>(context, R.layout.supplier_layout, suppliers) {
    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        // Método invocado tantas veces como elementos tenga la coleccion
        val layoutInflater = context.layoutInflater
        var rowview: View? = null
        // optimizando las diversas llamadas que se realizan a este método
        // pues a partir de la segunda llamada el objeto view ya viene formado
        rowview = view ?: layoutInflater.inflate(R.layout.invoice_layout, null)
        val tvNombreComercial = rowview!!.findViewById<TextView>(R.id.tvTradename)
        val tvNombrelegal = rowview.findViewById<TextView>(R.id.tvLegalName)
        val tvNRC = rowview!!.findViewById<TextView>(R.id.tvNRC)
        val tvNIT = rowview.findViewById<TextView>(R.id.tvNIT)
        val tvTelefonos = rowview.findViewById<TextView>(R.id.tvphone)
        val tvDetalles = rowview.findViewById<TextView>(R.id.tvDetails)
        //val tvFoto = rowview.findViewById<ImageView>(R.id.ivFotoProd)
        //Declarando todos los valores en la pantalla
        tvNombreComercial.text = suppliers[position].nombrecomercial
        tvNombrelegal.text = suppliers[position].nombrelegal
        tvNRC.text = suppliers[position].nrc
        tvNIT.text = suppliers[position].nit
        tvTelefonos.text = suppliers[position].telefonos
        //Glide.with(rowview).load(invoices[position].foto).into(tvFoto)
        tvDetalles.text = suppliers[position].pais

        return rowview
    }
}