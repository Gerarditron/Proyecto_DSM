package com.example.login_dsm

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
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
import android.widget.*
import android.content.Context
import android.provider.MediaStore.Audio.Radio
import android.renderscript.ScriptGroup.Input
import android.text.InputType
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputLayout
import java.security.KeyStore.TrustedCertificateEntry
import java.util.Random

class AddInvoiceActivity : AppCompatActivity() {
    private lateinit var edtNumero: EditText
    private lateinit var edtTipo: EditText
    private lateinit var edtFecha: EditText
    private lateinit var edtCliente: EditText
    private lateinit var edtConcepto: EditText
    private lateinit var edtTotal: EditText
    private lateinit var edtfoto: EditText
    private lateinit var txtTipFact: TextInputLayout
    private lateinit var rdGroup: RadioGroup
    private lateinit var rdPay: RadioButton
    private lateinit var rdPost: RadioButton
    private var key = ""
    private var numero = ""
    private var tipo = ""
    private var fecha = ""
    private var cliente = ""
    private var concepto = ""
    private var total = ""
    private var accion = ""
    private var userID = ""
    private lateinit var  database:DatabaseReference
    private val File = 1
    private val database1 = Firebase.database
    val myRef = database1.getReference("invoicesPictures")
    private lateinit var binding: ActivityAddInvoiceBinding

    private  var tipoMovSel: String = "POST"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_invoice)

        //Declarando el rdGroup y los rdButtons
        rdGroup = findViewById(R.id.rdgpPayPost)
        //Creando los rdButtons
        rdPay = findViewById(R.id.rdPayment)
        rdPost = findViewById(R.id.rdPost)

        binding = ActivityAddInvoiceBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btnSubirFoto.setOnClickListener {
            fileUpload()
        }
        inicializar()

    }

    fun rdPay_click(view: View?){
        tipoMovSel = "PAY"
    }
    fun rdPost_click(view: View?){
        tipoMovSel = "POST"
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
                        Toast.makeText(
                            this,
                            this.getString(R.string.toast_addinvoice_foto_upload), Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
    private fun inicializar() {
        edtNumero = findViewById(R.id.edtNumero)
        txtTipFact = findViewById(R.id.txtTipFact)
        edtTipo = findViewById(R.id.edtTipo)
        edtFecha = findViewById(R.id.edtFecha)
        edtCliente = findViewById(R.id.edtCliente)
        edtConcepto = findViewById(R.id.edtConcepto)
        edtTotal = findViewById(R.id.edtTotal)
        edtfoto = findViewById(R.id.edtfoto)
        rdPost = findViewById(R.id.rdPost)
        rdPay = findViewById<RadioButton>(R.id.rdPayment)
        var tipoMov : String

        // Obtención de datos que envia actividad anterior
        val datos: Bundle? = intent.getExtras()
        if (datos != null) {
            key = datos.getString("key").toString()
            edtNumero.setText(intent.getStringExtra("numero").toString())
            edtTipo.setText(intent.getStringExtra("tipo").toString())
            edtFecha.setText(intent.getStringExtra("fecha").toString())
            edtCliente.setText(intent.getStringExtra("cliente").toString())
            edtConcepto.setText(intent.getStringExtra("concepto").toString())
            edtTotal.setText(intent.getStringExtra("total").toString())
            edtfoto.setText(intent.getStringExtra("foto").toString())
            tipoMov = intent.getStringExtra("tipoMov").toString()
            //Si es una transferencia no sera capaz de cambiar el proveedor y tampoco el tipo de movimiento, porque fue hecho con otro usuario
            if (intent.getStringExtra("tipo").toString() == getString(R.string.tipo_transfer_value)){
                edtTipo.isEnabled = false
                edtTipo.inputType = InputType.TYPE_NULL
                edtTipo.setTextColor(getColor(R.color.black))
                txtTipFact.isEnabled = false
                txtTipFact.boxBackgroundColor = ContextCompat.getColor(this, R.color.colorBox_unavailable)
                if(tipoMov == "POST"){
                    rdPost.isChecked = true
                } else {
                    rdPay.isChecked = true
                }
                rdPost.isEnabled = false
                rdPay.isEnabled = false
                rdPay.setTextColor(getColor(R.color.colorDarkBlue_unavailable))
                rdPost.setTextColor(getColor(R.color.colorDarkBlue_unavailable))
            }

            //Por defecto si debe seleccionar el tipo de movimiento que se hizo
            if(tipoMov == "POST"){
                rdPost.isChecked = true
                rdPay.isChecked = false
            } else {
                rdPost.isChecked = false
                rdPay.isChecked = true
            }


            //Log.d("EDIT",tipoMov)
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
        val tipoMov: String? = tipoMovSel
        val userID: String = FirebaseAuth.getInstance().currentUser?.uid.toString()
        //Generando un codigo unico para cada factura
        val invoiceID : String = generateUniqueCode(10)
        //Si en algun momento da error por repetirse un ID crear una lectura a la bdd



        database=FirebaseDatabase.getInstance().getReference("invoices")

        // Se forma objeto producto
        val invoice = Invoice(numero, tipo, fecha, cliente, concepto, total, foto, tipoMov, userID, invoiceID)

        if (accion == "a") { //Agregar registro

            database.child(invoiceID).setValue(invoice).addOnSuccessListener {
                Toast.makeText(this,getString(R.string.toast_addinvoice_uploadSuccess), Toast.LENGTH_SHORT).show()
            }.addOnFailureListener{
                Toast.makeText(this,getString(R.string.toast_addinvoice_uploadFailed), Toast.LENGTH_SHORT).show()
            }
        } else  // Editar registro
        {
            val key = database.child("invoiceID").push().key
            if (key == null) {
                Toast.makeText(this,getString(R.string.toast_addinvoice_keyEmpty), Toast.LENGTH_SHORT).show()
            }
            val invoicesValues = invoice.toMap()
            val childUpdates = hashMapOf<String, Any>(
                "$numero" to invoicesValues
            )
            database.updateChildren(childUpdates)
            Toast.makeText(this,getString(R.string.toast_addinvoice_updateSuccessful), Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(this, getString(R.string.toast_addinvoice_sessionClosed), Toast.LENGTH_SHORT).show()

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

    fun generateUniqueCode(length: Int): String {
        val allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz0123456789"
        val sb = StringBuilder(length)
        val random = Random()

        while (sb.length < length) {
            val index = (random.nextFloat() * allowedChars.length).toInt()
            sb.append(allowedChars[index])
        }

        return sb.toString()
    }


}

