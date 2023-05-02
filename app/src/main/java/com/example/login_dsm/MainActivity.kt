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
import com.example.login_dsm.datos.Invoice
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    //Botones en el menu principal
    private lateinit var btnAddFact: Button
    private lateinit var btnFactHistory: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnAddFact : ImageView = findViewById<ImageView>(R.id.imgAddReceipt)
        val btnFactHistory : ImageView = findViewById<ImageView>(R.id.imgHistoryMov)
        val lbEmailLoggedIn : TextView = findViewById<TextView>(R.id.lbEmailLoggedIn)

        //Autenticandose con Firebase
        auth = FirebaseAuth.getInstance()
        val email = auth.currentUser
        lbEmailLoggedIn.setText(email?.email.toString())

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



}