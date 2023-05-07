package com.example.login_dsm

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import com.example.login_dsm.datos.Invoice
import com.google.firebase.auth.FirebaseAuth

class InvoiceActivity : AppCompatActivity() {
    var consultaOrdenada: Query = InvoiceActivity.refInvoices.orderByChild("numero")
    var invoices: MutableList<Invoice>? = null
    var listInvoices: ListView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invoice)
        inicializar()
    }
    private fun inicializar() {
        val fab_agregar: FloatingActionButton = findViewById<FloatingActionButton>(R.id.fab_agregar)
        listInvoices = findViewById<ListView>(R.id.ListInvoice)

        // Cuando el usuario haga clic en la lista (para editar registro)
        listInvoices!!.setOnItemClickListener(object : AdapterView.OnItemClickListener {
            override fun onItemClick(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                val intent = Intent(getBaseContext(), AddInvoiceActivity::class.java)
                intent.putExtra("accion", "e") // Editar
                intent.putExtra("key", invoices!![i].key)
                intent.putExtra("numero", invoices!![i].numero)
                intent.putExtra("tipo", invoices!![i].tipo)
                intent.putExtra("fecha", invoices!![i].fecha)
                intent.putExtra("cliente", invoices!![i].cliente)
                intent.putExtra("concepto", invoices!![i].concepto)
                intent.putExtra("total", invoices!![i].total)
                intent.putExtra("foto", invoices!![i].foto)
                intent.putExtra("tipoMov",invoices!![i].tipoMov)
                startActivity(intent)
            }
        })

        // Cuando el usuario hace un LongClic (clic sin soltar elemento por mas de 2 segundos)
        // Es por que el usuario quiere eliminar el registro
        listInvoices!!.onItemLongClickListener = object : AdapterView.OnItemLongClickListener {
            override fun onItemLongClick(
                adapterView: AdapterView<*>?,
                view: View,
                position: Int,
                l: Long
            ): Boolean {
                // Preparando cuadro de dialogo para preguntar al usuario
                // Si esta seguro de eliminar o no el registro
                val ad = AlertDialog.Builder(this@InvoiceActivity)
                ad.setMessage(getString(R.string.label_whatdoyouneed))
                    .setTitle("Menu")
                ad.setPositiveButton(getString(R.string.label_button1)
                ) { dialog, id ->
                    invoices!![position].numero?.let {
                        InvoiceActivity.refInvoices.child(it).removeValue()
                    }
                    Toast.makeText(
                        this@InvoiceActivity,
                        getString(R.string.label_record_deleted), Toast.LENGTH_SHORT
                    ).show()
                }
                ad.setNegativeButton(getString(R.string.label_button2), object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface, id: Int) {
                        Toast.makeText(
                            this@InvoiceActivity,
                            getString(R.string.label_record_deleted_canel), Toast.LENGTH_SHORT
                        ).show()
                    }
                })
                ad.show()
                return true
            }
        }

        fab_agregar.setOnClickListener(View.OnClickListener {
            // Cuando el usuario quiere agregar un nuevo registro
            val i = Intent(getBaseContext(), AddInvoiceActivity::class.java)
            i.putExtra("accion", "a") // Agregar
            i.putExtra("key", "")
            i.putExtra("numero", "")
            i.putExtra("tipo", "")
            i.putExtra("fecha", "")
            i.putExtra("cliente", "")
            i.putExtra("concepto", "")
            i.putExtra("total", "")
            i.putExtra("foto", "")
            startActivity(i)
        })
        invoices = ArrayList<Invoice>()

        // Cambiarlo a consultaOrdenada para ordenar lista
        consultaOrdenada.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Procedimiento que se ejecuta cuando hubo algun cambio
                // en la base de datos
                // Se actualiza la coleccion de Productos
                invoices!!.removeAll(invoices!!)
                for (dato in dataSnapshot.getChildren()) {
                    val invoice: Invoice? = dato.getValue(Invoice::class.java)
                    invoice?.key(dato.key)

                    //Filtrando la informacion de la lista solo para el usuario Logrado
                    var auth = FirebaseAuth.getInstance()
                    var userIDLogged : String = auth.currentUser?.uid.toString()
                    val userIDFact : String = invoice?.userID.toString()


                    if (invoice != null && userIDLogged == userIDFact ) {
                        invoices!!.add(invoice)
                    }
                }
                val adapter = AdapterInvoice(
                    this@InvoiceActivity,
                    invoices as ArrayList<Invoice>
                )
                listInvoices!!.adapter = adapter
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    companion object {
        var database: FirebaseDatabase = FirebaseDatabase.getInstance()
        var refInvoices: DatabaseReference = database.getReference("invoices")
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_sign_out->{
                FirebaseAuth.getInstance().signOut().also {
                    Toast.makeText(this, getString(R.string.menu_close_session), Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, SignInActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }

        }
        return super.onOptionsItemSelected(item)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

}