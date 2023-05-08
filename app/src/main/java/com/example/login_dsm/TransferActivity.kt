package com.example.login_dsm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.OutcomeReceiver
import android.text.InputType
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import com.example.login_dsm.transfer.SearchemailActivity
import org.w3c.dom.Text
import java.lang.NumberFormatException

class TransferActivity : AppCompatActivity() {

    private lateinit var lbEmailReceiver: TextView
    private lateinit var lbNameReceiver: TextView
    private lateinit var btnUploadTransfImg : Button
    private lateinit var btnOpenTransfImg: Button
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transfer)

        //Definiendo los parametros inidicales y por defecto de la actividad
        btnUploadTransfImg = findViewById(R.id.btnUploadTransfImg)
        btnOpenTransfImg = findViewById(R.id.btnOpenTransfImg)
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
        //El boton para Abrir la Imagen subida va a aparecer siempre inhabilitado hasta que se suba la foto correspondiente

        //Recibiendo los valores de la pantalla anterior
        val datos: Bundle? = intent.getExtras()
        if (datos != null) {
            userUIDReceiver = intent.getStringExtra("uidReceiver").toString()
            emailReceiver = intent.getStringExtra("emailReceiver").toString()
            nameReceiver = intent.getStringExtra("nameReceiver").toString()
        }
        //Mostrando los datos del usuario
        Log.d("TRANSFER",userUIDReceiver)
        lbEmailReceiver.text = emailReceiver
        lbNameReceiver.text = nameReceiver

        //Validacion de cada uno de los campos


        btnUploadTransfImg.setOnClickListener{
            Toast.makeText(this,"SUBIR IMAGEN... ESTA EN CONSTRUCCION....",Toast.LENGTH_SHORT).show()
        }

        btnOpenTransfImg.setOnClickListener{
            Toast.makeText(this,"ABRIR IMAGEN... ESTA EN CONSTRUCCION....",Toast.LENGTH_SHORT).show()
        }

        btnAddTransf.setOnClickListener{
            Toast.makeText(this,"TRANSFERIR... ESTA EN CONSTRUCCION...",Toast.LENGTH_SHORT).show()
        }

        //Boton para cancelar la transaccion y salir
        btnCancelTransf.setOnClickListener{
            val intentSearch = Intent(this, MainActivity::class.java)
            startActivity(intentSearch)
        }

    }
}