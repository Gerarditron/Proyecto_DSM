package com.example.login_dsm

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.login_dsm.databinding.ActivityAddInvoiceBinding
import com.example.login_dsm.databinding.ActivityInvoiceRegisterBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.example.login_dsm.datos.Invoice
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.database.ktx.database
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.HashMap

class AddInvoiceActivity : AppCompatActivity() {
    private var edtNumero: EditText? = null
    private var edtTipo: EditText? = null
    private var edtFecha: EditText? = null
    private var edtCliente: EditText? = null
    private var edtConcepto: EditText? = null
    private var edtTotal: EditText? = null
    private var edtfoto: EditText? = null
    private var key = ""
    private var numero = ""
    private var tipo = ""
    private var fecha = ""
    private var cliente = ""
    private var concepto = ""
    private var total = ""
    private var accion = ""
    private lateinit var  database:DatabaseReference
    private val File = 1
    private val database1 = Firebase.database
    val myRef = database1.getReference("invoicesPictures")
    private lateinit var binding: ActivityAddInvoiceBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_invoice)
        binding = ActivityAddInvoiceBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btnSubirFoto.setOnClickListener {
            fileUpload()
        }
        inicializar()
    }
    fun fileUpload() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(intent, File)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == File) {
            if (resultCode == RESULT_OK) {
                val FileUri = data!!.data
                val Folder: StorageReference =
                    FirebaseStorage.getInstance().getReference().child("invoicesPictures")
                val file_name: StorageReference = Folder.child("file" + FileUri!!.lastPathSegment)
                file_name.putFile(FileUri).addOnSuccessListener { taskSnapshot ->
                    file_name.getDownloadUrl().addOnSuccessListener { uri ->
                        val hashMap =
                            HashMap<String, String>()
                        hashMap["link"] = java.lang.String.valueOf(uri)
                        myRef.setValue(hashMap)
                        Log.d("Mensaje", "Se subió correctamente")
                        Toast.makeText(
                            this,
                            "La imagen se subió correctamente!", Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
    private fun inicializar() {
        edtNumero = findViewById<EditText>(R.id.edtNumero)
        edtTipo = findViewById<EditText>(R.id.edtTipo)
        edtFecha = findViewById<EditText>(R.id.edtFecha)
        edtCliente = findViewById<EditText>(R.id.edtCliente)
        edtConcepto = findViewById<EditText>(R.id.edtConcepto)
        edtTotal = findViewById<EditText>(R.id.edtTotal)
        edtfoto = findViewById<EditText>(R.id.edtfoto)

        val edtNumero = findViewById<EditText>(R.id.edtNumero)
        val edtTipo = findViewById<EditText>(R.id.edtTipo)
        val edtFecha = findViewById<EditText>(R.id.edtFecha)
        val edtCliente = findViewById<EditText>(R.id.edtCliente)
        val edtConcepto = findViewById<EditText>(R.id.edtConcepto)
        val edtTotal = findViewById<EditText>(R.id.edtTotal)
        val edtfoto = findViewById<EditText>(R.id.edtfoto)

        // Obtención de datos que envia actividad anterior
        val datos: Bundle? = intent.getExtras()
        if (datos != null) {
            key = datos.getString("key").toString()
        }
        if (datos != null) {
            edtNumero.setText(intent.getStringExtra("numero").toString())
        }
        if (datos != null) {
            edtTipo.setText(intent.getStringExtra("tipo").toString())
        }
        if (datos != null) {
            edtFecha.setText(intent.getStringExtra("fecha").toString())
        }
        if (datos != null) {
            edtCliente.setText(intent.getStringExtra("cliente").toString())
        }
        if (datos != null) {
            edtConcepto.setText(intent.getStringExtra("concepto").toString())
        }
        if (datos != null) {
            edtTotal.setText(intent.getStringExtra("total").toString())
        }
        if (datos != null) {
            edtfoto.setText(intent.getStringExtra("foto").toString())
        }
        if (datos != null) {
            accion = datos.getString("accion").toString()
        }

    }

    fun guardar(v: View?) {
        val numero: String = edtNumero?.text.toString()
        val tipo: String = edtTipo?.text.toString()
        val fecha: String = edtFecha?.text.toString()
        val cliente: String = edtCliente?.text.toString()
        val concepto: String = edtConcepto?.text.toString()
        val total: String = edtTotal?.text.toString()
        val foto: String = edtfoto?.text.toString()

        database=FirebaseDatabase.getInstance().getReference("invoices")

        // Se forma objeto producto
        val invoice = Invoice(numero, tipo, fecha, cliente, concepto, total, foto)

        if (accion == "a") { //Agregar registro
            database.child(numero).setValue(invoice).addOnSuccessListener {
                Toast.makeText(this,"Se guardo con exito", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener{
                Toast.makeText(this,"Failed ", Toast.LENGTH_SHORT).show()
            }
        } else  // Editar registro
        {
            val key = database.child("numero").push().key
            if (key == null) {
                Toast.makeText(this,"Llave vacia", Toast.LENGTH_SHORT).show()
            }
            val invoicesValues = invoice.toMap()
            val childUpdates = hashMapOf<String, Any>(
                "$numero" to invoicesValues
            )
            database.updateChildren(childUpdates)
            Toast.makeText(this,"Se actualizo con exito", Toast.LENGTH_SHORT).show()
        }
        finish()
    }

    fun cancelar(v: View?) {
        finish()
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_sign_out->{
                FirebaseAuth.getInstance().signOut().also {
                    Toast.makeText(this, "Sesion cerrada", Toast.LENGTH_SHORT).show()

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