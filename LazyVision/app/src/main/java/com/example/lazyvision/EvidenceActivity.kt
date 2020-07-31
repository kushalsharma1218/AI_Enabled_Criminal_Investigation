package com.example.lazyvision

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import kotlinx.android.synthetic.main.activity_evidence.*

class EvidenceActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var email: TextView
    private lateinit var evidence: ArrayList<Evidence>
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_evidence)

        auth = FirebaseAuth.getInstance()
        email = findViewById(R.id.email)
        database = Firebase.database.reference
        val cno = intent.getStringExtra("caseno")


        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)



        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                evidence = ArrayList<Evidence>()

                val dssss: DataSnapshot = dataSnapshot.child("caseno").child(cno.toString()).child("evidence")
                val dsss:Iterable<DataSnapshot> = dssss.children

                for(dss in dsss){
                    val label = dss.child("label").getValue()
                    val imgurl = dss.child("imgurl").getValue()
                    val result = dss.child("result").getValue()
                    evidence.add(Evidence(result.toString(), imgurl.toString(), label.toString()))
                }

                val adapter = EvidenceAdapter(evidence)
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

    }

    override fun onStart() {
        super.onStart()

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if(currentUser == null){
            intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }else{
            email.setText(currentUser.email)
            val cno = intent.getStringExtra("caseno")
            caseno.text = cno
        }
    }
}
