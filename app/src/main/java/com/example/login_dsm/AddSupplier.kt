package com.example.login_dsm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import com.example.login_dsm.databinding.ActivityAddInvoiceBinding
import com.example.login_dsm.datos.Invoice
import com.example.login_dsm.datos.Supplier
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class AddSupplier : AppCompatActivity() {
    private var edtTradename: EditText? = null
    private var edtLegalname: EditText? = null
    private var edtNRC: EditText? = null
    private var edtNIT: EditText? = null
    private var edtPhone: EditText? = null
    private var edtDetails: EditText? = null
    private var key = ""
    private var accion = ""
    private lateinit var  database: DatabaseReference
    private val File = 1
    private val database1 = Firebase.database
    private lateinit var binding: ActivityAddInvoiceBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_supplier)
        inicializar()
    }
    private fun inicializar() {
        edtTradename = findViewById<EditText>(R.id.edtTradename)
        edtLegalname = findViewById<EditText>(R.id.edtLegalName)
        edtNRC = findViewById<EditText>(R.id.edtNRC)
        edtNIT = findViewById<EditText>(R.id.edtNIT)
        edtPhone = findViewById<EditText>(R.id.edtPhone)
        edtDetails = findViewById<EditText>(R.id.edtDetails)

        val edtTradename = findViewById<EditText>(R.id.edtTradename)
        val edtLegalname = findViewById<EditText>(R.id.edtLegalName)
        val edtNRC = findViewById<EditText>(R.id.edtNRC)
        val edtNIT = findViewById<EditText>(R.id.edtNIT)
        val edtPhone = findViewById<EditText>(R.id.edtPhone)
        val edtDetails = findViewById<EditText>(R.id.edtDetails)

        // Obtención de datos que envia actividad anterior
        val datos: Bundle? = intent.getExtras()
        if (datos != null) {
            key = datos.getString("key").toString()
        }
        if (datos != null) {
            edtTradename.setText(intent.getStringExtra("nombrecomercial").toString())
        }
        if (datos != null) {
            edtLegalname.setText(intent.getStringExtra("nombrelegal").toString())
        }
        if (datos != null) {
            edtNRC.setText(intent.getStringExtra("nrc").toString())
        }
        if (datos != null) {
            edtNIT.setText(intent.getStringExtra("nit").toString())
        }
        if (datos != null) {
            edtPhone.setText(intent.getStringExtra("telefonos").toString())
        }
        if (datos != null) {
            edtDetails.setText(intent.getStringExtra("pais").toString())
        }
        if (datos != null) {
            accion = datos.getString("accion").toString()
        }

    }

    fun guardar(v: View?) {
        val tradename: String = edtTradename?.text.toString()
        val legalname: String = edtLegalname?.text.toString()
        val nrc: String = edtNRC?.text.toString()
        val nit: String = edtNIT?.text.toString()
        val phone: String = edtPhone?.text.toString()
        val details: String = edtDetails?.text.toString()
        val userID: String = FirebaseAuth.getInstance().currentUser?.uid.toString()

        database= FirebaseDatabase.getInstance().getReference("suppliers")

        // Se forma objeto producto
        val supplier = Supplier(tradename, legalname, nrc, nit, phone, details, userID)

        if (accion == "a") { //Agregar registro
            database.child(tradename).setValue(supplier).addOnSuccessListener {
                Toast.makeText(this,getString(R.string.toast_addsupplier_uploadSuccess), Toast.LENGTH_SHORT).show()
            }.addOnFailureListener{
                Toast.makeText(this,getString(R.string.toast_addsupplier_uploadFailed), Toast.LENGTH_SHORT).show()
            }
        } else  // Editar registro
        {
            val key = database.child("nrc").push().key
            if (key == null) {
                Toast.makeText(this,getString(R.string.toast_addinvoice_keyEmpty), Toast.LENGTH_SHORT).show()
            }
            val suppliersValues = supplier.toMap()
            val childUpdates = hashMapOf<String, Any>(
                "$nrc" to suppliersValues
            )
            database.updateChildren(childUpdates)
            Toast.makeText(this,getString(R.string.toast_addsupplier_uploadSuccess), Toast.LENGTH_SHORT).show()
        }
        finish()
    }

    fun cancelar(v: View?) {
        finish()
    }
}