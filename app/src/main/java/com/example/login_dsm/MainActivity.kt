package com.example.login_dsm

import android.content.Context
import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.disklrucache.DiskLruCache.Value
import com.example.login_dsm.datos.Invoice
import com.example.login_dsm.datos.Users
import com.example.login_dsm.transfer.SearchemailActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import org.w3c.dom.Text

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    //Botones en el menu principal
    private lateinit var btnAddFact: ImageView
    private lateinit var btnFactHistory: ImageView
    private lateinit var lbAccountValue: TextView
    private lateinit var lbEmailLoggedIn: TextView
    private lateinit var btnMakeTransfer: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val contextMain: Context = this
        btnAddFact = findViewById(R.id.imgAddReceipt)
        btnFactHistory  = findViewById(R.id.imgHistoryMov)
        lbEmailLoggedIn = findViewById(R.id.lbEmailLoggedIn)
        lbAccountValue = findViewById(R.id.lbAccountValue)
        btnMakeTransfer = findViewById(R.id.imgMakeTransf)

        //Autenticandose con Firebase
        auth = FirebaseAuth.getInstance()
        val email = auth.currentUser?.email.toString()

        //Guardamos en la base de datos la informacion del usuario loggeado en el RealtimeDB para que funcionen las transacciones
        var userIDLogged: String = auth.currentUser?.uid.toString()
        var userDisplayName: String = auth.currentUser?.displayName.toString()
        var userEmail: String = auth.currentUser?.email.toString()

        //Declarando la clase donde ire a crear mi nuevo usuario
        val users : Users = Users(userDisplayName, userEmail, userIDLogged)
        //Agregando la referencia a los obejetos "usuarios" dentro de la base
        val databaseRef = database.reference.child("users").child(userIDLogged)
        //Creando el nuevo usuario en la RealtimeDB
        databaseRef.setValue(users).addOnCompleteListener {
            if(it.isSuccessful){
                //Toast.makeText(contextMain, getString(R.string.toast_main_userAdded),Toast.LENGTH_SHORT).show()
                Log.d("MAIN","User added to the RealtimeDB")
            }else{
                //Toast.makeText(contextMain,getString(R.string.msg_singin_commonerror),Toast.LENGTH_SHORT).show()
                Log.d("MAIN","User wasn\'t added because of an error, Try it again")
            }
        }




        //Consulta a la base - solo los con el userID que ingreso a la aplicacion
        var consultaValue: com.google.firebase.database.Query =  refFact.orderByChild("userID").equalTo(userIDLogged)

        consultaValue.addValueEventListener( object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                //Definiendo la variable a reemplazar
                var actualMoney: Double = 0.00

                //Leyendo cada uno de las facturas de la bdd
                for (fact in snapshot.getChildren()) {
                    val data: Invoice? = fact.getValue(Invoice::class.java)

                    //Evaluando si es una abono/cargo
                    if (data?.tipoMov.toString() == "PAY"){
                        actualMoney += data?.total?.toDouble() ?: 0.0
                    } else if (data?.tipoMov.toString() == "POST"){
                        actualMoney -= data?.total?.toDouble() ?: 0.0
                    }

                    Log.d("MAIN",data?.total.toString())
                }

                //Actualizando el valor del string
                lbAccountValue.text = "$ " + actualMoney.toString()
                if (actualMoney > 0){
                    lbAccountValue.setTextColor(getColor(R.color.colorGreen))
                } else if (actualMoney == 0.0){
                    lbAccountValue.text = "$ 0.00"
                    lbAccountValue.setTextColor(getColor(R.color.black))
                } else if (actualMoney < 0)  {
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
        btnMakeTransfer.setOnClickListener {
            val intent = Intent(this, SearchemailActivity::class.java)
            startActivity(intent)
        }

        //Abriendo la opcion extra


        //Declarando el string de email en su lugar
        lbEmailLoggedIn.setText(email)

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
        var refUsers: DatabaseReference = database.getReference("users")
    }


}

