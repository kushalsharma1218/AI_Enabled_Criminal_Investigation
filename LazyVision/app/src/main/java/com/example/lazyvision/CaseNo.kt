package com.example.lazyvision

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_case_no.*

@IgnoreExtraProperties
data class User(
    var username: String? = "",
    var email: String? = ""
)

class CaseNo : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var email: TextView
    private lateinit var cnox: TextView
    private lateinit var ioffx: TextView
    private lateinit var statusx: TextView


    private lateinit var database: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_case_no)

        auth = FirebaseAuth.getInstance()
        email = findViewById(R.id.email)


        database = Firebase.database.reference



        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val Cno = "cno"
                val Ioff = "ioff"
                val Status = "status"
                while(dataSnapshot.exists()){
                    val cno = dataSnapshot.child("caseno").child("c101").child("cno").getValue()
                    val ioff = dataSnapshot.child("caseno").child("c101").child("ioff").getValue()
                    val status = dataSnapshot.child("caseno").child("c101").child("status").getValue()
                    cnox = findViewById(R.id.$Cno)
                }


                cno1.text = cno.toString()
                ioff1.text = ioff.toString()
                status1.text = status.toString()


            }

            override fun onCancelled(databaseError: DatabaseError) {
                cno1.text = "Error"
            }
        }
        database.addValueEventListener(postListener)


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
