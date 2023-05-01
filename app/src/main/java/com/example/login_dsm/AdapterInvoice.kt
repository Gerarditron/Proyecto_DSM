package com.example.login_dsm

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.database.*

import com.example.login_dsm.datos.Invoice

class AdapterInvoice(private val context: Activity, var invoices: List<Invoice>) :
    ArrayAdapter<Invoice?>(context, R.layout.invoice_layout, invoices) {
    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        // Método invocado tantas veces como elementos tenga la coleccion personas
        // para formar a cada item que se visualizara en la lista personalizada
        val layoutInflater = context.layoutInflater
        var rowview: View? = null
        // optimizando las diversas llamadas que se realizan a este método
        // pues a partir de la segunda llamada el objeto view ya viene formado
        // y no sera necesario hacer el proceso de "inflado" que conlleva tiempo y
        // desgaste de bateria del dispositivo
        rowview = view ?: layoutInflater.inflate(R.layout.invoice_layout, null)
        val tvNumero = rowview!!.findViewById<TextView>(R.id.tvNumero)
        val tvTipo = rowview.findViewById<TextView>(R.id.tvTipo)
        val tvFecha = rowview!!.findViewById<TextView>(R.id.tvFecha)
        val tvCliente = rowview.findViewById<TextView>(R.id.tvCliente)
        val tvConcepto = rowview.findViewById<TextView>(R.id.tvConcepto)
        val tvTotal = rowview.findViewById<TextView>(R.id.tvTotal)
        val tvFoto = rowview.findViewById<ImageView>(R.id.ivFotoProd)
        tvNumero.text = "Numero : " + invoices[position].numero
        tvTipo.text = "Tipo : " + invoices[position].tipo
        tvFecha.text = "Fecha : " + invoices[position].fecha
        tvCliente.text = "Cliente : " + invoices[position].cliente
        tvConcepto.text = "Concepto : " + invoices[position].concepto
        tvTotal.text = "Total : " + invoices[position].total
        Glide.with(rowview).load(invoices[position].foto).into(tvFoto)
        return rowview
    }
}