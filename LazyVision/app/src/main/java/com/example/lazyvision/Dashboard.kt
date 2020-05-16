package com.example.lazyvision

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_dashboard.*

class Dashboard : AppCompatActivity() {

    private lateinit var email: TextView
    private lateinit var caseno: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var logout: CardView
    private lateinit var textVision: CardView
    private lateinit var labelVision: CardView
    private lateinit var objectVision: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val cno = intent.getStringExtra("caseno")
        auth = FirebaseAuth.getInstance()


        email = findViewById(R.id.email)
        caseno = findViewById(R.id.caseno)
        logout = findViewById(R.id.logout)
        textVision = findViewById(R.id.textVision)
        objectVision = findViewById(R.id.objectVision)
        labelVision = findViewById(R.id.labelVision)
        caseno.text = cno.toString()


        objectVision.setOnClickListener({
            intent = Intent(this, CameraActivity::class.java)
            intent.putExtra("id","objectVision")
            intent.putExtra("caseno",cno.toString())
            startActivity(intent)
            finish()
        })

        textVision.setOnClickListener({
            intent = Intent(this, CameraActivity::class.java)
            intent.putExtra("id","textVision")
            intent.putExtra("caseno",cno.toString())

            startActivity(intent)
            finish()
        })

        labelVision.setOnClickListener({
            intent = Intent(this, CameraActivity::class.java)
            intent.putExtra("id","labelVision")
            intent.putExtra("caseno",cno.toString())

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
