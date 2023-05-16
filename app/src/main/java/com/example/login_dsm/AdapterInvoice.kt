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
import android.content.res.Resources

class AdapterInvoice(private val context: Activity, var invoices: List<Invoice>) :
    ArrayAdapter<Invoice?>(context, R.layout.invoice_layout, invoices) {
    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        // Método invocado tantas veces como elementos tenga la coleccion
        val layoutInflater = context.layoutInflater
        var rowview: View? = null
        // optimizando las diversas llamadas que se realizan a este método
        // pues a partir de la segunda llamada el objeto view ya viene formado
        rowview = view ?: layoutInflater.inflate(R.layout.invoice_layout, null)
        val tvNumero = rowview!!.findViewById<TextView>(R.id.tvNumero)
        val tvTipo = rowview.findViewById<TextView>(R.id.tvTipo)
        val tvFecha = rowview!!.findViewById<TextView>(R.id.tvFecha)
        val tvCliente = rowview.findViewById<TextView>(R.id.tvCliente)
        val tvConcepto = rowview.findViewById<TextView>(R.id.tvConcepto)
        val tvTotal = rowview.findViewById<TextView>(R.id.tvTotal)
        val tvFoto = rowview.findViewById<ImageView>(R.id.ivFotoProd)
        //Tipo de Movimiento CARGO o ABONO
        var tvTipoMov : String? = null
        tvTipoMov = invoices[position].tipoMov.toString()
        //Declarando todos los valores en la pantalla
        tvNumero.text = invoices[position].numero
        tvFecha.text = invoices[position].fecha
        tvTipo.text = invoices[position].tipo
        tvCliente.text = invoices[position].cliente
        tvConcepto.text = invoices[position].concepto
        Glide.with(rowview).load(invoices[position].foto).into(tvFoto)
        tvTotal.text = "$ " + invoices[position].total
        //Agregando color al total si es un ABONO o un CARGO
        if (tvTipoMov == "PAY") {
            tvTotal.setTextColor(context.getColor(R.color.colorGreen))
        } else if (tvTipoMov == "POST"){
            tvTotal.setTextColor(context.getColor(R.color.colorRed))
        }

        return rowview
    }
}