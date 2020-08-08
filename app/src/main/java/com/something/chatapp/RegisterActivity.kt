package com.something.chatapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private var userText : EditText? = null
    private var emailText: EditText? = null
    private var passwordText: EditText? = null
    private var auth = FirebaseAuth.getInstance()
    private val users = FirebaseFirestore.getInstance().collection("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        userText = findViewById(R.id.editText3)
        emailText = findViewById(R.id.editText4)
        passwordText = findViewById(R.id.editText5)
    }

    fun registerClicked(view: View){

        val button = findViewById<Button>(R.id.registerButton)
        button.text = getString(R.string.registering)
        if(emailText?.text == null || passwordText?.text == null || emailText?.text?.isEmpty()!! || passwordText?.text?.isEmpty()!! || userText?.text?.isEmpty()!! || userText?.text?.isEmpty()!!) {
            Toast.makeText(this, "Please fill all fields first", Toast.LENGTH_SHORT).show()
            return
        }
        val data = hashMapOf(
            "email" to emailText?.text.toString(),
            "username" to userText?.text.toString()
        )
        auth.createUserWithEmailAndPassword(emailText?.text.toString(), passwordText?.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val uid = task.result?.user?.uid
                    if (uid != null) {
                        users.document(uid)
                            .set(data)
                        Toast.makeText(this, "User registered! Login now!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Register failed try again", Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun startLogin(view: View){
        finish()
    }
}