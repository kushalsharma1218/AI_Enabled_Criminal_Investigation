package com.example.lazyvision

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class SignupActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var email: EditText
    private lateinit var pass: EditText
    private lateinit var signup: Button
    private lateinit var alreadyLogin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        email = findViewById(R.id.email)
        pass = findViewById(R.id.password)

        signup = findViewById(R.id.signup)
        alreadyLogin = findViewById(R.id.haveid)

        auth = FirebaseAuth.getInstance()

        alreadyLogin.setOnClickListener({
            intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        })

        signup.setOnClickListener({
            auth.createUserWithEmailAndPassword(email.text.toString(), pass.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        })

    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser != null) run {
            intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }


}
