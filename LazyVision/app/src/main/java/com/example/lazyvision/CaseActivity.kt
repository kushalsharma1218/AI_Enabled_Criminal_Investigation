package com.example.lazyvision

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class CaseActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var email: TextView
    private lateinit var cases: ArrayList<Case>

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_case)

        auth = FirebaseAuth.getInstance()
        email = findViewById(R.id.email)
        database = Firebase.database.reference

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        cases = ArrayList<Case>()


        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                cases = ArrayList<Case>()
                val dssss:DataSnapshot = dataSnapshot.child("caseno")
                val dsss:Iterable<DataSnapshot> = dssss.children

                for(dss in dsss){
                    val cno = dss.child("cno").getValue()
                    val ioff = dss.child("ioff").getValue()
                    val status = dss.child("status").getValue()
                    cases.add(Case(cno.toString(),ioff.toString(),status.toString()))

                }
                val adapter = CaseAdapter(cases)
                recyclerView.adapter = adapter
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(
                    baseContext, "Authentication With Database Failed",
                    Toast.LENGTH_SHORT
                ).show()
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
