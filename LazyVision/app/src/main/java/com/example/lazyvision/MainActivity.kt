package com.example.lazyvision

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var email: EditText
    private lateinit var pass: EditText
    private lateinit var login: Button
    private lateinit var noid: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        email = findViewById(R.id.email)
        pass = findViewById(R.id.password)
        login = findViewById(R.id.login)
        noid = findViewById(R.id.noid)

        noid.setOnClickListener({
            intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
            finish()
        })

        auth = FirebaseAuth.getInstance()

        login.setOnClickListener({
            if(email.text.toString().isEmpty() ||  pass.text.toString().isEmpty()){
                Toast.makeText(
                    baseContext, "Invalid Credentials.",
                    Toast.LENGTH_SHORT
                ).show()
            }else {
                auth.signInWithEmailAndPassword(email.text.toString(), pass.text.toString())
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            intent = Intent(this, Dashboard::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(
                                baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        })

    }

    override fun onStart() {
        super.onStart()
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if(currentUser != null){
            intent = Intent(this, Dashboard::class.java)
            startActivity(intent)
            finish()
        }
    }
}
