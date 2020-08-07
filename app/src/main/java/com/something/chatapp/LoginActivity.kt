package com.something.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private var emailText: EditText? = null
    private var passwordText: EditText? = null
    private var auth = FirebaseAuth.getInstance()
    private val users = FirebaseFirestore.getInstance().collection("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(auth.currentUser != null)
            login()

        emailText = findViewById(R.id.editText1)
        passwordText = findViewById(R.id.editText2)
    }

    fun loginClicked(view: View){

        if(emailText?.text == null || passwordText?.text == null || emailText?.text?.isEmpty()!! || passwordText?.text?.isEmpty()!!) {
            Toast.makeText(this, "Please fill email and password", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(emailText?.text.toString(), passwordText?.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    login()
                } else {
                    Toast.makeText(this, "Try registering first! Or try again later", Toast.LENGTH_SHORT).show()
                }
            }
    }
    fun startRegister(view: View){
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
    private fun login(){
        val intent = Intent(this, ChatScreenActivity::class.java)
        startActivity(intent)
        finish()
    }
}