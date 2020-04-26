package com.example.lazyvision

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.google.firebase.auth.FirebaseAuth

class Dashboard : AppCompatActivity() {

    private lateinit var email: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var logout: CardView
    private lateinit var textVision: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        auth = FirebaseAuth.getInstance()


        email = findViewById(R.id.email)
        logout = findViewById(R.id.logout)
        textVision = findViewById(R.id.textVision)


        textVision.setOnClickListener({
            intent = Intent(this, CameraActivity::class.java)
            intent.putExtra("id","textVision")
            startActivity(intent)
            finish()
        })

        logout.setOnClickListener({
            auth.signOut()
            intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        })

        val currentUser = auth.currentUser
        if (currentUser != null) {
            email.setText(currentUser.email)
        }
    }

    override fun onStart() {
        super.onStart()
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if(currentUser == null){
            intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
