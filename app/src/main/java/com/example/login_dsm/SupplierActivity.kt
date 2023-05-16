package com.example.login_dsm

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import com.example.login_dsm.datos.Supplier
import com.google.firebase.auth.FirebaseAuth

class SupplierActivity : AppCompatActivity() {
    var consultaOrdenada: Query = SupplierActivity.refSuppliers.orderByChild("nrc")
    var suppliers: MutableList<Supplier>? = null
    var listSuppliers: ListView? = null
    var accion=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_supplier)
        val bundle = intent.extras
        accion = bundle?.getString("Accion").toString()
        inicializar()
    }
    private fun inicializar() {
        val fab_agregar: FloatingActionButton = findViewById<FloatingActionButton>(R.id.fab_agregar)
        listSuppliers = findViewById<ListView>(R.id.ListSupplier)

        // Cuando el usuario haga clic en la lista (para editar registro)
        listSuppliers!!.setOnItemClickListener(object : AdapterView.OnItemClickListener {
            override fun onItemClick(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                val intent = Intent(getBaseContext(), AddSupplier::class.java)
                intent.putExtra("accion", "e") // Editar
                intent.putExtra("key", suppliers!![i].key)
                intent.putExtra("nombrecomercial", suppliers!![i].nombrecomercial)
                intent.putExtra("nombrelegal", suppliers!![i].nombrelegal)
                intent.putExtra("nrc", suppliers!![i].nrc)
                intent.putExtra("nit", suppliers!![i].nit)
                intent.putExtra("telefonos", suppliers!![i].telefonos)
                intent.putExtra("pais", suppliers!![i].pais)
                startActivity(intent)
            }
        })

        // Cuando el usuario hace un LongClic (clic sin soltar elemento por mas de 2 segundos)
        // Es por que el usuario quiere eliminar el registro
        listSuppliers!!.onItemLongClickListener = object : AdapterView.OnItemLongClickListener {
            override fun onItemLongClick(
                adapterView: AdapterView<*>?,
                view: View,
                position: Int,
                l: Long
            ): Boolean {
                // Preparando cuadro de dialogo para preguntar al usuario
                // Si esta seguro de eliminar o no el registro
                val ad = AlertDialog.Builder(this@SupplierActivity)
                ad.setMessage(getString(R.string.label_whatdoyouneed))
                    .setTitle("Menu")
                ad.setPositiveButton(getString(R.string.label_button1)
                ) { dialog, id ->
                    suppliers!![position].nombrecomercial?.let {
                        SupplierActivity.refSuppliers.child(it).removeValue()
                    }
                    Toast.makeText(
                        this@SupplierActivity,
                        getString(R.string.label_record_deleted), Toast.LENGTH_SHORT
                    ).show()
                }
                ad.setNegativeButton(getString(R.string.label_button2), object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface, id: Int) {
                        Toast.makeText(
                            this@SupplierActivity,
                            getString(R.string.label_record_deleted_canel), Toast.LENGTH_SHORT
                        ).show()
                    }
                })
                ad.setNegativeButton(getString(R.string.label_button3), object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface, id: Int) {
                        val intent = Intent(getBaseContext(), AddInvoiceActivity::class.java)
                        intent.putExtra("NombreComercial", suppliers!![position].nombrelegal)
                        startActivity(intent)
                        finish()
                    }
                })
                ad.show()
                return true
            }
        }

        fab_agregar.setOnClickListener(View.OnClickListener {
            // Cuando el usuario quiere agregar un nuevo registro
            val i = Intent(getBaseContext(), AddSupplier::class.java)
            i.putExtra("accion", "a") // Agregar
            i.putExtra("key", "")
            i.putExtra("nombrecomercial", "")
            i.putExtra("nombrelegal", "")
            i.putExtra("nrc", "")
            i.putExtra("nit", "")
            i.putExtra("telefonos", "")
            i.putExtra("pais", "")
            startActivity(i)
        })
        suppliers = ArrayList<Supplier>()

        // Cambiarlo refProductos a consultaOrdenada para ordenar lista
        consultaOrdenada.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Procedimiento que se ejecuta cuando hubo algun cambio
                // en la base de datos
                // Se actualiza la coleccion de Productos
                suppliers!!.removeAll(suppliers!!)
                for (dato in dataSnapshot.getChildren()) {
                    val supplier: Supplier? = dato.getValue(Supplier::class.java)
                    supplier?.key(dato.key)
                    if (supplier != null) {
                        suppliers!!.add(supplier)
                    }
                }
                val adapter = AdapterSupplier(
                    this@SupplierActivity,
                    suppliers as ArrayList<Supplier>
                )
                listSuppliers!!.adapter = adapter
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    companion object {
        var database: FirebaseDatabase = FirebaseDatabase.getInstance()
        var refSuppliers: DatabaseReference = database.getReference("suppliers")
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