package com.example.login_dsm.transfer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.login_dsm.MainActivity
import com.example.login_dsm.R
import com.example.login_dsm.datos.Invoice
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.HashMap
import java.util.Random


class TransferActivity : AppCompatActivity() {

    private lateinit var lbEmailReceiver: TextView
    private lateinit var lbNameReceiver: TextView
    private lateinit var edtNumero: EditText
    private lateinit var edtTipo: String //Siempre se declararan como tipo TRANSFERENCIA
    private lateinit var edtFecha: EditText
    private lateinit var edtCliente: EditText
    private lateinit var edtConcepto: EditText
    private lateinit var edtTotal: EditText
    private lateinit var btnAddTransf : Button
    private lateinit var btnCancelTransf : Button
    //Valores que cambian segun el usuario
    private lateinit var tipoMovSender: String
    private lateinit var tipoMovReceiver: String
    private lateinit var userUIDSender: String
    private lateinit var userUIDReceiver: String
    private lateinit var emailReceiver: String
    private lateinit var nameReceiver: String
    private val File = 1
    private lateinit var uploadedImgUri: String
    private val fechaPattern = "^([0-2][0-9]|3[0-1])(\\/|-)(0[1-9]|1[0-2])\\2(\\d{4})\$"
    private val totalPattern = "^(\\d+(?:,\\d{1,2})?).*"
    //Base de datos
    private val  auth : FirebaseAuth = FirebaseAuth.getInstance()
    private val database : FirebaseDatabase = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transfer)

        //Definiendo los parametros inidicales y por defecto de la actividad
        edtNumero = findViewById(R.id.edtNumero)
        lbEmailReceiver = findViewById(R.id.lbEmailReceiver)
        lbNameReceiver = findViewById(R.id.lbNameReceiver)
        edtTipo = getString(R.string.tipo_transfer_value)
        edtFecha = findViewById(R.id.edtFecha)
        edtCliente = findViewById(R.id.edtCliente)
        edtConcepto = findViewById(R.id.edtConcepto)
        edtTotal = findViewById(R.id.edtTotal)
        tipoMovSender = "POST" //el que envia le aparecera com POST/Pago
        tipoMovReceiver = "PAY" //el que recibe le aparecera como PAY/Abono
        btnAddTransf = findViewById(R.id.btnAddTransf)
        btnCancelTransf = findViewById(R.id.btnCancelTransf)
        uploadedImgUri = ""
        //El boton para Abrir la Imagen subida va a aparecer siempre inhabilitado hasta que se suba la foto correspondiente
        val current = LocalDateTime.now()

        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        edtFecha.setText(current.format(formatter))

        //Recibiendo los valores de la pantalla anterior
        val datos: Bundle? = intent.getExtras()
        if (datos != null) {
            userUIDReceiver = intent.getStringExtra("uidReceiver").toString()
            emailReceiver = intent.getStringExtra("emailReceiver").toString()
            nameReceiver = intent.getStringExtra("nameReceiver").toString()
            //Mostrando los datos del usuario
            Log.d("TRANSFER",userUIDReceiver)
            lbEmailReceiver.text = emailReceiver
            lbNameReceiver.text = nameReceiver

        } else { //Hubo un error encontrando los datos del usuario
            Toast.makeText(this, "Hubo un error cargando los datos del usuario", Toast.LENGTH_SHORT).show()
            finish()
        }


        btnAddTransf.setOnClickListener{
            //Llamando a la funcion que hara la transferencias
            makeTransfer(userUIDReceiver)
            //Toast.makeText(this,"TRANSFERIR... ESTA EN CONSTRUCCION...",Toast.LENGTH_SHORT).show()
        }

        //Boton para cancelar la transaccion y salir
        btnCancelTransf.setOnClickListener{
            val intentSearch = Intent(this, MainActivity::class.java)
            startActivity(intentSearch)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        val myImg = database.getReference("invoicesPictures")

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
                        //Log.d("TRANSFER",hashMap.toString())
                        myImg.setValue(hashMap)
                        Toast.makeText(
                            this,
                            this.getString(R.string.toast_addinvoice_foto_upload),
                            Toast.LENGTH_SHORT
                        ).show()
                        //Declarandolo en la variable privada
                        uploadedImgUri = FileUri.toString()
                        //Habilitando el boton de verificar imagen

                    }
                }

            }
        }
    }
    fun makeTransfer(userUIDReceiver:String){

        //Guardamos todos los valores digitados en sus respectivas variables
        val numero: String = edtNumero?.text.toString()
        val tipo: String = getString(R.string.tipo_transfer_value)
        val fecha: String = edtFecha?.text.toString()
        val cliente: String = edtCliente?.text.toString()
        val concepto: String = edtConcepto?.text.toString()
        val total: String = edtTotal?.text.toString()
        var foto: String = uploadedImgUri
        val userID: String = FirebaseAuth.getInstance().currentUser?.uid.toString()
        //Generando un codigo unico para cada factura
        var invoiceIDSend : String = generateUniqueCode(10)
        var invoiceIDRecieve: String = generateUniqueCode(10)

        //Validacion de cada uno de los campos, en caso haya alguno mal, no hacer la transferencia
        if (foto.isEmpty()){
            foto = "" //Por defecto que no vaya nada guardado
        }
        if (invoiceIDSend.isEmpty()){
            invoiceIDSend  = generateUniqueCode(10)
        }
        if (invoiceIDRecieve.isEmpty()){
            invoiceIDRecieve  = generateUniqueCode(10)
        }
        if (invoiceIDRecieve == invoiceIDSend){
            invoiceIDSend  = generateUniqueCode(10)
        }
        if (numero.isEmpty()){
            Toast.makeText(this,getString(R.string.toast_numFact_empty), Toast.LENGTH_SHORT).show()
        } else if (fecha.isEmpty()){
            Toast.makeText(this,getString(R.string.toast_dateFact_empty), Toast.LENGTH_SHORT).show()
        } else if (!fecha.matches(fechaPattern.toRegex())){
            Toast.makeText(this,getString(R.string.toast_date_notvalid), Toast.LENGTH_SHORT).show()
        } else if (cliente.isEmpty()){
            Toast.makeText(this,getString(R.string.toast_provider_empty), Toast.LENGTH_SHORT).show()
        } else if (concepto.isEmpty()){
            Toast.makeText(this,getString(R.string.toast_description_empty), Toast.LENGTH_SHORT).show()
        } else if (total.isEmpty()){
            Toast.makeText(this,getString(R.string.toast_value_empty), Toast.LENGTH_SHORT).show()
        } else if (!total.matches(totalPattern.toRegex())){
            Toast.makeText(this,getString(R.string.toast_value_empty), Toast.LENGTH_SHORT).show()
        } else if (userID.isEmpty()){
            Toast.makeText(this, getString(R.string.msg_singin_commonerror), Toast.LENGTH_SHORT).show()
        } else {
            //Llamamos la instancia de los valores que necesitamos de la bdd
            val myInv = database.getReference("invoices")
            // Se forma objeto producto PARA EL QUE ENVIA Y OTRO PARA EL QUE RECIBE la transferencia
            val invoiceSend = Invoice(numero, tipo, fecha, cliente, concepto, total, foto, tipoMovSender, userID, invoiceIDSend)
            val invoiceReceiver = Invoice(numero, tipo, fecha, cliente, concepto, total, foto, tipoMovReceiver, userUIDReceiver, invoiceIDRecieve)
            // Agregando una factura al usuario que envia
            myInv.child(invoiceIDSend).setValue(invoiceSend).addOnSuccessListener {
                Toast.makeText(this,getString(R.string.toast_addtransfer_uploadSuccess), Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }.addOnFailureListener{
                Toast.makeText(this,getString(R.string.toast_addtransfer_uploadFailed), Toast.LENGTH_SHORT).show()
            }
            // Agregando una factura al usuario que recibe
            myInv.child(invoiceIDRecieve).setValue(invoiceReceiver).addOnSuccessListener {
                //No hay porque notificar dos veces la transferencia correcta
                //Toast.makeText(this,getString(R.string.toast_addtransfer_uploadSuccess), Toast.LENGTH_SHORT).show()
            }.addOnFailureListener{
                //Notificando si no se hizo correctamente la transferencia
                Toast.makeText(this,getString(R.string.toast_addtransfer_uploadFailed), Toast.LENGTH_SHORT).show()
            }
        }

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