package com.example.login_dsm.transfer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.login_dsm.MainActivity
import com.example.login_dsm.R
import com.example.login_dsm.databinding.ActivityAddInvoiceBinding
import com.example.login_dsm.datos.Invoice
import com.example.login_dsm.datos.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener


class SearchemailActivity: AppCompatActivity() {

    private lateinit var txtCorreo: TextView
    private lateinit var btnSearchEmail: Button
    private lateinit var btnSearchEmailCancel: Button
    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_searchemail)
        //Autenticandose con Firebase
        auth = FirebaseAuth.getInstance()
        //Declarando el texto y el boton
        txtCorreo = findViewById(R.id.txtCorreoSearch)
        btnSearchEmail = findViewById(R.id.btnSearchEmailTransfer)
        btnSearchEmailCancel = findViewById(R.id.btnSearchEmailCancel)

        var emailLogged: String = auth.currentUser?.email.toString() //Email actualmente loggeado

        btnSearchEmail.setOnClickListener{
            //Buscando el correo en la base
            val email = txtCorreo.text.toString()
            if (email == ""){
                Toast.makeText(this,getString(R.string.toast_searchEmail_emailempty),Toast.LENGTH_SHORT).show()
            } else if (!email.matches(emailPattern.toRegex())){
                Toast.makeText(this,getString(R.string.toast_searchEmail_emailnotvalid),Toast.LENGTH_SHORT).show()
            } else if (email == emailLogged){
                Toast.makeText(this,getString(R.string.toast_searchEmail_emailloggedin),Toast.LENGTH_SHORT).show()
            } else {
                searchEmail(email)
            }
        }

        btnSearchEmailCancel.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }

    fun searchEmail(email : String){
        //Declarando el contexto de afuera para mostar el Toast
        val contextMain: Context = this
        //Consulta a la base - solo los con el userID logeado
        var consultUsers: com.google.firebase.database.Query =  SearchemailActivity.refUsers.orderByChild("email").equalTo(email)

        consultUsers.addValueEventListener( object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //Leyendo cada uno de los usuarios de la bdd - En este caso solo sera uno
                for (user in snapshot.getChildren()) {
                    val data: Users? = user.getValue(Users::class.java)
                    Toast.makeText(contextMain,getString(R.string.toast_searchEmail_emailfound),Toast.LENGTH_SHORT).show()

                    Log.d("SEARCH",data?.uid.toString())
                    Log.d("SEARCH",data?.email.toString())

                }
            }

            override fun onCancelled(error: DatabaseError) {
                //Fallo la conexion
                Log.d("SEARCH",error.message)
            }
        })
    }

    companion object {
        var database: FirebaseDatabase = FirebaseDatabase.getInstance()
        var refUsers: DatabaseReference = database.getReference("users")
    }


}