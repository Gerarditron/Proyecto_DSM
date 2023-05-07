package com.example.login_dsm

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.example.login_dsm.datos.Users
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

    @SuppressLint("MissingInflatedId")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        val signUpName : EditText = findViewById(R.id.signUpName)
        val signUpEmail : EditText = findViewById(R.id.signUpEmail)
        val signUpPassword : EditText = findViewById(R.id.signUpPassword)
        val signUpCPassword : EditText = findViewById(R.id.signUpCPassword)
        val signUpPasswordLayout : TextInputLayout = findViewById(R.id.signUpPasswordLayout)
        val signUpCPasswordLayout : TextInputLayout = findViewById(R.id.signUpCPasswordLayout)
        val signUpBtn : Button = findViewById(R.id.SignUpBtn)
        val signUpProgress : ProgressBar = findViewById(R.id.SignUpProgress)

        val signInText : TextView = findViewById(R.id.SignInText)

        signInText.setOnClickListener {
            val intent = Intent(this,SignInActivity::class.java)
            startActivity(intent)
        }

        signUpBtn.setOnClickListener {
            val name = signUpName.text.toString()
            val email = signUpEmail.text.toString()
            val password = signUpPassword.text.toString()
            val cPassword = signUpCPassword.text.toString()

            signUpProgress.visibility = View.VISIBLE
            signUpPasswordLayout.isPasswordVisibilityToggleEnabled = true
            signUpCPasswordLayout.isPasswordVisibilityToggleEnabled = true

            if(name.isEmpty() || email.isEmpty() || password.isEmpty() || cPassword.isEmpty()){
                if(name.isEmpty()){
                    signUpName.error = getString(R.string.msg_singup_enterusername)
                } else if(email.isEmpty()){
                    signUpEmail.error = getString(R.string.msg_singin_enteremail)
                } else if(password.isEmpty()){
                    signUpPasswordLayout.isPasswordVisibilityToggleEnabled = false
                    signUpPassword.error = getString(R.string.msg_singin_enterpassword)
                } else if(cPassword.isEmpty()){
                    signUpCPasswordLayout.isPasswordVisibilityToggleEnabled = false
                    signUpCPassword.error = getString(R.string.msg_singup_enterpassword_again)
                }

                Toast.makeText(this,getString(R.string.msg_singin_enterdatacorrectly),Toast.LENGTH_SHORT).show()
                signUpProgress.visibility = View.GONE
            }else if (!email.matches(emailPattern.toRegex())){
                signUpProgress.visibility = View.GONE
                signUpEmail.error = getString(R.string.msg_singin_enteremail_valid)
                Toast.makeText(this,getString(R.string.msg_singin_enteremail_valid),Toast.LENGTH_SHORT).show()
            }else if (password.length < 6 ){
                signUpPasswordLayout.isPasswordVisibilityToggleEnabled = false
                signUpProgress.visibility = View.GONE
                signUpPassword.error = getString(R.string.msg_singin_enterpassword_valid)
                Toast.makeText(this,getString(R.string.msg_singin_enterpassword_valid),Toast.LENGTH_SHORT).show()
            }else if (password != cPassword){
                signUpProgress.visibility = View.GONE
                signUpCPassword.error = getString(R.string.msg_singup_enterpassword_notmatch)
            }else{
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {

                    if (it.isSuccessful){
                        //Agregando la referencia a los obejetos "usuarios" dentro de la base
                        val databaseRef = database.reference.child("users").child(auth.currentUser!!.uid)
                        //Agregando el usuario para el Realtime Database
                        val users : Users = Users(name, email, auth.currentUser!!.uid)

                        databaseRef.setValue(users).addOnCompleteListener {
                            if(it.isSuccessful){
                                val intent = Intent(this,SignInActivity::class.java)
                                startActivity(intent)
                            }else{
                                Toast.makeText(this,getString(R.string.msg_singin_commonerror),Toast.LENGTH_SHORT).show()
                            }
                        }
                    }else{
                        Toast.makeText(this,getString(R.string.msg_singin_commonerror),Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }
    }
}