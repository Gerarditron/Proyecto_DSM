package com.example.login_dsm

import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.disklrucache.DiskLruCache.Value
import com.example.login_dsm.datos.Invoice
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.w3c.dom.Text

class   MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    //Botones en el menu principal
    private lateinit var btnAddFact: ImageView
    private lateinit var btnFactHistory: ImageView
    private lateinit var lbAccountValue: TextView
    private lateinit var lbEmailLoggedIn: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnAddFact = findViewById(R.id.imgAddReceipt)
        btnFactHistory  = findViewById(R.id.imgHistoryMov)
        lbEmailLoggedIn = findViewById(R.id.lbEmailLoggedIn)
        lbAccountValue = findViewById(R.id.lbAccountValue)

        //Autenticandose con Firebase
        auth = FirebaseAuth.getInstance()
        val email = auth.currentUser
        lbEmailLoggedIn.setText(email?.email.toString())

        //Consulta a la base - solo los con el userID logeado
        var consultaValue: com.google.firebase.database.Query =  refFact.orderByChild("userID").equalTo(auth.currentUser?.uid)
        var provider: String

        consultaValue.addValueEventListener( object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                //Definiendo la variable a reemplazar
                var actualMoney: Double = 0.0

                //Leyendo cada uno de las facturas de la bdd
                for (fact in snapshot.getChildren()) {
                    val data: Invoice? = fact.getValue(Invoice::class.java)

                    //Evaluando si es una abono/cargo
                    if (data?.tipoMov.toString() == "PAY"){
                        actualMoney += data?.total?.toDouble() ?: 0.0
                    } else {
                        actualMoney -= data?.total?.toDouble() ?: 0.0
                    }
                    //Log.d("MAIN",data?.total.toString())
                }

                //Actualizando el valor del string
                lbAccountValue.text = "$ " + actualMoney.toString()
                if (actualMoney > 0){
                    lbAccountValue.setTextColor(getColor(R.color.colorGreen))
                } else  {
                    lbAccountValue.setTextColor(getColor(R.color.colorRed))
                }

            }

            override fun onCancelled(error: DatabaseError) {
                //Fallo la conexion
            }
        })


        if(auth.currentUser == null){
            val intent = Intent(this,SignInActivity::class.java)
            startActivity(intent)
        }

        //Abriendo el Historial de Movimientos/Facturas
        btnFactHistory.setOnClickListener {
            val intent = Intent(this, InvoiceActivity::class.java)
            startActivity(intent)
        }

        //Abriendo el Clasificador de Movimientos


        //Abriendo las Estadisticas


        //Abriendo el Agregar Factura
        btnAddFact.setOnClickListener {
            val intent = Intent(this, AddInvoiceActivity::class.java)
            startActivity(intent)
        }


        //Abriendo el Hacer una Transaccion



        //Abriendo la opcion extra



    }

    private fun signOut(){
        auth.signOut()
        val intent = Intent(this,SignInActivity::class.java)
        this.startActivity(intent)
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

    //Lectura al firebase
    companion object {
        var database: FirebaseDatabase = FirebaseDatabase.getInstance()
        var refFact: DatabaseReference = database.getReference("invoices")
    }


}

